package com.example.menudaya;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

/**
 * Service aksesibilitas yang tidak membaca apa pun dari layar.
 * Tugasnya hanya satu: memanggil dialog daya sistem (Matikan / Mulai ulang),
 * sama seperti menekan lama tombol power.
 *
 * Saat tersambung, service menampilkan notifikasi kecil (foreground) supaya
 * penghemat baterai Xiaomi tidak mematikan prosesnya di latar belakang.
 */
public class PowerService extends AccessibilityService {

    private static final String ID_SALURAN = "menu_daya";
    private static final int ID_NOTIFIKASI = 1;

    /** Diisi saat service aktif; null jika belum diaktifkan di Pengaturan. */
    static volatile PowerService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        Jejak.catat(this, "layanan TERSAMBUNG");
        jadikanForeground();
    }

    private void jadikanForeground() {
        try {
            NotificationManager nm = getSystemService(NotificationManager.class);
            NotificationChannel saluran = new NotificationChannel(
                    ID_SALURAN, "Menu Daya", NotificationManager.IMPORTANCE_LOW);
            saluran.setShowBadge(false);
            nm.createNotificationChannel(saluran);

            // Ketuk notifikasi = sama dengan mengetuk ikon aplikasi.
            PendingIntent ketuk = PendingIntent.getActivity(
                    this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

            Notification notifikasi = new Notification.Builder(this, ID_SALURAN)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentTitle("Menu Daya siap")
                    .setContentText("Ketuk untuk menampilkan menu Matikan / Mulai ulang")
                    .setContentIntent(ketuk)
                    .setOngoing(true)
                    .build();

            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(ID_NOTIFIKASI, notifikasi,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(ID_NOTIFIKASI, notifikasi);
            }
            Jejak.catat(this, "foreground: BERHASIL");
        } catch (Throwable t) {
            Jejak.catat(this, "foreground GAGAL: " + t);
        }
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
        Jejak.catat(this, "layanan diinterupsi (onInterrupt)");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        Jejak.catat(this, "layanan PUTUS (onUnbind)");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        instance = null;
        Jejak.catat(this, "layanan DIHANCURKAN (onDestroy)");
        super.onDestroy();
    }
}
