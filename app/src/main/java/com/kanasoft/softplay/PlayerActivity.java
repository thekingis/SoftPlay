package com.kanasoft.softplay;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kanasoft.softplay.Adapters.QueueAdapter;
import com.kanasoft.softplay.Adapters.ViewPagerAdapter;
import com.kanasoft.softplay.Interfaces.MusicListener;
import com.kanasoft.softplay.Interfaces.OnCompletedListener;
import com.kanasoft.softplay.Utils.Arraylist;
import com.kanasoft.softplay.Utils.Functions;
import com.kanasoft.softplay.Utils.JsonArray;
import com.kanasoft.softplay.Services.MusicService;
import com.kanasoft.softplay.Utils.SongList;
import com.kanasoft.softplay.Utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.kanasoft.softplay.Utils.Functions.getTrackBitmap;


public class PlayerActivity extends SharedCompatActivity implements OnCompletedListener, MusicListener {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    RelativeLayout mainView, requestView, menuBox, createPlaylistsLayer, playlistsLayer, playListMenuBox;
    EditText searchBox, playlistNameBox;
    LinearLayout menuLayer, morBtn, favBtn, plyBtn, playlistMenuLayer, playLayout, playlistLayer, layout, player, playPause, playPause2, playerBox, btnLooper, minimizeBtn, prevBtn, nextBtn, cuedBtn, queueLayout, scannerBtn;
    private GridView gridView;
    private SeekBar seekBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageDisc, playerImage, playPauseBtn, playPauseBtn2, looperBtn, faviconView;
    private TextView textView, textView2, playerBoxTitle, playerBoxArtist, durationTextView, progressTextView, queueNumberTV;
    TextView closeQueue, playlistNameView, undoBtn, songNameSel, artistsSel, headerTextView;
    Button deleteBtn, createNewPlaylistBtn, createPlaylistBtn, headerBtn;
    private static String currentPath, playlistNameSel, renameText, currentPlaylist, currentFolder;
    private String[] loopToastText, menuTexts, playlistMenuTexts, headerTexts;
    private int[] tabIcons, loopToasts, menuIcons, playlistMenuIcons;
    private boolean canExit;
    private boolean playerBoxIsVisible;
    private boolean queueLayoutIsVisible;
    private boolean showPlayerBox;
    private boolean isEnabled;
    public boolean canScroll, playlistIsOpen, markerIsOpen, folderIsOpen;
    @SuppressLint("StaticFieldLeak")
    public static MusicService musicService;
    private Intent playIntent;
    public int selectedIcon, shareIndex;
    private static int currentTime, playerBoxHeight, eventY, actionTodo,
            draggedY, loopPreference, draggedMin, playIndex, queueLayoutHeight;
    private ArrayList<SongList> songLists, cuedTracks, queueList, favourites;
    private ArrayList<String> allMediaList;
    private Arraylist allPlaylists, allFolders;
    public SongList songList, songListSel;
    private Timer timer;
    private QueueAdapter queueAdapter;
    private static MusicListener musicListener;
    private static OnCompletedListener onCompletedListener;
    FavouriteFragment favouriteFragment;
    FoldersFragment foldersFragment;
    PlaylistsFragment playlistsFragment;
    SongsFragment songsFragment;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        context = this;
        musicListener = this;
        onCompletedListener = this;

        allMediaList = sharedPreferenceManager.getSongLists();

        selectedIcon = 0;
        shareIndex = sharedPreferenceManager.getShareIndex();
        canExit = false;
        isEnabled = false;
        playlistIsOpen = false;
        canScroll = true;
        queueLayoutIsVisible = false;
        playerBoxIsVisible = false;
        markerIsOpen = false;
        folderIsOpen = false;
        showPlayerBox = getIntent().getBooleanExtra("showPlayerBox", false);
        timer = new Timer();
        songLists = new ArrayList<>();
        currentPlaylist = sharedPreferenceManager.getCurrentPlaylist();
        loopPreference = sharedPreferenceManager.getLoopPreference();
        currentPath = sharedPreferenceManager.getCurrentSong();
        cuedTracks = sharedPreferenceManager.getCuedTracks();
        queueList = sharedPreferenceManager.getQueueList();
        handler = new Handler(Looper.getMainLooper());
        headerTexts = new String[]{
                "All Songs",
                "Playlists",
                "Folders",
                "Favourites"
        };
        menuTexts = new String[]{
                "Add to Favourites",
                "Add to Playlist",
                "Play Next",
                "Set as Ringtone",
                "Delete"
        };
        playlistMenuTexts = new String[]{
                "Rename Playlist",
                "Empty Playlist",
                "Delete Playlist"
        };
        menuIcons = new int[]{
                R.drawable.ic_favorite_none,
                R.drawable.ic_add_playlist,
                R.drawable.ic_play_next,
                R.drawable.ic_notification,
                R.drawable.ic_delete
        };
        playlistMenuIcons = new int[]{
                R.drawable.ic_edit,
                R.drawable.ic_empty,
                R.drawable.ic_delete
        };

        mainView = findViewById(R.id.mainView);
        plyBtn = findViewById(R.id.plyBtn);
        faviconView = findViewById(R.id.faviconView);
        favBtn = findViewById(R.id.favBtn);
        morBtn = findViewById(R.id.morBtn);
        playListMenuBox = findViewById(R.id.playListMenuBox);
        playLayout = findViewById(R.id.playout);
        headerBtn = findViewById(R.id.headerBtn);
        playlistNameView = findViewById(R.id.playlistNameView);
        playlistMenuLayer = findViewById(R.id.playlistMenuLayer);
        headerTextView = findViewById(R.id.headerTextView);
        createPlaylistsLayer = findViewById(R.id.createPlaylistsLayer);
        playlistsLayer = findViewById(R.id.playlistsLayer);
        playlistLayer = findViewById(R.id.playlistLayer);
        menuBox = findViewById(R.id.menuBox);
        playlistNameBox = findViewById(R.id.playlistNameBox);
        createNewPlaylistBtn = findViewById(R.id.createNewPlaylistBtn);
        createPlaylistBtn = findViewById(R.id.createPlaylistBtn);
        layout = findViewById(R.id.layout);
        menuLayer = findViewById(R.id.menuLayer);
        searchBox = findViewById(R.id.searchBox);
        songNameSel = findViewById(R.id.songNameSel);
        artistsSel = findViewById(R.id.artistsSel);
        playPause = findViewById(R.id.playPause);
        seekBar = findViewById(R.id.seekBar);
        undoBtn = findViewById(R.id.undoBtn);
        scannerBtn = findViewById(R.id.scannerBtn);
        playPause2 = findViewById(R.id.playPause2);
        player = findViewById(R.id.player);
        playerBox = findViewById(R.id.playerBox);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        playPauseBtn2 = findViewById(R.id.playPauseBtn2);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        deleteBtn = findViewById(R.id.deleteBtn);
        imageDisc = findViewById(R.id.imageDisc);
        playerImage = findViewById(R.id.playerImage);
        progressTextView = findViewById(R.id.progressTextView);
        durationTextView = findViewById(R.id.durationTextView);
        minimizeBtn = findViewById(R.id.minimizeBtn);
        looperBtn = findViewById(R.id.looperBtn);
        textView = findViewById(R.id.songName);
        textView2 = findViewById(R.id.artists);
        playerBoxTitle = findViewById(R.id.playerBoxTitle);
        playerBoxArtist = findViewById(R.id.playerBoxArtist);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
        cuedBtn = findViewById(R.id.cuedBtn);
        btnLooper = findViewById(R.id.btnLooper);
        queueLayout = findViewById(R.id.queueLayout);
        gridView = findViewById(R.id.gridView);
        queueNumberTV = findViewById(R.id.queueNumberTV);
        closeQueue = findViewById(R.id.closeQueue);

