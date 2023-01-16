package com.kanasoft.softplay.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MusicAdapter extends BaseAdapter {

    Context context;
    PlayerActivity playerActivity;
    ArrayList<SongList> songLists;
    JSONObject object;
    int exPosition, shareIndex;
    String currentPath, playlistName, folderPath;
    SharedPreferenceManager sharedPreferenceManager;

    public MusicAdapter(Context context, ArrayList<SongList> songLists) {
        this.context = context;
        this.playerActivity = (PlayerActivity) context;
        this.songLists = songLists;
        this.exPosition = -1;
        this.playlistName = null;
        this.folderPath = null;
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
        this.object = sharedPreferenceManager.getMediaData();
        this.currentPath = sharedPreferenceManager.getCurrentSong();
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
    @SuppressLint({"InflateParams", "ViewHolder", "ClickableViewAccessibility"})
    public View getView(int position, View convertView, ViewGroup parent) {
        int shrIndex = sharedPreferenceManager.getShareIndex();
        String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist(),
                playingFolder = sharedPreferenceManager.getPlayingFolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.song_lists, null, false);
        TextView textView = convertView.findViewById(R.id.songName);
        TextView textView2 = convertView.findViewById(R.id.artists);
        ImageView imageIcon = convertView.findViewById(R.id.imageIcon);
        ImageButton imageButton = convertView.findViewById(R.id.imageButton);
        SongList songList = getItem(position);
        String filePath = songList.getFilePath();
        String title = songList.getTitle();
        String artists = songList.getArtist();
        convertView.setTag(filePath);
        textView.setText(title);
        textView2.setText(artists);
        boolean highlight = currentPath != null && currentPath.equals(filePath) && shareIndex == shrIndex
                && ((shareIndex == 0) || (playingPlaylist != null && shareIndex == 1 && playingPlaylist.equals(playlistName))
                || (playingFolder != null && shareIndex == 2 && playingFolder.equals(folderPath)) || (shareIndex == 3));
        if(highlight){
            exPosition = position;
            int onColorTitle = ContextCompat.getColor(context, R.color.skyBlue);
            int onColorArtist = ContextCompat.getColor(context, R.color.skyBlueLight);
            int onIcon = R.drawable.ic_icon_on;
            imageIcon.setImageResource(onIcon);
            textView.setTextColor(onColorTitle);
            textView2.setTextColor(onColorArtist);
        }
        String s = null;
        switch (shareIndex){
            case 1:
                s = playlistName;
                break;
            case 2:
                s = folderPath;
                break;
        }
        String finalS = s;
        convertView.setOnClickListener(v -> {
            playerActivity.hideSoftKeyboard(v);
            playerActivity.resetCuedTracks(songLists);
            playerActivity.playSong(filePath, songList, true, shareIndex, finalS);
            currentPath = filePath;
        });
        imageButton.setOnClickListener(v -> {
            playerActivity.showMenuBox(songList);
        });
        return convertView;
    }

    public void setShareIndex(int shareIndex) {
        this.shareIndex = shareIndex;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void reOrder(ArrayList<SongList> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }
}
