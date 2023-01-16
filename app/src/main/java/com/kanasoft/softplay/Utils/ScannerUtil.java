package com.kanasoft.softplay.Utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.kanasoft.softplay.Interfaces.OnScanListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ScannerUtil implements Runnable {

    Context context;
    OnScanListener onScanListener;
    boolean isCancelled;
    int total;
    String[] allPath, forbiddenPaths;
    JSONObject object, folders;
    ArrayList<String> allMediaList;
    SharedPreferenceManager sharedPreferenceManager;

    public ScannerUtil(Context context, OnScanListener onScanListener){
        this.context = context;
        this.total = 0;
        this.isCancelled = false;
        this.object = new JSONObject();
        this.onScanListener = onScanListener;
        this.allMediaList = new ArrayList<>();
        this.allPath = StorageUtils.getStorageDirectories(context);
        this.sharedPreferenceManager = new SharedPreferenceManager(context);
        this.folders = sharedPreferenceManager.getFolders();
        this.forbiddenPaths = new String[]{
                "/PhoneRecord",
                "/Record"
        };
    }

    public void loadDirectoryFiles(File directory){
        if(this.isCancelled)
            return;
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (File file : fileList) {
                String filePath = file.getAbsolutePath();
                if (file.isDirectory()) {
                    boolean notForbidden = false;
                    for (String forbiddenPath : forbiddenPaths) {
                        if (filePath.contains(forbiddenPath)) {
                            notForbidden = true;
                            break;
                        }
                    }
                    if (!notForbidden)
                        loadDirectoryFiles(file);
                } else {
                    String name = file.getName().toLowerCase();
                    for (String ext : Constants.allowedExts) {
                        if (name.endsWith(ext) && !name.startsWith(".") && !StringUtils.isEmpty(name)) {
                            try {
                                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                mediaMetadataRetriever.setDataSource(filePath);
                                boolean hasAudio = Functions.hasAudio(mediaMetadataRetriever);
                                if(hasAudio) {
                                    String folderPath = file.getParent();
                                    if (!this.folders.has(folderPath))
                                        folders.put(Objects.requireNonNull(folderPath), new JSONObject());
                                    if (!this.object.has(filePath)) {
                                        String title = Functions.getTitle(context, file);
                                        String artist = Functions.getArtists(mediaMetadataRetriever);
                                        String album = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                                        String bitrate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                                        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                        String genre = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                                        JSONArray array = new JSONArray();
                                        JSONObject jsonObject = new JSONObject();
                                        JSONObject folderObj = folders.getJSONObject(Objects.requireNonNull(folderPath));
                                        jsonObject.put("title", title);
                                        jsonObject.put("artist", artist);
                                        jsonObject.put("album", album);
                                        jsonObject.put("bitrate", bitrate);
                                        jsonObject.put("durationStr", durationStr);
                                        jsonObject.put("genre", genre);
                                        array.put(title);
                                        array.put(filePath);
                                        array.put(artist);
                                        folderObj.put(filePath, array);
                                        if (this.isCancelled)
                                            return;
                                        this.folders.put(folderPath, folderObj);
                                        this.object.put(filePath, jsonObject);
                                    }
                                    this.allMediaList.add(filePath);
                                    this.total = allMediaList.size();
                                    this.onScanListener.onScanning(this.total);
                                }
                            } catch (Exception e) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for(String path: this.allPath){
            File storage = new File(path);
            loadDirectoryFiles(storage);
        }
        this.sharedPreferenceManager.saveMediaData(object);
        this.sharedPreferenceManager.saveFolders(folders);
        this.onScanListener.onFinished(this.total);
        if(this.total > 0){
            this.sharedPreferenceManager.setScanned(true);
            this.sharedPreferenceManager.saveSongLists(this.allMediaList);
        }
    }

    public void cancel(boolean ignore){
        this.isCancelled = true;
        if(!ignore) {
            this.onScanListener.onCancel(total);
            this.sharedPreferenceManager.saveMediaData(object);
            if (this.total > 0) {
                this.sharedPreferenceManager.setScanned(true);
                this.sharedPreferenceManager.saveSongLists(this.allMediaList);
            }
        }
    }

}
