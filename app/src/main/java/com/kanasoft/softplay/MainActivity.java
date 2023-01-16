package com.kanasoft.softplay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kanasoft.softplay.Utils.Constants;
import com.kanasoft.softplay.Utils.StorageUtils;
import com.kanasoft.softplay.Utils.StringUtils;

import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends SharedCompatActivity {

    TextView textView;
    LinearLayout linearLayout;
    Button yesBtn, noBtn;
    Spanned spanned;
    String[] requestPermissions;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        requestPermissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK
        };

        String text = "from <b>KanaSoft</b>";
        textView = findViewById(R.id.textView);
        linearLayout = findViewById(R.id.reqLayout);
        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        else
            spanned = Html.fromHtml(text);
        textView.setText(spanned);
        yesBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivityForResult(intent, 51);
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
        noBtn.setOnClickListener(v -> {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });

        new android.os.Handler().postDelayed(
                () -> {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionGrant()) {
                            loadNextPage();
                        } else {
                            requestPermissionGrant();
                        }
                    } else loadNextPage();
                }, 3000);

    }

    private void requestPermissionGrant() {
        ActivityCompat.requestPermissions(this, requestPermissions, 10);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkPermissionGrant() {
        int WESR = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RESR = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE),
                WAKE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE),
                pckMngr = PackageManager.PERMISSION_GRANTED;
        return WESR == pckMngr && RESR == pckMngr && WAKE == pckMngr;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissionGrant()) {
            loadNextPage();
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadNextPage() {
        boolean isScanned = sharedPreferenceManager.isScanned();
        Intent intent = isScanned ? new Intent(MainActivity.this, PlayerActivity.class) : new Intent(this, ScannerAct.class);
        finish();
        startActivity(intent);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            getWindow().setDecorFitsSystemWindows(false);
            return;
        }
        int uiOptions = decorView.getSystemUiVisibility();
        /*uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;*/
        // Hide the nav bar and status bar
        uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onBackPressed() {
    }
}