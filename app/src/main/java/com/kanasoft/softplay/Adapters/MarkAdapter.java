package com.kanasoft.softplay.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kanasoft.softplay.PlayerActivity;
import com.kanasoft.softplay.PlaylistsFragment;
import com.kanasoft.softplay.R;
import com.kanasoft.softplay.Utils.SongList;

import java.util.ArrayList;

public class MarkAdapter extends BaseAdapter {

    Context context;
    PlayerActivity playerActivity;
    ArrayList<SongList> songLists;
    PlaylistsFragment playlistsFragment;
    int unchecked, checked;

    public MarkAdapter(Context context, ArrayList<SongList> songLists, PlaylistsFragment playlistsFragment) {
        this.context = context;
        this.songLists = songLists;
        this.playlistsFragment = playlistsFragment;
        this.playerActivity = (PlayerActivity) context;
        this.unchecked = R.drawable.ic_square;
        this.checked = R.drawable.ic_check_box;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.song_lists, null, false);
        TextView textView = convertView.findViewById(R.id.songName);
        TextView textView2 = convertView.findViewById(R.id.artists);
        ImageButton imageButton = convertView.findViewById(R.id.imageButton);
        SongList songList = getItem(position);
        String title = songList.getTitle();
        String artists = songList.getArtist();
        boolean isCheckedX = playlistsFragment.markedSongs.contains(songList);
        int iconX = isCheckedX ? checked : unchecked;
        imageButton.setImageResource(iconX);
        textView.setText(title);
        textView2.setText(artists);
        convertView.setOnClickListener(v -> {
            boolean isChecked = playlistsFragment.markedSongs.contains(songList);
            int icon = isChecked ? unchecked : checked;
            imageButton.setImageResource(icon);
            playlistsFragment.setSongMark(songList, isChecked);
        });
        imageButton.setOnClickListener(v -> {
            boolean isChecked = playlistsFragment.markedSongs.contains(songList);
            int icon = isChecked ? unchecked : checked;
            imageButton.setImageResource(icon);
            playlistsFragment.setSongMark(songList, isChecked);
        });
        return convertView;
    }

    public void reOrder(ArrayList<SongList> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }
}
