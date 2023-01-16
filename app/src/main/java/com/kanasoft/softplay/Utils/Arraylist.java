package com.kanasoft.softplay.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Arraylist extends ArrayList<String> {

    public Arraylist() {
    }

    public Arraylist(String[] strings) {
        this.addAll(new ArrayList(Arrays.asList(strings)));
    }

    public Arraylist(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                this.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Arraylist(Arraylist arraylist) {
        this.addAll(arraylist);
    }
}
