package com.kanasoft.softplay.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kanasoft.softplay.PlayerActivity;
import com.kanasoft.softplay.Utils.Constants;

public class ActionReceiver extends BroadcastReceiver {
    
    String ACTION;
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_NEXT = "next";
    private final Context mContext = PlayerActivity.context;
    private PlayerActivity playerActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        playerActivity = (PlayerActivity) mContext;
        ACTION = intent.getAction();
        executeAction();
    }

    private void executeAction() {
        if(!Constants.SHOWING_DIALOG || !Constants.IS_PLAYING)
        switch (ACTION){
            case ACTION_PLAY:
            case ACTION_PAUSE:
                playerActivity.playPauseAudio();
                break;
            case ACTION_NEXT:
                playerActivity.changeMusic(1);
                break;
            case ACTION_PREV:
                playerActivity.changeMusic(-1);
                break;
        }
    }

}
