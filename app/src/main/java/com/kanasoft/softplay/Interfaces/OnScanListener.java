package com.kanasoft.softplay.Interfaces;

public interface OnScanListener {
    void onScanning(int total);
    void onCancel(int total);
    void onFinished(int total);
}
