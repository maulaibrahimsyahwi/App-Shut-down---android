package com.example.menudaya;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Tanpa layout. Dibuka -> langsung minta menu daya -> tutup sendiri.
 *
 * Tiga keadaan:
 * 1. Service tersambung            -> tampilkan menu daya seketika.
 * 2. Service aktif di Pengaturan, tapi belum tersambung (sering di Xiaomi)
 *                                  -> tunggu sebentar, lalu beri petunjuk.
 * 3. Service belum diaktifkan      -> arahkan ke Pengaturan Aksesibilitas.
 */
public class MainActivity extends Activity {

    private static final int MAKS_PERCOBAAN = 8;
    private static final long JEDA_MS = 400;
    private static final long TAHAN_SEBELUM_TUTUP_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private int percobaan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerService service = PowerService.instance;
        if (service != null) {
            jalankan(service);
        } else if (layananAktifDiPengaturan()) {
            // Aktif tapi belum tersambung: beri waktu sistem untuk menyambungkannya.
            coba();
        } else {
            Toast.makeText(this, R.string.toast_aktifkan_service, Toast.LENGTH_LONG).show();
            bukaPengaturanAksesibilitas();
            finish();
        }
    }

    /** Kirim perintah menu daya seketika, lalu tahan activity sebentar sebelum menutup. */
    private void jalankan(PowerService service) {
        try {
            boolean diterima = service.bukaMenuDaya();
            if (!diterima) {
                Toast.makeText(this, R.string.toast_menu_ditolak, Toast.LENGTH_LONG).show();
            }
        } catch (Throwable t) {
            Toast.makeText(this, "Gagal: " + t, Toast.LENGTH_LONG).show();
        }
        handler.postDelayed(this::finish, TAHAN_SEBELUM_TUTUP_MS);
    }

    private void coba() {
        PowerService service = PowerService.instance;
        if (service != null) {
            jalankan(service);
            return;
        }
        if (++percobaan < MAKS_PERCOBAAN) {
            handler.postDelayed(this::coba, JEDA_MS);
            return;
        }
        Toast.makeText(this, R.string.toast_service_belum_tersambung, Toast.LENGTH_LONG).show();
        bukaPengaturanAksesibilitas();
        finish();
    }

    private boolean layananAktifDiPengaturan() {
        String daftar = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (daftar == null || daftar.isEmpty()) {
            return false;
        }
        ComponentName saya = new ComponentName(this, PowerService.class);
        String panjang = saya.flattenToString();
        String pendek = saya.flattenToShortString();
        for (String item : daftar.split(":")) {
            if (item.equalsIgnoreCase(panjang) || item.equalsIgnoreCase(pendek)) {
                return true;
            }
        }
        return false;
    }

    private void bukaPengaturanAksesibilitas() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
