package com.kanasoft.softplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kanasoft.softplay.Utils.Constants;
import com.kanasoft.softplay.Utils.Functions;

import java.util.Timer;
import java.util.TimerTask;

public class DialogBoxActivity extends AppCompatActivity implements
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    Context context;
    ImageView imageDisc, playPauseBtn;
    TextView songName, artists, durationTextView;
    LinearLayout playPause;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    MediaMetadataRetriever mediaMetadataRetriever;
    Uri uri;
    Timer timer;
    String title, artist;
    Bitmap bitmap;
    boolean startedPlaying, hasAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_box);
        setFinishOnTouchOutside(false);
        Constants.SHOWING_DIALOG = true;

        imageDisc = findViewById(R.id.imageDisc);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        songName = findViewById(R.id.songName);
        artists = findViewById(R.id.artists);
        durationTextView = findViewById(R.id.durationTextView);
        playPause = findViewById(R.id.playPause);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setEnabled(false);

        uri = getIntent().getData();

        if(uri == null)
            return;

        context = this;
        mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(context, uri);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        hasAudio = Functions.hasAudio(mediaMetadataRetriever);

        if(!hasAudio)
            return;

        timer = new Timer();
        startedPlaying = false;
        mediaPlayer = new MediaPlayer();
        title = Functions.getTitle(context, uri);
        artist = Functions.getArtists(mediaMetadataRetriever);
        bitmap = Functions.getTrackBitmap(context, uri);
        audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);

        imageDisc.setImageBitmap(bitmap);
        songName.setText(title);
        artists.setText(artist);
        playPause.setOnClickListener(v -> playPauseAudio());

        initMusicPlayer();

    }

    private void initMusicPlayer() {
        try{
            //set player properties
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //set listeners
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepareAsync();
        } catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
    }

    private void playPauseAudio() {
        if (startedPlaying) {
            boolean isPlaying = mediaPlayer.isPlaying();
            if (isPlaying)
                mediaPlayer.pause();
            else {
                mediaPlayer.start();
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
            Constants.IS_PLAYING = !isPlaying;
            toggleButton();
        }
    }

    private void toggleButton(){
        boolean isPlaying = mediaPlayer.isPlaying();
        int icon = isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
        playPauseBtn.setImageResource(icon);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {}

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(0);
        toggleButton();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        startedPlaying = true;
        Constants.IS_PLAYING = true;
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        toggleButton();
        initializeSeekbar();
    }

    private void initializeSeekbar() {
        seekBar.setEnabled(true);
        int max = mediaPlayer.getDuration();
        String duration = Functions.convertSecondsToString(max);
        seekBar.setMax(max);
        durationTextView.setText(duration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        startTimer();
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer.isPlaying()) {
                    int currentTime = mediaPlayer.getCurrentPosition();
                    runOnUiThread(() -> seekBar.setProgress(currentTime));
                }
            }
        };
        timer.schedule(task, 0, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!(mediaPlayer == null))
            mediaPlayer.stop();
        Constants.IS_PLAYING = false;
        Constants.SHOWING_DIALOG = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!(mediaPlayer == null))
            mediaPlayer.stop();
        Constants.IS_PLAYING = false;
        Constants.SHOWING_DIALOG = false;
        finish();
    }
}