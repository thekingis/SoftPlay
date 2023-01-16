package com.kanasoft.softplay;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kanasoft.softplay.Adapters.MarkAdapter;
import com.kanasoft.softplay.Adapters.MusicAdapter;
import com.kanasoft.softplay.Adapters.PlaylistAdapter;
import com.kanasoft.softplay.Utils.Arraylist;
import com.kanasoft.softplay.Utils.SongList;

import java.util.ArrayList;

public class PlaylistsFragment extends Fragment {

    Context context;
    GridView gridView, subGridView, markGridView;
    LinearLayout addPlaylistButton, addSongButton;
    RelativeLayout emptyResult;
    TextView emptyTextView;
    Arraylist playlists;
    PlaylistAdapter playlistAdapter;
    PlayerActivity playerActivity;
    MusicAdapter musicAdapter;
    MarkAdapter markAdapter;
    boolean emptyIsVisible;
    public ArrayList<SongList> songLists, markedSongs;

    public PlaylistsFragment(Context context, Arraylist playlists, ArrayList<SongList> songLists) {
        this.context = context;
        this.playlists = playlists;
        this.songLists = songLists;
        this.emptyIsVisible = false;
        this.markedSongs = new ArrayList<>();
        this.playerActivity = (PlayerActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);
        gridView = view.findViewById(R.id.gridView);
        subGridView = view.findViewById(R.id.subGridView);
        markGridView = view.findViewById(R.id.markGridView);
        addPlaylistButton = view.findViewById(R.id.addPlaylistButton);
        addSongButton = view.findViewById(R.id.addSongButton);
        emptyResult = view.findViewById(R.id.emptyResult);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        addPlaylistButton.setOnClickListener(v -> {
            playerActivity.songListSel = null;
            playerActivity.createPlaylistsLayer.setVisibility(View.VISIBLE);
        });
        addSongButton.setOnClickListener(v -> {
            subGridView.setVisibility(View.INVISIBLE);
            addSongButton.setVisibility(View.INVISIBLE);
            markGridView.setVisibility(View.VISIBLE);
            playerActivity.markerIsOpen = true;
        });

        populatePlaylists();

        return view;
    }

    private void populatePlaylists() {
        playlistAdapter = new PlaylistAdapter(context, playlists);
        markAdapter = new MarkAdapter(context, songLists, this);
        gridView.setAdapter(playlistAdapter);
        markGridView.setAdapter(markAdapter);
        if(playlists.size() == 0)
            showEmptyLayer("No Playlist Created");
        else
            hideEmptyLayer();
    }

    public void reorderList(Arraylist playlists, String text){
        playlistAdapter.reOrder(playlists);
        if(text != null && playlists.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
    }

    public void reorderList(ArrayList<SongList> songLists, String text){
        markAdapter.reOrder(songLists);
        if(text != null && songLists.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
    }

    public void openPlaylist(ArrayList<SongList> songLists, String name, String text){
        gridView.setVisibility(View.INVISIBLE);
        addPlaylistButton.setVisibility(View.INVISIBLE);
        subGridView.setVisibility(View.VISIBLE);
        addSongButton.setVisibility(View.VISIBLE);
        if(text != null && songLists.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
        if(musicAdapter == null){
            musicAdapter = new MusicAdapter(context, songLists);
            musicAdapter.setShareIndex(1);
            musicAdapter.setPlaylistName(name);
            subGridView.setAdapter(musicAdapter);
            return;
        }
        musicAdapter.setPlaylistName(name);
        musicAdapter.reOrder(songLists);
        gridView.setVisibility(View.INVISIBLE);
        addPlaylistButton.setVisibility(View.INVISIBLE);
        subGridView.setVisibility(View.VISIBLE);
        addSongButton.setVisibility(View.VISIBLE);
    }

    public void updatePlaylist(ArrayList<SongList> songLists, String text){
        musicAdapter.reOrder(songLists);
        if(text != null && songLists.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
    }

    public void regulatePlay(String currentPath, String exPath){
        int offColorTitle = ContextCompat.getColor(context, R.color.white);
        int onColorTitle = ContextCompat.getColor(context, R.color.skyBlue);
        int offColorArtist = ContextCompat.getColor(context, R.color.asher);
        int onColorArtist = ContextCompat.getColor(context, R.color.skyBlueLight);
        int offIcon = R.drawable.ic_icon;
        int onIcon = R.drawable.ic_icon_on;
        if(!(exPath == null)) {
            LinearLayout linearLayout = subGridView.findViewWithTag(exPath);
            if (!(linearLayout == null)) {
                ImageView imageIcon = linearLayout.findViewById(R.id.imageIcon);
                TextView textView = linearLayout.findViewById(R.id.songName);
                TextView textView2 = linearLayout.findViewById(R.id.artists);
                imageIcon.setImageResource(offIcon);
                textView.setTextColor(offColorTitle);
                textView2.setTextColor(offColorArtist);
            }
        }
        LinearLayout linearLayout1 = subGridView.findViewWithTag(currentPath);
        if(!(linearLayout1 == null)) {
            ImageView imageIcon1 = linearLayout1.findViewById(R.id.imageIcon);
            TextView textView1 = linearLayout1.findViewById(R.id.songName);
            TextView textView21 = linearLayout1.findViewById(R.id.artists);
            imageIcon1.setImageResource(onIcon);
            textView1.setTextColor(onColorTitle);
            textView21.setTextColor(onColorArtist);
        }
    }

    public void revertView(){
        gridView.setVisibility(View.VISIBLE);
        addPlaylistButton.setVisibility(View.VISIBLE);
        subGridView.setVisibility(View.INVISIBLE);
        addSongButton.setVisibility(View.INVISIBLE);
    }

    public void ignoreView(){
        subGridView.setVisibility(View.VISIBLE);
        addSongButton.setVisibility(View.VISIBLE);
        markGridView.setVisibility(View.INVISIBLE);
    }

    public void setSongMark(SongList songList, boolean isChecked){
        if(isChecked)
            markedSongs.remove(songList);
        else
            markedSongs.add(songList);
        if(markedSongs.size() == 0){
            playerActivity.hideHeaderButton();
            return;
        }
        String text = "Add " + markedSongs.size() + " Song";
        if(markedSongs.size() > 1)
            text += "s";
        playerActivity.showHeaderButton(text);
    }

    public void addSongsToPlaylist(){
        playerActivity.addSongsToPlaylist(markedSongs);
        subGridView.setVisibility(View.VISIBLE);
        addSongButton.setVisibility(View.VISIBLE);
        markGridView.setVisibility(View.INVISIBLE);
    }

    private void showEmptyLayer(String text){
        emptyTextView.setText(text);
        emptyResult.setVisibility(View.VISIBLE);
        emptyIsVisible = true;
    }

    public void hideEmptyLayer(){
        emptyResult.setVisibility(View.GONE);
        emptyTextView.setText("");
        emptyIsVisible = false;
    }

}