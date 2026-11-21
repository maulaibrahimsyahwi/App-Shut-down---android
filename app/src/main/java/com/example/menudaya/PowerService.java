package com.example.menudaya;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

/**
 * Service aksesibilitas yang tidak membaca apa pun dari layar.
 * Tugasnya hanya satu: memanggil dialog daya sistem (Matikan / Mulai ulang),
 * sama seperti menekan lama tombol power.
 */
public class PowerService extends AccessibilityService {

    /** Diisi saat service aktif; null jika belum diaktifkan di Pengaturan. */
    static volatile PowerService instance;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    /** Menampilkan menu daya. Jeda singkat agar activity peluncur sempat tertutup. */
    void openPowerMenu() {
        handler.postDelayed(() -> performGlobalAction(GLOBAL_ACTION_POWER_DIALOG), 200);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Tidak dipakai.
    }

    @Override
    public void onInterrupt() {
        // Tidak dipakai.
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
