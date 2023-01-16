package com.kanasoft.softplay.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kanasoft.softplay.PlayerActivity;
import com.kanasoft.softplay.R;
import com.kanasoft.softplay.Utils.Functions;
import com.kanasoft.softplay.Utils.SharedPreferenceManager;
import com.kanasoft.softplay.Utils.SongList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class QueueAdapter extends BaseAdapter {

    Context context;
    PlayerActivity playerActivity;
    ArrayList<SongList> songLists;
    JSONObject object;
    int undoPosition;
    SongList undoSongList;
    SharedPreferenceManager sharedPreferenceManager;

    public QueueAdapter(Context context, ArrayList<SongList> songLists) {
        this.context = context;
        this.songLists = songLists;
        this.playerActivity = (PlayerActivity) context;
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
        this.object = sharedPreferenceManager.getMediaData();
    }

    @Override
    public int getCount() {
        return songLists.size();
    }

    @Override
    public SongList getItem(int position) {
        return songLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint({"InflateParams", "ViewHolder"})
    public View getView(int position, View convertView, ViewGroup parent) {
        String currentPath = sharedPreferenceManager.getCurrentSong();
        convertView = LayoutInflater.from(context).inflate(R.layout.queued_lists, null, false);
        TextView textView = convertView.findViewById(R.id.textView);
        RelativeLayout imageButton = convertView.findViewById(R.id.imageButton);
        SongList songList = getItem(position);
        String filePath = songList.getFilePath();
        String title = songList.getTitle();
        convertView.setTag(filePath);
        textView.setText(title);
        if(currentPath != null && currentPath.equals(filePath)){
            int onColor = ContextCompat.getColor(context, R.color.skyBlue);
            textView.setTextColor(onColor);
        }
        convertView.setOnClickListener(v -> {
            int shareIndex = sharedPreferenceManager.getShareIndex();
            String name = shareIndex == 1 ? sharedPreferenceManager.getCurrentPlaylist() :
                    shareIndex == 2 ? sharedPreferenceManager.getCurrentFolder() : null;
            playerActivity.playSong(filePath, songList, false, shareIndex, name);
        });
        imageButton.setOnClickListener(v -> {
            removeView(position, songList);
            playerActivity.recountList();
        });
        return convertView;
    }


    private void removeView(int position, SongList songList){
        undoPosition = position;
        undoSongList = songList;
        songLists.remove(position);
        songLists.add(songList);
        notifyDataSetChanged();
        playerActivity.setCuedTracks(songLists);
        playerActivity.setUndoButton();
    }

    public void undo(){
        int count = getCount();
        songLists.add(undoPosition, undoSongList);
        songLists.remove(count - 1);
        notifyDataSetChanged();
        playerActivity.hideUndoButton();
        playerActivity.setCuedTracks(songLists);
    }

    public void resetList(ArrayList<SongList> songLists, boolean scroll){
        this.songLists = songLists;
        notifyDataSetChanged();
        playerActivity.recountList();
        if(scroll)
            playerActivity.scrollGridView();
    }
}
