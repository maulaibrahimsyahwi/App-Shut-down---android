package com.example.menudaya;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/**
 * Tanpa layout. Dibuka -> langsung minta menu daya -> tutup sendiri.
 *
 * Jika menu daya tidak bisa ditampilkan, muncul jendela status yang menjelaskan
 * keadaannya (aktif di Aksesibilitas? tersambung? catatan kejadian terakhir).
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
        mintaIzinNotifikasi();

        PowerService service = PowerService.instance;
        boolean aktif = layananAktifDiPengaturan();
        Jejak.catat(this, "ikon diketuk: tersambung=" + (service != null)
                + ", aktifDiPengaturan=" + aktif);

        if (service != null) {
            jalankan(service);
        } else if (aktif) {
            // Aktif tapi belum tersambung: beri waktu sistem untuk menyambungkannya.
            coba();
        } else {
            tampilkanStatus();
        }
    }

    /** Kirim perintah menu daya seketika, lalu tahan activity sebentar sebelum menutup. */
    private void jalankan(PowerService service) {
        boolean diterima = false;
        try {
            diterima = service.bukaMenuDaya();
            Jejak.catat(this, "perintah menu daya: " + (diterima ? "diterima sistem" : "DITOLAK sistem"));
        } catch (Throwable t) {
            Jejak.catat(this, "perintah menu daya ERROR: " + t);
        }

        if (diterima) {
            handler.postDelayed(this::finish, TAHAN_SEBELUM_TUTUP_MS);
        } else {
            tampilkanStatus();
        }
    }

    /** Android 13+ butuh izin agar notifikasi "Menu Daya siap" bisa tampil. */
    private void mintaIzinNotifikasi() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
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
        Jejak.catat(this, "menunggu 3 detik: layanan tetap tidak tersambung");
        tampilkanStatus();
    }

    private void tampilkanStatus() {
        boolean tersambung = PowerService.instance != null;
        boolean aktif = layananAktifDiPengaturan();

        String pesan = "Menu daya belum bisa ditampilkan.\n\n"
                + "Aktif di Aksesibilitas: " + (aktif ? "YA" : "TIDAK") + "\n"
                + "Layanan tersambung: " + (tersambung ? "YA" : "TIDAK") + "\n\n"
                + "Catatan terakhir:\n" + Jejak.baca(this);

        new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                .setTitle(R.string.app_name)
                .setMessage(pesan)
                .setPositiveButton("Buka Aksesibilitas", (d, w) -> bukaPengaturanAksesibilitas())
                .setNeutralButton("Info aplikasi", (d, w) -> bukaInfoAplikasi())
                .setNegativeButton("Tutup", null)
                .setOnDismissListener(d -> finish())
                .show();
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

    private void bukaInfoAplikasi() {
        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName())));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