        tabIcons = new int[]{
                R.drawable.ic_icon,
                R.drawable.ic_library_music,
                R.drawable.ic_folder,
                R.drawable.ic_favorite
        };

        loopToasts = new int[]{
                R.drawable.ic_loop_all,
                R.drawable.ic_shuffle,
                R.drawable.ic_loop_one
        };

        loopToastText = new String[]{
                "Loop All",
                "Shuffle",
                "Loop One"
        };

        looperBtn.setBackgroundResource(loopToasts[loopPreference]);

        tabLayout.setupWithViewPager(viewPager);

        seekBar.setEnabled(musicService != null);
        loadAllSongs();
        getPlaylists();
        getFolders();
        getFavourites();
        initializePlayer();
        setupViewPager();
        setupTabIcons();
        initializePlayerBox();
        hideQueueLayout();
        listenToSeekDrag();
        initializeSeeker();
        toggleButton();
        initializeSearchBox();

        prevBtn.setOnClickListener(v -> changeMusic(-1));
        nextBtn.setOnClickListener(v -> changeMusic(1));
        btnLooper.setOnClickListener(v -> togglePreference());
        playPause.setOnClickListener(v -> playPauseAudio());
        playPause2.setOnClickListener(v -> playPauseAudio());
        player.setOnClickListener(v -> togglePlayerBoxVisibility());
        minimizeBtn.setOnClickListener(v -> togglePlayerBoxVisibility());
        playerBox.setOnClickListener(v -> {});
        queueLayout.setOnClickListener(v -> {});
        cuedBtn.setOnClickListener(v -> toggleQueueLayout());
        closeQueue.setOnClickListener(v -> toggleQueueLayout());
        undoBtn.setOnClickListener(v -> queueAdapter.undo());
        scannerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerAct.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("canGoBack", true);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        menuBox.setOnClickListener(v -> {
            menuBox.setVisibility(View.GONE);
        });
        layout.setOnClickListener(v -> {});
        menuLayer.setOnClickListener(v -> {});
        playlistsLayer.setOnClickListener(v -> {});
        createPlaylistsLayer.setOnClickListener(v -> {});
        playListMenuBox.setOnClickListener(v -> {});
        createPlaylistBtn.setOnClickListener(v -> {
            playlistsLayer.setVisibility(View.GONE);
            createPlaylistsLayer.setVisibility(View.VISIBLE);
        });
        createNewPlaylistBtn.setOnClickListener(v -> {
            createNewPlaylist();
        });
        headerBtn.setOnClickListener(v -> {
            if(selectedIcon == 1){
                playlistsFragment.addSongsToPlaylist();
            }
        });
        favBtn.setOnClickListener(v -> {
            addToFavourite(songList);
            checkFavourite(songList);
        });
        morBtn.setOnClickListener(v -> {
            showMenuBox(songList);
        });
        plyBtn.setOnClickListener(v -> {
            addToPlaylist(songList);
        });

        if(!(currentPath == null)) {
            playIntent = new Intent(this, MusicService.class);
            if (showPlayerBox)
                playIntent.putExtra("showNotification", true);
            startService(playIntent);
        }

