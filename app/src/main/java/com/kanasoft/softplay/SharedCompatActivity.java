package com.kanasoft.softplay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.kanasoft.softplay.Utils.SharedPreferenceManager;

import org.json.JSONObject;

import java.util.Objects;

public class SharedCompatActivity extends AppCompatActivity {

    Context context;
    static JSONObject object;
    @SuppressLint("StaticFieldLeak")
    static SharedPreferenceManager sharedPreferenceManager;
    public static Handler UIHandler;
    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_player);

        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(this);
        object = sharedPreferenceManager.getMediaData();

    }
}
