package com.example.menudaya;

import android.app.Application;

/** Mencatat setiap kali proses aplikasi dimulai (untuk mendeteksi proses yang dimatikan sistem). */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Jejak.catat(this, "proses aplikasi dimulai");
    }
}
