package com.kanasoft.softplay.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.kanasoft.softplay.PlayerActivity;
import com.kanasoft.softplay.Utils.Constants;

public class HeadsetReceiver extends BroadcastReceiver {

    private final Context mContext = PlayerActivity.context;
    PlayerActivity playerActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        playerActivity = (PlayerActivity) mContext;

        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null || Constants.SHOWING_DIALOG)
                return;

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = event.getKeyCode();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                    playerActivity.playPauseAudio();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_STOP)
                    playerActivity.stop();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_PLAY)
                    playerActivity.playAudio();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE)
                    playerActivity.pauseAudio();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_NEXT)
                    playerActivity.changeMusic(1);

                if(keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                    playerActivity.changeMusic(-1);

                if(keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
                    playerActivity.rewind();

                if(keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
                    playerActivity.forward();
            }
        }
    }
}
