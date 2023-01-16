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
import com.kanasoft.softplay.Utils.Functions;
import com.kanasoft.softplay.Utils.SharedPreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FolderAdapter extends BaseAdapter {

    Context context;
    PlayerActivity playerActivity;
    ArrayList<String> folders;
    SharedPreferenceManager sharedPreferenceManager;
    JSONObject foldersObj;

    public FolderAdapter(Context context, Arraylist folders){
        this.context = context;
        this.playerActivity = (PlayerActivity) context;
        this.folders = folders;
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
        this.foldersObj = this.sharedPreferenceManager.getFolders();
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public String getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            String playingFolder = sharedPreferenceManager.getPlayingFolder();
            String folderPath = folders.get(position);
            String folderName = Functions.getName(folderPath);
            JSONObject folderObj = foldersObj.getJSONObject(folderPath);
            int listCount = folderObj.length();
            String countStr = listCount + " Song";
            if(listCount > 1)
                countStr += "s";
            convertView = LayoutInflater.from(context).inflate(R.layout.song_lists, null, false);
            TextView textView = convertView.findViewById(R.id.songName);
            TextView textView2 = convertView.findViewById(R.id.artists);
            ImageView imageIcon = convertView.findViewById(R.id.imageIcon);
            ImageButton imageButton = convertView.findViewById(R.id.imageButton);
            imageButton.setVisibility(View.GONE);
            boolean isPlayingFolder = playingFolder != null && playingFolder.equals(folderPath);
            int icon = isPlayingFolder ? R.drawable.ic_folder_blue : R.drawable.ic_folder;
            int titleColor = ContextCompat.getColor(context, isPlayingFolder ? R.color.skyBlue : R.color.white);
            int subColor = ContextCompat.getColor(context, isPlayingFolder ? R.color.skyBlueLight : R.color.asher);
            imageIcon.setImageResource(icon);
            textView.setText(folderName);
            textView2.setText(countStr);
            textView.setTextColor(titleColor);
            textView2.setTextColor(subColor);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageIcon.getLayoutParams();
            layoutParams.rightMargin = 20;
            imageIcon.setLayoutParams(layoutParams);
            convertView.setOnLongClickListener(v -> {
                playerActivity.showPlaylistMenuBox(folderPath);
                return true;
            });
            convertView.setOnClickListener(v -> {
                playerActivity.hideSoftKeyboard(v);
                playerActivity.openFolders(folderPath, folderName);
            });
            return convertView;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reOrder(Arraylist folders){
        this.folders = folders;
        notifyDataSetChanged();
    }
}