        setupUI(mainView);

    }

    private void deletePlaylist() {
        String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist();
        JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
        playlistObj.remove(playlistNameSel);
        sharedPreferenceManager.savePlaylist(playlistObj);
        if(playingPlaylist.equals(playlistNameSel))
            sharedPreferenceManager.savePlayingPlaylist(null);
        getPlaylists();
        playlistsFragment.reorderList(allPlaylists, "No Playlist Created");
        Toast.makeText(context, "Playlist Deleted", Toast.LENGTH_SHORT).show();
    }

    private void emptyPlaylist() {
        try {
            String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist();
            JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
            playlistObj.put(playlistNameSel, new JSONObject());
            sharedPreferenceManager.savePlaylist(playlistObj);
            if(playingPlaylist.equals(playlistNameSel))
                sharedPreferenceManager.savePlayingPlaylist(null);
            getPlaylists();
            playlistsFragment.reorderList(allPlaylists, "Empty Playlist");
            Toast.makeText(context, "Playlist Emptied", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renamePlaylist() {
        try {
            String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist();
            JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
            if(playlistObj.has(renameText)){
                String text = "Playlist name already exists";
                TextView textView = requestView.findViewById(R.id.textView);
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
                return;
            }
            JSONObject playlistCtnt = playlistObj.getJSONObject(playlistNameSel);
            playlistObj.remove(playlistNameSel);
            playlistObj.put(renameText, playlistCtnt);
            sharedPreferenceManager.savePlaylist(playlistObj);
            if(playingPlaylist.equals(playlistNameSel))
                sharedPreferenceManager.savePlayingPlaylist(renameText);
            getPlaylists();
            playlistsFragment.reorderList(allPlaylists, null);
            Toast.makeText(context, "Playlist Renamed", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mainView.removeView(requestView);
        requestView = null;
    }

    private void deleteSong() {
        if(!(songListSel == null)) {
            if(songList == songListSel){
                boolean isPlaying = musicService.isPlaying();
                changeMusic(1);
                if(!isPlaying && musicService.isPlaying())
                    playPauseAudio();
            }
            String filePath = songListSel.getFilePath();
            object.remove(filePath);
            songLists.remove(songListSel);
            cuedTracks.remove(songListSel);
            // also check list from search result
            songsFragment.reorderList(songLists, "Empty Song List");
            queueAdapter.resetList(cuedTracks, false);
            sharedPreferenceManager.saveMediaData(object);
            sharedPreferenceManager.saveCuedTracks(cuedTracks);
            try {
                File file = new File(filePath);
                if (file.delete()) {
                    if (file.exists()) {
                        file.getCanonicalFile().delete();
                        if(file.exists())
                            context.deleteFile(file.getName());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("filePath", filePath);
            Toast.makeText(context, "Song Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPlaylists() {
        JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
        JSONArray namesArr = playlistObj.names();
        if(namesArr == null){
            allPlaylists = new Arraylist();
        } else {
            allPlaylists = new Arraylist(namesArr);
            Collections.sort(allPlaylists, String::compareToIgnoreCase);
        }
    }

    private void getFolders() {
        JSONObject folderObj = sharedPreferenceManager.getFolders();
        JSONArray namesArr = folderObj.names();
        if(namesArr == null){
            allFolders = new Arraylist();
        } else {
            allFolders = new Arraylist(namesArr);
            Collections.sort(allFolders, (sl1, sl2) -> Functions.getName(sl1).compareToIgnoreCase(Functions.getName(sl2)));
        }
    }

    private void getFavourites() {
        this.favourites = new ArrayList<>();
        ArrayList<String> favourites = sharedPreferenceManager.getFavourites();
        for (int x = 0; x < favourites.size(); x++) {
            String filePth = favourites.get(x);
            if(!StringUtils.isEmpty(filePth)) {
                String title, artists;
                try {
                    JSONObject jsonObject = object.getJSONObject(filePth);
                    title = jsonObject.getString("title");
                    artists = jsonObject.getString("artist");
                    SongList songList = new SongList(title, filePth, artists);
                    this.favourites.add(songList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(this.favourites, (sl1, sl2) -> sl1.getTitle().compareToIgnoreCase(sl2.getTitle()));
    }

    public void showHeaderButton(String text){
        headerBtn.setText(text);
        headerBtn.setVisibility(View.VISIBLE);
    }

    public void hideHeaderButton(){
        headerBtn.setText("");
        headerBtn.setVisibility(View.INVISIBLE);
    }

    public void openPlaylists(String name) {
        currentPlaylist = name;
        hideSoftKeyboard(mainView);
        headerTextView.setText(name);
        ArrayList<SongList> arrayList = getPlaylistSongs();
        playlistsFragment.openPlaylist(arrayList, name, "Empty Playlist");
        sharedPreferenceManager.saveCurrentPlaylist(name);
        playlistIsOpen = true;
    }

    public void openFolders(String path, String name) {
        currentFolder = path;
        hideSoftKeyboard(mainView);
        headerTextView.setText(name);
        ArrayList<SongList> arrayList = getFolderSongs();
        foldersFragment.openFolder(arrayList, path);
        sharedPreferenceManager.saveCurrentFolder(path);
        folderIsOpen = true;
    }

    private ArrayList<SongList> getPlaylistSongs(){
        ArrayList<SongList> arrayList = new ArrayList<>();
        try {
            JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
            JSONObject objectList = playlistObj.getJSONObject(currentPlaylist);
            for(int i = 0; i < objectList.length(); i++){
                String key = Objects.requireNonNull(objectList.names()).getString(i);
                JsonArray array = new JsonArray(objectList.getJSONArray(key));
                SongList songList = array.convertToSongList();
                arrayList.add(songList);
            }
            Collections.sort(arrayList, (sl1, sl2) -> sl1.getTitle().compareToIgnoreCase(sl2.getTitle()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<SongList> getFolderSongs(){
        ArrayList<SongList> arrayList = new ArrayList<>();
        try {
            JSONObject folderObj = sharedPreferenceManager.getFolders();
            JSONObject objectList = folderObj.getJSONObject(currentFolder);
            for(int i = 0; i < objectList.length(); i++){
                String key = Objects.requireNonNull(objectList.names()).getString(i);
                JsonArray array = new JsonArray(objectList.getJSONArray(key));
                SongList songList = array.convertToSongList();
                arrayList.add(songList);
            }
            Collections.sort(arrayList, (sl1, sl2) -> sl1.getTitle().compareToIgnoreCase(sl2.getTitle()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private void createNewPlaylist() {
        try {
            String playlistName = playlistNameBox.getText().toString();
            if(!playlistName.isEmpty()) {
                JSONObject playlists = sharedPreferenceManager.getPlaylist();
                String toastText = "Playlist Created";
                JSONObject objectList = new JSONObject();
                if (playlists.has(playlistName) && songListSel == null) {
                    Toast.makeText(context, "Playlist already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                playlistNameBox.setText("");
                if (playlists.has(playlistName)) {
                    objectList = playlists.getJSONObject(playlistName);
                }
                if (!(songListSel == null)) {
                    toastText = "Song added to Playlist";
                    JSONArray array = songListSel.convertToJSONArray();
                    objectList.put(songListSel.getFilePath(), array);
                }
                playlists.put(playlistName, objectList);
                sharedPreferenceManager.savePlaylist(playlists);
                getPlaylists();
                playlistsFragment.reorderList(allPlaylists, null);
                createPlaylistsLayer.setVisibility(View.GONE);
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public void addToPlaylist(String playlistName){
        try {
            JSONObject playlists = sharedPreferenceManager.getPlaylist();
            JSONArray array = songListSel.convertToJSONArray();
            JSONObject objectList = playlists.getJSONObject(playlistName);
            objectList.put(songListSel.getFilePath(), array);
            playlists.put(playlistName, objectList);
            sharedPreferenceManager.savePlaylist(playlists);
            getPlaylists();
            playlistsFragment.reorderList(allPlaylists, null);
            playlistsLayer.setVisibility(View.GONE);
            Toast.makeText(context, "Song added to Playlist", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addSongsToPlaylist(ArrayList<SongList> songs){
        try {
            String text = "Song";
            if(songs.size() > 1)
                text += "s";
            text += " added to Playlist";
            JSONObject playlists = sharedPreferenceManager.getPlaylist();
            JSONObject objectList = playlists.getJSONObject(currentPlaylist);
            for(SongList song : songs){
                String filePath = song.getFilePath();
                JSONArray array = song.convertToJSONArray();
                objectList.put(filePath, array);
            }
            playlists.put(currentPlaylist, objectList);
            sharedPreferenceManager.savePlaylist(playlists);
            getPlaylists();
            ArrayList<SongList> arrayList = getPlaylistSongs();
            playlistsFragment.reorderList(allPlaylists, null);
            playlistsFragment.updatePlaylist(arrayList, null);
            playlistsLayer.setVisibility(View.GONE);
            hideHeaderButton();
            playlistsFragment.ignoreView();
            markerIsOpen = false;
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            playlistsFragment.markedSongs = new ArrayList<>();
            searchBox.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeFromPlaylist(String filePath){
        try {
            JSONObject playlistObj = sharedPreferenceManager.getPlaylist();
            JSONObject objectList = playlistObj.getJSONObject(currentPlaylist);
            objectList.remove(filePath);
            playlistObj.put(currentPlaylist, objectList);
            sharedPreferenceManager.savePlaylist(playlistObj);
            getPlaylists();
            ArrayList<SongList> playlistSongs = getPlaylistSongs();
            playlistsFragment.updatePlaylist(playlistSongs, "Empty Playlist");
            playlistsFragment.reorderList(allPlaylists, "No Playlist Created");
            Toast.makeText(context, "Song Removed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUndoButton(){
        undoBtn.setVisibility(View.VISIBLE);
    }

    public void hideUndoButton(){
        undoBtn.setVisibility(View.INVISIBLE);
    }

    public void setUndoButton(){
        showUndoButton();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            runOnUI(this::hideUndoButton);
        }, 4000);
    }

    @SuppressLint("InflateParams")
    private void showRequestView(String text, String btnText){
        requestView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.request_view, null, false);
        TextView textView = requestView.findViewById(R.id.textView);
        Button cancelBtn = requestView.findViewById(R.id.cancelBtn);
        Button deleteBtn = requestView.findViewById(R.id.deleteBtn);
        textView.setText(text);
        deleteBtn.setText(btnText);
        requestView.setOnClickListener(v -> {});
        cancelBtn.setOnClickListener(v -> {
            mainView.removeView(requestView);
            requestView = null;
        });
        deleteBtn.setOnClickListener(v -> {
            switch (actionTodo){
                case 0:
                    deleteSong();
                    break;
                case 1:
                    deletePlaylist();
                    break;
                case 2:
                    emptyPlaylist();
                    break;
            }
            mainView.removeView(requestView);
            requestView = null;
        });
        mainView.addView(requestView);
    }

    @SuppressLint("InflateParams")
    private void showRenameView(){
        requestView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.rename_view, null, false);
        EditText editText = requestView.findViewById(R.id.editText);
        Button cancelBtn = requestView.findViewById(R.id.cancelBtn);
        Button renameBtn = requestView.findViewById(R.id.renameBtn);
        editText.setText(playlistNameSel);
        requestView.setOnClickListener(v -> {});
        cancelBtn.setOnClickListener(v -> {
            mainView.removeView(requestView);
            requestView = null;
        });
        renameBtn.setOnClickListener(v -> {
            renameText = editText.getText().toString();
            if(!renameText.isEmpty())
                renamePlaylist();
        });
        mainView.addView(requestView);
    }

    @SuppressLint("InflateParams")
    public void showMenuBox(SongList songList){
        String filePath = songList.getFilePath();
        ArrayList<String> favourites = sharedPreferenceManager.getFavourites();
        if(menuLayer.getChildCount() > 0)
            menuLayer.removeAllViews();
        for(int i = 0; i < menuIcons.length; i++){
            if(i == 2 && currentPath.equals(filePath))
                continue;
            boolean toRemove = selectedIcon == 1 && i == menuIcons.length - 1;
            int menuIcon = menuIcons[i];
            String menuText = menuTexts[i];
            if(toRemove)
                menuText = "Remove";
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.menu_button, null, false);
            TextView textView = linearLayout.findViewById(R.id.menuBtn);
            if(i == 0 && favourites.contains(filePath)){
                menuIcon = R.drawable.ic_favorite;
                menuText = "Remove from Favourites";
                ColorStateList color = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.normalRed));
                TextViewCompat.setCompoundDrawableTintList(textView, color);
            }
            textView.setText(menuText);
            textView.setCompoundDrawablesWithIntrinsicBounds(menuIcon, 0, 0, 0);
            int finalI = i;
            textView.setOnClickListener(v -> {
                switch(finalI){
                    case 0:
                        addToFavourite(songList);
                        break;
                    case 1:
                        addToPlaylist(songList);
                        break;
                    case 2:
                        playNext(songList);
                        break;
                    case 3:
                        setRingtone(songList);
                        break;
                    case 4:
                        if(toRemove)
                            removeFromPlaylist(filePath);
                        else
                            deleteSong(songList);
                        break;
                }
                menuBox.setVisibility(View.GONE);
            });
            menuLayer.addView(linearLayout);
        }
        String title = songList.getTitle();
        String artists = songList.getArtist();
        songNameSel.setText(title);
        artistsSel.setText(artists);
        menuBox.setVisibility(View.VISIBLE);
    }

    @SuppressLint("InflateParams")
    public void showPlaylistMenuBox(String name){
        if(playlistMenuLayer.getChildCount() > 0)
            playlistMenuLayer.removeAllViews();
        for(int i = 0; i < playlistMenuIcons.length; i++){
            int menuIcon = playlistMenuIcons[i];
            String menuText = playlistMenuTexts[i];
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.menu_button, null, false);
            TextView textView = linearLayout.findViewById(R.id.menuBtn);
            textView.setText(menuText);
            textView.setCompoundDrawablesWithIntrinsicBounds(menuIcon, 0, 0, 0);
            int finalI = i;
            textView.setOnClickListener(v -> {
                switch(finalI){
                    case 0:
                        renamePlaylist(name);
                        break;
                    case 1:
                        emptyPlaylist(name);
                        break;
                    case 2:
                        deletePlaylist(name);
                        break;
                }
                playListMenuBox.setVisibility(View.GONE);
            });
            playlistMenuLayer.addView(linearLayout);
        }
        playlistNameView.setText(name);
        playListMenuBox.setVisibility(View.VISIBLE);
    }

    private void renamePlaylist(String name) {
        playlistNameSel = name;
        showRenameView();
    }

    private void emptyPlaylist(String name) {
        actionTodo = 2;
        playlistNameSel = name;
        String text = "Empty " + name;
        showRequestView(text, "Empty");
    }

    private void deletePlaylist(String name) {
        actionTodo = 1;
        playlistNameSel = name;
        String text = "Delete " + name;
        showRequestView(text, "Delete");
    }

    private void addToFavourite(SongList songList) {
        String toastText, text = null, filePath = songList.getFilePath();
        ArrayList<String> favourites = sharedPreferenceManager.getFavourites();
        if(favourites.contains(filePath)) {
            text = "Empty Favourite List";
            toastText = "Song removed from Favourites";
            favourites.remove(filePath);
        } else {
            toastText = "Song added to Favourites";
            favourites.add(filePath);
        }
        sharedPreferenceManager.saveFavourites(favourites);
        getFavourites();
        checkFavourite(this.songList);
        favouriteFragment.reorderList(this.favourites, text);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("InflateParams")
    private void addToPlaylist(SongList songList) {
        songListSel = songList;
        getPlaylists();
        if(playlistLayer.getChildCount() > 0)
            playlistLayer.removeAllViews();
        for(String playlistName : allPlaylists){
            int index = allPlaylists.indexOf(playlistName);
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.playlists, null, false);
            TextView textView = linearLayout.findViewById(R.id.textView);
            textView.setText(playlistName);
            linearLayout.setOnClickListener(v -> {
                addToPlaylist(playlistName);
                playlistsLayer.setVisibility(View.GONE);
            });
            playlistLayer.addView(linearLayout, index);
        }
        playlistsLayer.setVisibility(View.VISIBLE);
    }

    private void playNext(SongList songList) {
        int next = getPlayIndex() + 1;
        cuedTracks.remove(songList);
        cuedTracks.add(next, songList);
        sharedPreferenceManager.saveCuedTracks(cuedTracks);
        queueAdapter.resetList(cuedTracks, false);
        Toast.makeText(context, "Queued to play next", Toast.LENGTH_SHORT).show();
    }

    private void setRingtone(SongList songList) {
    }

    private void deleteSong(SongList songList) {
        actionTodo = 0;
        songListSel = songList;
        String title = songList.getTitle();
        String text = "Delete " + title;
        showRequestView(text, "Delete");
    }

    private void initializeSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().toLowerCase();
                if(selectedIcon == 0) {
                    if (!searchText.isEmpty()) {
                        ArrayList<SongList> foundLists = getSongListResult(songLists, searchText);
                        songsFragment.reorderList(foundLists, "No Result Found");
                        return;
                    }
                    songsFragment.reorderList(songLists, "Empty Song List");
                }
                if(selectedIcon == 1){
                    if(markerIsOpen){
                        if (!searchText.isEmpty()){
                            ArrayList<SongList> foundLists = getSongListResult(songLists, searchText);
                            playlistsFragment.reorderList(foundLists, "No Result Found");
                            return;
                        }
                        playlistsFragment.reorderList(songLists, "Empty Song List");
                        return;
                    }
                    if(playlistIsOpen){
                        ArrayList<SongList> arrayList = getPlaylistSongs();
                        if (!searchText.isEmpty()){
                            ArrayList<SongList> foundLists = getSongListResult(arrayList, searchText);
                            playlistsFragment.updatePlaylist(foundLists, "No Result Found");
                            return;
                        }
                        playlistsFragment.updatePlaylist(arrayList, "Empty Playlist");
                        return;
                    }
                    if (!searchText.isEmpty()){
                        Arraylist foundLists = getDirListResult(allPlaylists, searchText);
                        Collections.sort(foundLists, String::compareToIgnoreCase);
                        playlistsFragment.reorderList(foundLists, "No Result Found");
                        return;
                    }
                    playlistsFragment.reorderList(allPlaylists, "No Playlist Created");
                }
                if(selectedIcon == 2){
                    if(folderIsOpen){
                        ArrayList<SongList> arrayList = getFolderSongs();
                        if (!searchText.isEmpty()){
                            ArrayList<SongList> foundLists = getSongListResult(arrayList, searchText);
                            foldersFragment.updateFolderList(foundLists, "No Result Found");
                            return;
                        }
                        foldersFragment.updateFolderList(arrayList, "No Folder Found");
                        return;
                    }
                    if (!searchText.isEmpty()){
                        Arraylist foundLists = getDirListResult(allFolders, searchText);
                        Collections.sort(foundLists, (sl1, sl2) -> Functions.getName(sl1).compareToIgnoreCase(Functions.getName(sl2)));
                        foldersFragment.reorderList(foundLists, "No Result Found");
                        return;
                    }
                    foldersFragment.reorderList(allFolders, "No Folder Found");
                }
                if(selectedIcon == 3) {
                    if (!searchText.isEmpty()) {
                        ArrayList<SongList> foundLists = getSongListResult(favourites, searchText);
                        favouriteFragment.reorderList(foundLists, "No Result Found");
                        return;
                    }
                    favouriteFragment.reorderList(favourites, "Empty Favourite List");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<SongList> getSongListResult(ArrayList<SongList> arrayList, String searchText){
        ArrayList<SongList> foundLists = new ArrayList<>(arrayList), foundArtists = new ArrayList<>(arrayList);
        foundLists.removeIf(sList -> !sList.getSearchTitle().toLowerCase().contains(searchText));
        foundArtists.removeIf(sList -> !sList.getSearchArtist().toLowerCase().contains(searchText));
        foundLists.removeAll(foundArtists);
        foundLists.addAll(foundArtists);
        Collections.sort(foundLists, (sl1, sl2) -> sl1.getTitle().compareToIgnoreCase(sl2.getTitle()));
        return foundLists;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Arraylist getDirListResult(Arraylist arrayList, String searchText){
        Arraylist foundLists = new Arraylist(arrayList), foundArtists = new Arraylist(arrayList);
        foundLists.removeIf(name -> !Functions.getName(name).toLowerCase().startsWith(searchText));
        foundArtists.removeIf(name -> !Functions.getName(name).toLowerCase().startsWith(searchText));
        foundLists.removeAll(foundArtists);
        foundLists.addAll(foundArtists);
        return foundLists;
    }

    private void toggleQueueLayout() {
        int mStop = queueLayoutIsVisible ? -queueLayoutHeight : 0;
        int mStart = !queueLayoutIsVisible ? -queueLayoutHeight : 0;
        ValueAnimator animator = ValueAnimator.ofInt(mStart, mStop);
        animator.addUpdateListener(animation -> {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) queueLayout.getLayoutParams();
            params.bottomMargin = (Integer) animation.getAnimatedValue();
            queueLayout.requestLayout();
        });
        animator.setDuration(250);
        animator.start();
        queueLayoutIsVisible = !queueLayoutIsVisible;
    }

    private void hideQueueLayout() {
        queueLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                queueLayoutHeight = queueLayout.getHeight();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) queueLayout.getLayoutParams();
                params.bottomMargin = -queueLayoutHeight;
                queueLayout.setLayoutParams(params);
                queueLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void initializeSeeker() {
        if(musicService != null){
            seekBar.setEnabled(true);
            int max = musicService.getDuration();
            seekBar.setMax(max);
            String durationTime = Functions.convertSecondsToString(max);
            durationTextView.setText(durationTime);
        }
    }

    public void changeMusic(int i) {
        playIndex += i;
        if (playIndex < 0)
            playIndex = cuedTracks.size() - 1;
        if (!(playIndex < cuedTracks.size()))
            playIndex = 0;
        songList = cuedTracks.get(playIndex);
        int shareIndex = sharedPreferenceManager.getShareIndex();
        String filePath = songList.getFilePath(),
                name = shareIndex == 1 ? sharedPreferenceManager.getCurrentPlaylist() :
                        shareIndex == 2 ? sharedPreferenceManager.getCurrentFolder() : null;
        playSong(filePath, songList, false, shareIndex, name);
    }

    public void setCuedTracks(ArrayList<SongList> songLists) {
        cuedTracks = songLists;
        sharedPreferenceManager.saveCuedTracks(cuedTracks);
    }

    private void regulatePlay(String oldPath, String filePath) {
        int offColor = ContextCompat.getColor(context, R.color.white);
        int onColor = ContextCompat.getColor(context, R.color.skyBlue);
        LinearLayout linearLayout = gridView.findViewWithTag(currentPath);
        if (!(linearLayout == null) && !oldPath.equals(filePath)) {
            TextView textView = linearLayout.findViewById(R.id.textView);
            textView.setTextColor(offColor);
        }
        LinearLayout linearLayout1 = gridView.findViewWithTag(filePath);
        if (!(linearLayout1 == null)) {
            TextView textView1 = linearLayout1.findViewById(R.id.textView);
            textView1.setTextColor(onColor);
        }
    }

    @SuppressLint("InflateParams")
    private void stackQueue(){
        int count = cuedTracks.size();
        String text = "(" + count + ")";
        queueNumberTV.setText(text);
        searchForPlayIndex();
        queueAdapter = new QueueAdapter(context, cuedTracks);
        gridView.setAdapter(queueAdapter);
        queueAdapter.notifyDataSetChanged();
        scrollGridView();
        queueLayoutHeight = queueLayout.getHeight();
        canScroll = false;
    }

    public void scrollGridView(){
        if(canScroll) {
            int s = playIndex < 3 ? 0 : playIndex - 3;
            gridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gridView.setSelection(s);
                    gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    regulatePlay(currentPath, currentPath);
                }
            });
            canScroll = false;
        }
    }

    public void recountList() {
        int count = cuedTracks.size();
        String text = "(" + count + ")";
        queueNumberTV.setText(text);
    }

    private void enableSeekBar() {
        int progress = isEnabled ? 0 : currentTime;
        int max = musicService.getDuration();
        musicService.seekTo(progress);
        seekBar.setEnabled(true);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        String progressTime = Functions.convertSecondsToString(progress);
        String durationTime = Functions.convertSecondsToString(max);
        progressTextView.setText(progressTime);
        durationTextView.setText(durationTime);
        isEnabled = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listenToSeekDrag() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    musicService.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void togglePreference() {
        loopPreference++;
        if (loopPreference > 2)
            loopPreference = 0;
        looperBtn.setBackgroundResource(loopToasts[loopPreference]);
        Toast.makeText(context, loopToastText[loopPreference], Toast.LENGTH_SHORT).show();
        sharedPreferenceManager.saveLoopPreference(loopPreference);
        if (musicService != null)
            musicService.setLooper(loopPreference == 2);
        cuedTracks = queueList;
        if (loopPreference == 1)
            Collections.shuffle(cuedTracks);
        sharedPreferenceManager.saveCuedTracks(cuedTracks);
        playIndex = getPlayIndex();
        canScroll = true;
        queueAdapter.resetList(cuedTracks, true);
    }

    private int getPlayIndex() {
        return cuedTracks.indexOf(songList);
    }

    private void togglePlayerBoxVisibility() {
        hideSoftKeyboard(mainView);
        if(queueLayoutIsVisible){
            toggleQueueLayout();
            return;
        }
        int mStop = playerBoxIsVisible ? playerBoxHeight : 0;
        int mStart = !playerBoxIsVisible ? playerBoxHeight : 0;
        ValueAnimator animator = ValueAnimator.ofInt(mStart, mStop);
        animator.addUpdateListener(animation -> {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerBox.getLayoutParams();
            params.topMargin = (Integer) animation.getAnimatedValue();
            playerBox.requestLayout();
        });
        animator.setDuration(300);
        animator.start();
        playerBoxIsVisible = !playerBoxIsVisible;
    }

    private void hidePlayerBoxVisibility(int mStart, int mStop) {
        int toMove = Math.abs(mStart - mStop);
        int ratio = toMove / playerBoxHeight;
        int duration = 300 * ratio;
        ValueAnimator animator = ValueAnimator.ofInt(mStart, mStop);
        animator.addUpdateListener(animation -> {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerBox.getLayoutParams();
            params.topMargin = (Integer) animation.getAnimatedValue();
            playerBox.requestLayout();
        });
        animator.setDuration(duration);
        animator.start();
        playerBoxIsVisible = mStop == 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializePlayerBox() {
        playerBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                playerBoxHeight = playerBox.getHeight();
                if(!showPlayerBox) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerBox.getLayoutParams();
                    params.topMargin = playerBoxHeight;
                    playerBox.setLayoutParams(params);
                } else {
                    playerBoxIsVisible = true;
                }
                playerBox.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        playerBox.setOnTouchListener((v, event) -> {
            if(queueLayoutIsVisible){
                toggleQueueLayout();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                draggedMin = 0;
                eventY = (int) event.getY();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerBox.getLayoutParams();
                int mStart = params.topMargin;
                int halfHeight = (playerBoxHeight / 2) - 400;
                int mStop = mStart > halfHeight ? playerBoxHeight : 0;
                hidePlayerBoxVisibility(mStart, mStop);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                draggedY = (int) event.getY();
                if (draggedY > eventY) {
                    int draggedM = draggedY - eventY;
                    if ((draggedM - draggedMin) > 1) {
                        draggedMin = draggedM;
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerBox.getLayoutParams();
                        params.topMargin = draggedM;
                        playerBox.setLayoutParams(params);
                    }
                }
            }
            return false;
        });
    }

    private void initializePlayer() {
        if (currentPath != null) {
            searchForPlayIndex();
            stackPlayer();
        }
    }

    public void playPauseAudio() {
        if(currentPath == null) {
            playIndex = loopPreference == 2 ? generateRandomNumber(songLists.size()) : 0;
            songList = songLists.get(playIndex);
            currentPath = songList.getFilePath();
            stackPlayer();
            playIntent = new Intent(this, MusicService.class);
            playIntent.putExtra("forcePlay", false);
            playIntent.putExtra("startPlaying", true);
            playIntent.putExtra("showNotification", true);
            startService(playIntent);
        } else {
            if (musicService.startedPlaying()) {
                boolean isPlaying = musicService.isPlaying();
                if (isPlaying)
                    musicService.pause(false);
                else
                    musicService.start();
                playIntent.putExtra("forcePlay", false);
                playIntent.putExtra("startPlaying", !isPlaying);
                playIntent.putExtra("showNotification", true);
                startService(playIntent);
            } else {
                playIntent.putExtra("forcePlay", true);
                playIntent.putExtra("startPlaying", true);
                playIntent.putExtra("showNotification", true);
                startService(playIntent);
            }
        }
        sharedPreferenceManager.saveCurrentSong(currentPath);
        toggleButton();
    }

    public void pauseAudio() {
        if (musicService.startedPlaying()) {
            boolean isPlaying = musicService.isPlaying();
            if (isPlaying) {
                musicService.pause(false);
                playIntent.putExtra("forcePlay", false);
                playIntent.putExtra("startPlaying", !isPlaying);
                playIntent.putExtra("showNotification", true);
                startService(playIntent);
            }
        }
        toggleButton();
    }

    public void playAudio() {
        boolean forcePlay = true;
        if (musicService.startedPlaying()) {
            forcePlay = false;
            boolean isPlaying = musicService.isPlaying();
            if (!isPlaying)
                musicService.start();
        }
        playIntent.putExtra("forcePlay", forcePlay);
        playIntent.putExtra("startPlaying", true);
        playIntent.putExtra("showNotification", true);
        startService(playIntent);
        toggleButton();
    }

    public void rewind(){
        int currentPosition = musicService.getCurrentPosition();
        int position = currentPosition < 10 ? 0 : currentPosition - 10;
        musicService.seekTo(position);
    }

    public void forward(){
        int currentPosition = musicService.getCurrentPosition();
        int duration = musicService.getDuration();
        int position = currentPosition > (duration + 10) ? duration : currentPosition + 10;
        musicService.seekTo(position);
    }

    public void stop(){
        if (musicService != null) {
            musicService.stop();
            playIntent.putExtra("forcePlay", false);
            playIntent.putExtra("startPlaying", false);
            playIntent.putExtra("showNotification", true);
            startService(playIntent);
            toggleButton();
        }
    }

    public static void musicCreated(MusicService mService) {
        musicService = mService;
        musicService.setPlayerActivity((PlayerActivity) context);
        musicService.setMusicListener(musicListener);
        musicService.setOnCompletedListener(onCompletedListener);
        musicService.setFilePath(currentPath);
        musicService.setLooper(loopPreference == 2);
        musicService.prepareSong();
    }

    private void searchForPlayIndex() {
        for (int i = 0; i < cuedTracks.size(); i++) {
            SongList songList1 = cuedTracks.get(i);
            String filePath = songList1.getFilePath();
            if (filePath.equals(currentPath)) {
                playIndex = i;
                songList = songList1;
                break;
            }
        }
    }

    private int generateRandomNumber(int high) {
        Random r = new Random();
        return r.nextInt(high);
    }

    private void stackPlayer() {
        if (!(currentPath == null)) {
            File file = new File(currentPath);
            if (file.exists()) {
                checkFavourite(songList);
                String title = songList.getTitle();
                String artists = songList.getArtist();
                Bitmap bitmap = getTrackBitmap(currentPath);
                textView.setText(title);
                textView2.setText(artists);
                playerBoxTitle.setText(title);
                playerBoxArtist.setText(artists);
                if (!(bitmap == null)) {
                    imageDisc.setImageBitmap(bitmap);
                    playerImage.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void checkFavourite(SongList songList){
        String filePath = songList.getFilePath();
        ArrayList<String> favourites = sharedPreferenceManager.getFavourites();
        boolean isFavourite = favourites.contains(filePath);
        int favicon = isFavourite ? R.drawable.ic_favorite_red : R.drawable.ic_favorite_none;
        faviconView.setImageResource(favicon);
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(musicService.isPlaying()) {
                    currentTime = musicService.getCurrentPosition();
                    String progressTime = Functions.convertSecondsToString(currentTime);
                    runOnUI(() -> {
                        progressTextView.setText(progressTime);
                        seekBar.setProgress(currentTime);
                    });
                }
            }
        };
        timer.schedule(task, 0, 100);
    }

    private void setupViewPager() {
        viewPager.setOffscreenPageLimit(5);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        songsFragment = new SongsFragment(context, songLists);
        playlistsFragment = new PlaylistsFragment(context, allPlaylists, songLists);
        foldersFragment = new FoldersFragment(context, allFolders);
        favouriteFragment = new FavouriteFragment(context, favourites);
        adapter.addFragment(songsFragment);
        adapter.addFragment(playlistsFragment);
        adapter.addFragment(foldersFragment);
        adapter.addFragment(favouriteFragment);
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        for (int x = 0; x < tabIcons.length; x++) {
            Objects.requireNonNull(tabLayout.getTabAt(x)).setIcon(tabIcons[x]);
        }
        TabLayout.Tab tab = tabLayout.getTabAt(selectedIcon);
        int color = ContextCompat.getColor(context, R.color.skyBlue);
        if (tab != null)
            Objects.requireNonNull(tab.getIcon()).setColorFilter(color, PorterDuff.Mode.SRC_IN);
        changeIconColor();
    }

    public void resetCuedTracks(ArrayList<SongList> songLists){
        queueList = songLists;
        cuedTracks = songLists;
        int loopPreference = sharedPreferenceManager.getLoopPreference();
        if(loopPreference == 1)
            Collections.shuffle(cuedTracks);
        sharedPreferenceManager.saveCuedTracks(cuedTracks);
        sharedPreferenceManager.saveQueueList(queueList);
        playIndex = getPlayIndex();
        canScroll = true;
        queueAdapter.resetList(cuedTracks, true);
    }

    private void changeIconColor() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                searchBox.setText("");
                TabLayout.Tab exTab = tabLayout.getTabAt(selectedIcon);
                int color = ContextCompat.getColor(context, R.color.skyBlue);
                int exColor = ContextCompat.getColor(context, R.color.blash);
                if (tab != null)
                    Objects.requireNonNull(tab.getIcon()).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                if (exTab != null)
                    Objects.requireNonNull(exTab.getIcon()).setColorFilter(exColor, PorterDuff.Mode.SRC_IN);
                selectedIcon = tabLayout.getSelectedTabPosition();
                String headerText = headerTexts[selectedIcon];
                headerTextView.setText(headerText);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadAllSongs() {
        for (int x = 0; x < allMediaList.size(); x++) {
            String filePth = allMediaList.get(x);
            String title, artists;
            try {
                JSONObject jsonObject = object.getJSONObject(filePth);
                title = jsonObject.getString("title");
                artists = jsonObject.getString("artist");
                SongList songList = new SongList(title, filePth, artists);
                songLists.add(songList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(songLists, (sl1, sl2) -> sl1.getTitle().compareToIgnoreCase(sl2.getTitle()));
        if (cuedTracks == null) {
            cuedTracks = new ArrayList<>();
            cuedTracks.addAll(songLists);
            if (loopPreference == 1)
                Collections.shuffle(cuedTracks);
            sharedPreferenceManager.saveCuedTracks(cuedTracks);
        }
        stackQueue();
    }

    public void playSong(String filePath, SongList songList1, boolean canOpenPlayer, int shareIndex, String name) {
        hideSoftKeyboard(mainView);
        String playingPlaylist = sharedPreferenceManager.getPlayingPlaylist(),
                playingFolder = sharedPreferenceManager.getPlayingFolder();
        songList = songList1;
        playIndex = getPlayIndex();
        checkFavourite(songList1);
        boolean isFromSameList = currentPath != null && this.shareIndex == shareIndex && (shareIndex == 0 ||
                (shareIndex == 1 && playingPlaylist != null && playingPlaylist.equals(name)) ||
                (shareIndex == 2 &&playingFolder != null && playingFolder.equals(name)) || shareIndex == 3);
        if (isFromSameList && musicService != null) {
            boolean isSameSong = filePath.equals(currentPath),
                    isPlaying = musicService.isPlaying();
            regulatePlay(currentPath, filePath);
            if(canOpenPlayer && isSameSong && isPlaying)
                togglePlayerBoxVisibility();
            if (isSameSong) {
                if(!isPlaying) {
                    musicService.start();
                    toggleButton();
                }
                return;
            }
        }
        regulatePlay(currentPath, filePath);
        if(shareIndex == 0)
            songsFragment.regulatePlay(filePath, currentPath);
        if(shareIndex == 1)
            playlistsFragment.regulatePlay(filePath, currentPath);
        if(shareIndex == 2)
            foldersFragment.regulatePlay(filePath, currentPath);
        if(shareIndex == 3)
            favouriteFragment.regulatePlay(filePath, currentPath);
        currentPath = filePath;
        String title = songList1.getTitle();
        String artists = songList1.getArtist();
        imageDisc.setImageBitmap(null);
        playerImage.setImageBitmap(null);
        Bitmap bitmap = getTrackBitmap(filePath);
        sharedPreferenceManager.saveCurrentSong(filePath);
        textView.setText(title);
        textView2.setText(artists);
        playerBoxTitle.setText(title);
        playerBoxArtist.setText(artists);
        imageDisc.setImageBitmap(bitmap);
        playerImage.setImageBitmap(bitmap);
        if (musicService == null) {
            playIntent = new Intent(this, MusicService.class);
            playIntent.putExtra("forcePlay", false);
            playIntent.putExtra("startPlaying", true);
            playIntent.putExtra("showNotification", true);
            startService(playIntent);
        } else {
            musicService.canPlay(true);
            musicService.setFilePath(filePath);
            playIntent.putExtra("forcePlay", false);
            playIntent.putExtra("startPlaying", true);
            playIntent.putExtra("showNotification", true);
            startService(playIntent);
            musicService.prepareSong();
        }
        toggleButton();
        switch (shareIndex){
            case 1:
                sharedPreferenceManager.savePlayingFolder(null);
                sharedPreferenceManager.savePlayingPlaylist(name);
                getPlaylists();
                break;
            case 2:
                sharedPreferenceManager.savePlayingPlaylist(null);
                sharedPreferenceManager.savePlayingFolder(name);
                getFolders();
                break;
            default:
                sharedPreferenceManager.savePlayingPlaylist(null);
                sharedPreferenceManager.savePlayingFolder(null);
                getFavourites();
                break;
        }
        if(canOpenPlayer) {
            songsFragment.reorderList(songLists, "Empty Song List");
            playlistsFragment.reorderList(allPlaylists, "No Playlist Created");
            foldersFragment.reorderList(allFolders, "No Folder Found");
            favouriteFragment.reorderList(favourites, "Empty Favourite List");
        }
        this.shareIndex = shareIndex;
        sharedPreferenceManager.saveShareIndex(shareIndex);
    }

    public void toggleButton() {
        if (musicService != null) {
            boolean isPlaying = musicService.isPlaying();
            int icon = isPlaying ? R.drawable.ic_pause_circle : R.drawable.ic_play_circle;
            playPauseBtn.setImageResource(icon);
            playPauseBtn2.setImageResource(icon);
            startTimer();
        }
    }

    @Override
    public void onBackPressed() {
        if(!(requestView == null)){
            mainView.removeView(requestView);
            requestView = null;
            return;
        }
        if(menuBox.getVisibility() == View.VISIBLE || playlistsLayer.getVisibility() == View.VISIBLE ||
                createPlaylistsLayer.getVisibility() == View.VISIBLE || playListMenuBox.getVisibility() == View.VISIBLE){
            menuBox.setVisibility(View.GONE);
            playlistsLayer.setVisibility(View.GONE);
            createPlaylistsLayer.setVisibility(View.GONE);
            playListMenuBox.setVisibility(View.GONE);
            return;
        }
        if(queueLayoutIsVisible) {
            toggleQueueLayout();
            return;
        }
        if(playerBoxIsVisible) {
            togglePlayerBoxVisibility();
            return;
        }
        if(!searchBox.getText().toString().isEmpty()){
            searchBox.setText("");
            return;
        }
        if(markerIsOpen && selectedIcon == 1){
            markerIsOpen = false;
            headerBtn.setVisibility(View.INVISIBLE);
            playlistsFragment.ignoreView();
            playlistsFragment.markedSongs = new ArrayList<>();
            return;
        }
        if(playlistIsOpen && selectedIcon == 1){
            playlistIsOpen = false;
            playlistsFragment.revertView();
            String headerText = headerTexts[selectedIcon];
            headerTextView.setText(headerText);
            if(playlistsFragment.emptyIsVisible)
                playlistsFragment.hideEmptyLayer();
            return;
        }
        if(folderIsOpen && selectedIcon == 2){
            folderIsOpen = false;
            foldersFragment.revertView();
            String headerText = headerTexts[selectedIcon];
            headerTextView.setText(headerText);
            return;
        }
        if(!(selectedIcon == 0)){
            viewPager.setCurrentItem(0, true);
            return;
        }
        if (canExit) {
            super.onBackPressed();
            return;
        }
        canExit = true;
        Toast.makeText(context, "Tap again to exit app", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(() -> canExit = false, 2000);
    }

    @Override
    public void onFinishedCompleting() {
        changeMusic(1);
    }

    @Override
    public void onFinishedPreparing(boolean startPlaying) {
        enableSeekBar();
        musicService.seekTo(0);
        playIntent.putExtra("forcePlay", false);
        playIntent.putExtra("startPlaying", startPlaying);
        playIntent.putExtra("showNotification", startPlaying);
        startService(playIntent);
    }

    @Override
    public void onMusicStart(boolean startService) {
        toggleButton();
        if(startService) {
            playIntent.putExtra("forcePlay", false);
            playIntent.putExtra("startPlaying", false);
            playIntent.putExtra("showNotification", true);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!musicService.isPlaying()){
            stopService(playIntent);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(v);
                searchBox.clearFocus();
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}