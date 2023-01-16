package com.kanasoft.softplay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kanasoft.softplay.Interfaces.OnScanListener;
import com.kanasoft.softplay.Utils.ScannerUtil;

public class ScannerAct extends SharedCompatActivity implements OnScanListener {

    ImageView imageView;
    TextView textView;
    Button button;
    ScannerUtil scannerUtil;
    Thread thread;
    boolean scanning, finishedScanning, canGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scanning = true;
        finishedScanning = false;
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        Bundle bundle = getIntent().getExtras();
        canGoBack = bundle != null && bundle.getBoolean("canGoBack");

        button.setOnClickListener((v) -> {
            if(!finishedScanning && scanning)
                scannerUtil.cancel(false);
            if(finishedScanning){
                Intent intent = new Intent(this, PlayerActivity.class);
                finish();
                startActivity(intent);
            }
        });

        startScan();

    }

    private void startScan(){
        scannerUtil = new ScannerUtil(context, this);
        thread = new Thread(scannerUtil);
        thread.start();
    }

    @Override
    public void onScanning(int total) {
        runOnUI(() -> {
            textView.setText(String.valueOf(total));
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCancel(int total) {
        runOnUI(() -> {
            scanning = false;
            finishedScanning = true;
            imageView.setVisibility(View.INVISIBLE);
            button.setText("Go To Songs");
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onFinished(int total) {
        runOnUI(() -> {
            scanning = false;
            finishedScanning = true;
            imageView.setVisibility(View.INVISIBLE);
            button.setText("Go To Songs");
        });
    }

    @Override
    public void onBackPressed() {
        if(canGoBack) {
            scannerUtil.cancel(true);
            super.onBackPressed();
        }
    }
}