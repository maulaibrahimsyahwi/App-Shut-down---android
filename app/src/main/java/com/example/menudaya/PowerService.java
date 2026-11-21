package com.example.menudaya;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Service aksesibilitas yang tidak membaca apa pun dari layar.
 * Tugasnya hanya satu: memanggil dialog daya sistem (Matikan / Mulai ulang),
 * sama seperti menekan lama tombol power.
 */
public class PowerService extends AccessibilityService {

    /** Diisi saat service aktif; null jika belum diaktifkan di Pengaturan. */
    static volatile PowerService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    /**
     * Menampilkan menu daya SEKARANG (tanpa jeda), supaya perintah sudah terkirim
     * ke sistem sebelum proses aplikasi sempat dimatikan oleh penghemat baterai.
     *
     * @return true jika sistem menerima perintah
     */
    boolean bukaMenuDaya() {
        return performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
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
