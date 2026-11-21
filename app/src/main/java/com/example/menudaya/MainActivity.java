package com.example.menudaya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Tanpa layout. Dibuka -> langsung minta menu daya -> langsung tutup.
 * Jika service aksesibilitas belum aktif, arahkan ke halaman pengaturannya.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerService service = PowerService.instance;
        if (service != null) {
            service.openPowerMenu();
        } else {
            Toast.makeText(this, R.string.toast_aktifkan_service, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }

        finish();
    }
}
