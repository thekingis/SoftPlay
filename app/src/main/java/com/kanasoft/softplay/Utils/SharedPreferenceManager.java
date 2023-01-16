package com.kanasoft.softplay.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SharedPreferenceManager {

    Context context;
    private static final String sharedPreferenceName = "sharedPreferenceName";
    private static final String objectName = "objectName";
    private static final String currentSong = "currentSong";
    private static final String loopPreference = "loopPreference";
    private static final String songLists = "songLists";
    private static final String scanned = "scanned";
    private static final String cuedTracks = "cuedTracks";
    private static final String favourites = "favourites";
    private static final String playlists = "playlists";
    private static final String folders = "folders";
    private static final String shareIndex = "shareIndex";
    private static final String currentPlaylist = "currentPlaylist";
    private static final String playingPlaylist = "playingPlaylist";
    private static final String currentFolder = "currentFolder";
    private static final String playingFolder = "playingFolder";
    private static final String queueList = "queueList";

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    public void saveMediaData(JSONObject object){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(objectName, object.toString());
        editor.apply();
    }

    public void savePlaylist(JSONObject object){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(playlists, object.toString());
        editor.apply();
    }

    public void saveFolders(JSONObject object){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(folders, object.toString());
        editor.apply();
    }

    public void saveCurrentSong(String filePath){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(currentSong, filePath);
        editor.apply();
    }

    public void saveCurrentPlaylist(String playlistName){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(currentPlaylist, playlistName);
        editor.apply();
    }

    public void savePlayingPlaylist(String playlistName){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(playingPlaylist, playlistName);
        editor.apply();
    }

    public void saveCurrentFolder(String folderName){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(currentFolder, folderName);
        editor.apply();
    }

    public void savePlayingFolder(String folderName){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putString(playingFolder, folderName);
        editor.apply();
    }

    public void saveLoopPreference(int preference){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putInt(loopPreference, preference);
        editor.apply();
    }

    public void saveShareIndex(int index){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putInt(shareIndex, index);
        editor.apply();
    }

    public void saveSongLists(ArrayList<String> arrayList){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        String listString = TextUtils.join("<>", arrayList);
        editor.putString(songLists, listString);
        editor.apply();
    }

    public void saveFavourites(ArrayList<String> arrayList){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        String listString = TextUtils.join(",", arrayList);
        editor.putString(favourites, listString);
        editor.apply();
    }

    public void saveCuedTracks(ArrayList<SongList> arrayList) {
        try {
            JSONArray array = new JSONArray();
            for (SongList songList : arrayList) {
                String title = songList.getTitle();
                String filePath = songList.getFilePath();
                String artist = songList.getArtist();
                JSONObject object = new JSONObject();
                object.put("title", title);
                object.put("filePath", filePath);
                object.put("artist", artist);
                array.put(object);
            }
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shrdPrf.edit();
            editor.putString(cuedTracks, array.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveQueueList(ArrayList<SongList> arrayList) {
        try {
            JSONArray array = new JSONArray();
            for (SongList songList : arrayList) {
                String title = songList.getTitle();
                String filePath = songList.getFilePath();
                String artist = songList.getArtist();
                JSONObject object = new JSONObject();
                object.put("title", title);
                object.put("filePath", filePath);
                object.put("artist", artist);
                array.put(object);
            }
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shrdPrf.edit();
            editor.putString(queueList, array.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setScanned(boolean scan){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putBoolean(scanned, scan);
        editor.apply();
    }

    public boolean isScanned() {
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getBoolean(scanned, false);
    }

    public JSONObject getMediaData(){
        try {
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            String objectStr = shrdPrf.getString(objectName, null);
            if (!(objectStr == null))
                return new JSONObject(objectStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject getPlaylist(){
        try {
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            String objectStr = shrdPrf.getString(playlists, null);
            if (!(objectStr == null))
                return new JSONObject(objectStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject getFolders(){
        try {
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            String objectStr = shrdPrf.getString(folders, null);
            if (!(objectStr == null))
                return new JSONObject(objectStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public String getCurrentSong(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getString(currentSong, null);
    }

    public String getCurrentPlaylist(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getString(currentPlaylist, null);
    }

    public String getPlayingPlaylist(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getString(playingPlaylist, null);
    }

    public String getCurrentFolder(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getString(currentFolder, null);
    }

    public String getPlayingFolder(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getString(playingFolder, null);
    }

    public ArrayList<String> getSongLists(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        String listString = shrdPrf.getString(songLists, "");
        return new ArrayList<>(Arrays.asList(listString.split("<>")));
    }

    public ArrayList<String> getFavourites(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        String listString = shrdPrf.getString(favourites, "");
        return new ArrayList<>(Arrays.asList(listString.split(",")));
    }

    public ArrayList<SongList> getCuedTracks(){
        try {
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            String objectStr = shrdPrf.getString(cuedTracks, null);
            if (!(objectStr == null)) {
                ArrayList<SongList> songLists = new ArrayList<>();
                JSONArray array = new JSONArray(objectStr);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String title = object.getString("title");
                    String filePath = object.getString("filePath");
                    String artist = object.getString("artist");
                    SongList songList = new SongList(title, filePath, artist);
                    songLists.add(songList);
                }
                return songLists;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<SongList> getQueueList(){
        try {
            SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
            String objectStr = shrdPrf.getString(queueList, null);
            if (!(objectStr == null)) {
                ArrayList<SongList> songLists = new ArrayList<>();
                JSONArray array = new JSONArray(objectStr);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    String title = object.getString("title");
                    String filePath = object.getString("filePath");
                    String artist = object.getString("artist");
                    SongList songList = new SongList(title, filePath, artist);
                    songLists.add(songList);
                }
                return songLists;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLoopPreference(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getInt(loopPreference, 0);
    }

    public int getShareIndex(){
        SharedPreferences shrdPrf = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        return shrdPrf.getInt(shareIndex, 0);
    }

}
