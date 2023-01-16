package com.kanasoft.softplay.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
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
import com.kanasoft.softplay.Utils.Arraylist;
import com.kanasoft.softplay.Utils.SharedPreferenceManager;
import com.kanasoft.softplay.Utils.SongList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistAdapter extends BaseAdapter {

    Context context;
    PlayerActivity playerActivity;
    ArrayList<String> playlists;
    SharedPreferenceManager sharedPreferenceManager;

    public PlaylistAdapter(Context context, Arraylist playlists){
        this.context = context;
        this.playerActivity = (PlayerActivity) context;
        this.playlists = playlists;
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public String getItem(int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist();
            String playlistName = playlists.get(position);
            JSONObject playlistsObj = sharedPreferenceManager.getPlaylist();
            JSONObject playlistObj = playlistsObj.getJSONObject(playlistName);
            int listCount = playlistObj.length();
            String countStr = listCount + " Song";
            if(listCount > 1)
                countStr += "s";
            convertView = LayoutInflater.from(context).inflate(R.layout.song_lists, null, false);
            TextView textView = convertView.findViewById(R.id.songName);
            TextView textView2 = convertView.findViewById(R.id.artists);
            ImageView imageIcon = convertView.findViewById(R.id.imageIcon);
            ImageButton imageButton = convertView.findViewById(R.id.imageButton);
            imageButton.setVisibility(View.GONE);
            boolean isPlayingPlaylist = playingPlaylist != null && playingPlaylist.equals(playlistName);
            int icon = isPlayingPlaylist ? R.drawable.ic_playlist_blue : R.drawable.ic_playlist;
            int titleColor = ContextCompat.getColor(context, isPlayingPlaylist ? R.color.skyBlue : R.color.white);
            int subColor = ContextCompat.getColor(context, isPlayingPlaylist ? R.color.skyBlueLight : R.color.asher);
            imageIcon.setImageResource(icon);
            textView.setText(playlistName);
            textView2.setText(countStr);
            textView.setTextColor(titleColor);
            textView2.setTextColor(subColor);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageIcon.getLayoutParams();
            layoutParams.rightMargin = 20;
            imageIcon.setLayoutParams(layoutParams);
            convertView.setOnLongClickListener(v -> {
                playerActivity.showPlaylistMenuBox(playlistName);
                return true;
            });
            convertView.setOnClickListener(v -> {
                playerActivity.hideSoftKeyboard(v);
                playerActivity.openPlaylists(playlistName);
            });
            return convertView;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reOrder(Arraylist playlists){
        this.playlists = playlists;
        notifyDataSetChanged();
    }
}
