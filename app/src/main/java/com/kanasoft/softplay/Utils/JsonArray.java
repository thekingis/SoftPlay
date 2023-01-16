package com.kanasoft.softplay.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonArray extends JSONArray {

    JSONArray array;

    public JsonArray(JSONArray array) {
        this.array = array;
    }

    public SongList convertToSongList() throws JSONException {
        String title = this.getString(0);
        String filePath = this.getString(1);
        String artist = this.getString(2);
        return new SongList(title, filePath, artist);
    }

    @Override
    public String getString(int index) throws JSONException {
        return array.getString(index);
    }
}
