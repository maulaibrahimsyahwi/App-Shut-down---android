package com.example.menudaya;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Catatan kejadian kecil yang disimpan ke penyimpanan (bukan memori),
 * supaya tetap ada walaupun proses aplikasi dimatikan sistem.
 * Dipakai untuk mencari tahu kenapa layanan tidak tersambung.
 */
final class Jejak {

    private static final String BERKAS = "jejak";
    private static final String KUNCI = "log";
    private static final int MAKS_BARIS = 12;

    private Jejak() {
    }

    static synchronized void catat(Context context, String pesan) {
        try {
            SharedPreferences sp = context.getApplicationContext()
                    .getSharedPreferences(BERKAS, Context.MODE_PRIVATE);
            String waktu = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
            String lama = sp.getString(KUNCI, "");
            String[] baris = lama.isEmpty() ? new String[0] : lama.split("\n");

            StringBuilder sb = new StringBuilder();
            int mulai = Math.max(0, baris.length - (MAKS_BARIS - 1));
            for (int i = mulai; i < baris.length; i++) {
                sb.append(baris[i]).append('\n');
            }
            sb.append(waktu).append(' ').append(pesan);

            // commit() (sinkron), bukan apply(): proses bisa dimatikan sebentar lagi.
            sp.edit().putString(KUNCI, sb.toString()).commit();
        } catch (Throwable ignored) {
            // Pencatatan tidak boleh sampai merusak aplikasi.
        }
    }

    static synchronized String baca(Context context) {
        try {
            String isi = context.getApplicationContext()
                    .getSharedPreferences(BERKAS, Context.MODE_PRIVATE)
                    .getString(KUNCI, "");
            return isi.isEmpty() ? "(kosong)" : isi;
        } catch (Throwable t) {
            return "(gagal membaca: " + t + ")";
        }
    }
}
