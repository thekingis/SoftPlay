package com.kanasoft.softplay.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Functions {

    @SuppressLint({"Recycle", "Range"})
    public static String getTitle(Context context, File file){
        String title = null, filePath = file.getAbsolutePath();
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(filePath);
            title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if(title == null) {
                String path = file.getCanonicalPath();
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.DISPLAY_NAME
                        },
                        MediaStore.Audio.Media.DATA + " = ?",
                        new String[]{path}, "");
                if(!(cursor == null)){
                    while (cursor.moveToNext()) {
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        if(title == null)
                            title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(title == null || StringUtils.isWhiteSpace(title)) {
            String fileName = file.getName();
            String[] names = fileName.split("\\.");
            StringBuilder name = new StringBuilder();
            for(int i = 0; i < names.length - 1; i++){
                name.append(names[i]);
            }
            title = name.toString();
        }
        return title.trim();
    }

    @SuppressLint({"Recycle", "Range"})
    public static String getTitle(Context context, Uri uri){
        String title;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, uri);
        title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(title == null) {
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst())
                        title = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (title == null || StringUtils.isWhiteSpace(title)) {
            String uriPath = uri.getPath();
            String[] paths = uriPath.split("/");
            String fileName = paths[paths.length - 1];
            String[] names = fileName.split("\\.");
            StringBuilder name = new StringBuilder();
            for(int i = 0; i < names.length - 1; i++){
                name.append(names[i]);
            }
            title = name.toString();
        }
        return title.trim();
    }

    public static String getArtists(MediaMetadataRetriever mediaMetadataRetriever){
        String artists = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if(!(artists == null) && !StringUtils.isWhiteSpace(artists) && artists.trim().length() > 0)
            return artists.trim();
        return "Unknown";
    }

    public static String getName(String path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Paths.get(path).getFileName().toString();
        }
        String[] paths = path.replace("\\", "/").split("/");
        return paths[paths.length - 1];
    }

    public static boolean hasAudio(MediaMetadataRetriever mediaMetadataRetriever){
        String hasAudioStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        return hasAudioStr != null && hasAudioStr.toLowerCase().equals("yes");
    }

    public static String convertSecondsToString(int timeInMillisecond){
        int sec = (timeInMillisecond / 1000);
        int min = sec / 60;
        int hr = sec / 3600;
        sec -= (min * 60);
        String strTime = "";

        String hrStr = hr + ":";
        if(hr < 10)
            hrStr = "0" + hrStr;
        strTime += hrStr;

        String minStr = min + ":";
        if(min < 10)
            minStr = "0" + minStr;
        strTime += minStr;

        String secStr = String.valueOf(sec);
        if(sec < 10)
            secStr = "0" + secStr;
        strTime += secStr;

        return strTime;
    }

    public static Bitmap getTrackBitmap(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(filePath);
        byte[] bytes = mediaMetadataRetriever.getEmbeddedPicture();
        if (bytes != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
        return bitmap;
    }

    public static Bitmap getTrackBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, uri);
        byte[] bytes = mediaMetadataRetriever.getEmbeddedPicture();
        if (bytes != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
        return bitmap;
    }

    public static boolean defaultThemeIsDark(Context context){
        int defaultThemeMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return defaultThemeMode == Configuration.UI_MODE_NIGHT_YES || defaultThemeMode == Configuration.UI_MODE_NIGHT_MASK;
    }

}
