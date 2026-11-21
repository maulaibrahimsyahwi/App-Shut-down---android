# Menu Daya

Ketuk ikon -> langsung muncul menu daya sistem (Matikan / Mulai ulang),
seperti menekan lama tombol power. Selesai.

## Cara 1: Android Studio (paling mudah)
1. Buka Android Studio -> Open -> pilih folder MenuDaya ini.
2. Tunggu Gradle sync selesai.
3. Menu Build -> Build Bundle(s) / APK(s) -> Build APK(s).
4. APK ada di app/build/outputs/apk/debug/app-debug.apk

## Cara 2: GitHub Actions (tanpa instal apa pun)
1. Buat repository baru di GitHub, unggah seluruh isi folder ini
   (termasuk folder .github).
2. Buka tab Actions -> workflow "Build APK" jalan otomatis.
3. Setelah hijau, unduh artifact "MenuDaya-apk" -> berisi app-debug.apk.

## Cara pakai di HP (Xiaomi)
1. Pindahkan APK ke HP, instal (izinkan "instal dari sumber tidak dikenal").
2. Pengaturan -> Pengaturan tambahan -> Aksesibilitas -> Menu Daya -> aktifkan.
3. Ketuk ikon "Menu Daya" -> menu Matikan / Mulai ulang muncul.

Tips Xiaomi/HyperOS: agar service tidak dimatikan sistem, buka Info aplikasi
Menu Daya -> Mulai otomatis (aktifkan) dan Hemat baterai -> Tanpa batasan.
