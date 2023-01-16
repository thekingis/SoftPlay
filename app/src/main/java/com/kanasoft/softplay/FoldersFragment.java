package com.kanasoft.softplay;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.kanasoft.softplay.Adapters.FolderAdapter;
import com.kanasoft.softplay.Adapters.MusicAdapter;
import com.kanasoft.softplay.Utils.Arraylist;
import com.kanasoft.softplay.Utils.SongList;

import java.util.ArrayList;

public class FoldersFragment extends Fragment {

    Context context;
    GridView gridView, subGridView;
    RelativeLayout emptyResult;
    TextView emptyTextView;
    Arraylist folders;
    PlayerActivity playerActivity;
    MusicAdapter musicAdapter;
    FolderAdapter folderAdapter;

    public FoldersFragment(Context context, Arraylist folders) {
        this.context = context;
        this.folders = folders;
        this.playerActivity = (PlayerActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        gridView = view.findViewById(R.id.gridView);
        subGridView = view.findViewById(R.id.subGridView);
        emptyResult = view.findViewById(R.id.emptyResult);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        populateFolders();

        return view;
    }

    private void populateFolders() {
        folderAdapter = new FolderAdapter(context, folders);
        gridView.setAdapter(folderAdapter);
        if(folders.size() == 0)
            showEmptyLayer("No Folder Found");
    }

    public void openFolder(ArrayList<SongList> songLists, String path){
        gridView.setVisibility(View.INVISIBLE);
        subGridView.setVisibility(View.VISIBLE);
        if(musicAdapter == null){
            musicAdapter = new MusicAdapter(context, songLists);
            musicAdapter.setShareIndex(2);
            musicAdapter.setFolderPath(path);
            subGridView.setAdapter(musicAdapter);
            return;
        }
        musicAdapter.setFolderPath(path);
        musicAdapter.reOrder(songLists);
        gridView.setVisibility(View.INVISIBLE);
        subGridView.setVisibility(View.VISIBLE);
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

    public void updateFolderList(ArrayList<SongList> songLists, String text){
        musicAdapter.reOrder(songLists);
        if(text != null && songLists.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
    }

    public void revertView(){
        gridView.setVisibility(View.VISIBLE);
        subGridView.setVisibility(View.INVISIBLE);
    }

    public void reorderList(Arraylist folders, String text){
        folderAdapter.reOrder(folders);
        if(text != null && folders.size() == 0)
            showEmptyLayer(text);
        else
            hideEmptyLayer();
    }

    private void showEmptyLayer(String text){
        emptyTextView.setText(text);
        emptyResult.setVisibility(View.VISIBLE);
    }

    private void hideEmptyLayer(){
        emptyResult.setVisibility(View.GONE);
        emptyTextView.setText("");
    }
}