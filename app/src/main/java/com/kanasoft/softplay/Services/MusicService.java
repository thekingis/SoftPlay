package com.kanasoft.softplay.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.kanasoft.softplay.Interfaces.MusicListener;
import com.kanasoft.softplay.Interfaces.OnCompletedListener;
import com.kanasoft.softplay.PlayerActivity;
import com.kanasoft.softplay.R;
import com.kanasoft.softplay.Receivers.ActionReceiver;
import com.kanasoft.softplay.Receivers.HeadsetReceiver;
import com.kanasoft.softplay.Utils.Functions;
import com.kanasoft.softplay.Utils.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.kanasoft.softplay.PlayerActivity.musicCreated;
import static com.kanasoft.softplay.Utils.Constants.CHANNEL_ID;
import static com.kanasoft.softplay.Utils.Functions.getTrackBitmap;

public class MusicService extends Service implements
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    Context context;
    private MediaPlayer mediaPlayer;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    String filePath;
    //notification id
    private static final int NOTIFY_ID = 1;
    private JSONObject object;
    private boolean loopOne, canPlay, startedPlaying, isPaused, wasPlaying;
    private OnCompletedListener onCompletedListener;
    private MusicListener musicListener;
    private PlayerActivity playerActivity;
    AudioManager audioManager;
    long startTime;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();
        audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);

        canPlay = false;
        isPaused = false;
        wasPlaying = false;
        startedPlaying = false;
        startTime = System.currentTimeMillis();
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        object = sharedPreferenceManager.getMediaData();
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
        musicCreated(this);

        ComponentName component = new ComponentName(this, HeadsetReceiver.class);
        IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        mediaFilter.setPriority(10000);
        HeadsetReceiver headsetReceiver = new HeadsetReceiver();
        //registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        registerReceiver(headsetReceiver, mediaFilter);
        ((AudioManager) getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(component);
    }

    public void initMusicPlayer(){
        //set player properties
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    @SuppressLint("RemoteViewLayout")
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            boolean forcePlay = intent.getBooleanExtra("forcePlay", false);
            boolean startPlaying = intent.getBooleanExtra("startPlaying", false);
            boolean showNotification = intent.getBooleanExtra("showNotification", false);
            canPlay(startPlaying);
            if(forcePlay)
                start();
            musicListener.onMusicStart(false);
            if(showNotification) {
                Intent notIntent = new Intent(this, PlayerActivity.class);
                notIntent.putExtra("showPlayerBox", true);
                notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                        notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Bitmap bitmap = getTrackBitmap(filePath);
                boolean defaultThemeIsDark = Functions.defaultThemeIsDark(context);
                String action = startPlaying || isPlaying() ? "pause" : "play";
                int gradient = defaultThemeIsDark ? R.drawable.gradient_dark : R.drawable.gradient_white;
                int gradientBG = defaultThemeIsDark ? R.drawable.gradient_bg_dark : R.drawable.gradient_bg_white;
                int icon = startPlaying || isPlaying() ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
                RemoteViews bigNotificationLayout = new RemoteViews(getPackageName(), R.layout.big_notification_content);
                bigNotificationLayout.setViewPadding(R.id.mainView, 0, 0, 0, 0);
                bigNotificationLayout.setTextViewText(R.id.songName, getSongTitle());
                bigNotificationLayout.setTextViewText(R.id.artists, getSongArtists());
                bigNotificationLayout.setImageViewResource(R.id.playPauseBtn, icon);
                if(!(bitmap == null))
                    bigNotificationLayout.setImageViewBitmap(R.id.imageBGView, bitmap);
                else
                    bigNotificationLayout.setImageViewResource(R.id.imageBGView, R.drawable.retro);
                bigNotificationLayout.setInt(R.id.viewBGLayer, "setBackgroundResource", gradient);
                bigNotificationLayout.setOnClickPendingIntent(R.id.playPause, getPendingIntent(action));
                bigNotificationLayout.setOnClickPendingIntent(R.id.prevBtn, getPendingIntent("prev"));
                bigNotificationLayout.setOnClickPendingIntent(R.id.nextBtn, getPendingIntent("next"));

                RemoteViews smallNotificationLayout = new RemoteViews(getPackageName(), R.layout.small_notification_content);
                smallNotificationLayout.setTextViewText(R.id.songName, getSongTitle());
                smallNotificationLayout.setTextViewText(R.id.artists, getSongArtists());
                smallNotificationLayout.setImageViewResource(R.id.playPauseBtn, icon);
                if(!(bitmap == null))
                    smallNotificationLayout.setImageViewBitmap(R.id.imageBGView, bitmap);
                else
                    smallNotificationLayout.setImageViewResource(R.id.imageBGView, R.drawable.retro);
                smallNotificationLayout.setInt(R.id.viewBGLayer, "setBackgroundResource", gradientBG);
                smallNotificationLayout.setOnClickPendingIntent(R.id.playPause, getPendingIntent(action));
                smallNotificationLayout.setOnClickPendingIntent(R.id.prevBtn, getPendingIntent("prev"));
                smallNotificationLayout.setOnClickPendingIntent(R.id.nextBtn, getPendingIntent("next"));

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentIntent(pendInt)
                        .setSmallIcon(R.drawable.icon)
                        .setOngoing(true)
                        .setSilent(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setWhen(startTime)
                        .setShowWhen(false)
                        .setOngoing(true)
                        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(smallNotificationLayout)
                        .setCustomBigContentView(bigNotificationLayout)
                        .build();
                startForeground(NOTIFY_ID, notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    private PendingIntent getPendingIntent(String action){
        Intent intent = new Intent(this, ActionReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(this, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public boolean startedPlaying() {
        return startedPlaying;
    }

    public void setMusicListener(MusicListener musicListener) {
        this.musicListener = musicListener;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch(focusChange){
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_NONE:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(startedPlaying && isPaused && wasPlaying) {
                    isPaused = false;
                    start();
                    musicListener.onMusicStart(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(startedPlaying && isPlaying()) {
                    isPaused = true;
                    pause(true);
                    musicListener.onMusicStart(true);
                }
                break;
        }
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.release();
        return false;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    //play a song
    public void prepareSong(){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
        } catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    private String getSongArtists(){
        String artist;
        try {
            JSONObject jsonObject = object.getJSONObject(filePath);
            artist = jsonObject.getString("artist");
        } catch (JSONException e) {
            artist = "Unknown";
        }
        return artist;
    }

    private String getSongTitle() throws JSONException {
        JSONObject jsonObject = object.getJSONObject(filePath);
        return jsonObject.getString("title");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(startedPlaying) {
            if (loopOne) {
                canPlay(true);
                prepareSong();
            } else
                onCompletedListener.onFinishedCompleting();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("STATUS", "PREPARED");
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playerActivity.initializeSeeker();
        if(canPlay) {
            mp.start();
            toggleButton();
            wasPlaying = true;
            startedPlaying = true;
        }
    }

    public void setOnCompletedListener(OnCompletedListener onCompletedListener){
        this.onCompletedListener = onCompletedListener;
    }

    public void canPlay(boolean canPlay){
        this.canPlay = canPlay;
    }

    public void setLooper(boolean loop){
        loopOne = loop;
    }

    public int getDuration(){
        try {
            return mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCurrentPosition(){
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isPlaying(){
        try {
            return mediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPaused(){
        return isPaused;
    }

    public void pause(boolean wasPlaying){
        mediaPlayer.pause();
        this.wasPlaying = wasPlaying;
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public void start(){
        mediaPlayer.start();
        wasPlaying = true;
        startedPlaying = true;
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void setPlayerActivity(PlayerActivity playerActivity) {
        this.playerActivity = playerActivity;
    }

    public void toggleButton(){
        playerActivity.toggleButton();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        stopForeground(false);
    }

}