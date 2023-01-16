package com.kanasoft.softplay.Utils;

import org.json.JSONArray;

public class SongList {

    String title, filePath, artist;

    public SongList(String title, String filePath, String artist) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSearchTitle() {
        return " " + title;
    }

    public String getSearchArtist() {
        return " " + artist;
    }

    public JSONArray convertToJSONArray(){
        JSONArray array = new JSONArray();
        array.put(this.title);
        array.put(this.filePath);
        array.put(this.artist);
        return array;
    }
}

