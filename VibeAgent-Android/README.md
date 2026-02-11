# VibeAgent - AI Financial Assistant

Aplikasi Android Native untuk BSC Testnet wallet management.

## Fitur
- 💬 Chat Interface dengan AI
- 👛 Buat Wallet Baru
- 📥 Import Wallet dengan Private Key
- 💰 Cek Saldo BNB
- 💸 Kirim BNB ke alamat lain
- ⚡ Quick Actions

## Requirements
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34

## Cara Build APK

### Menggunakan Android Studio (Direkomendasikan)
1. Buka Android Studio
2. Pilih **File > Open** dan navigasi ke folder `VibeAgent-Android`
3. Tunggu Gradle sync selesai
4. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**
5. APK akan berada di: `app/build/outputs/apk/debug/app-debug.apk`

### Menggunakan Command Line
```bash
# Di Windows dengan Android Studio terinstall
cd VibeAgent-Android
gradlew.bat assembleDebug
```

## Lokasi APK
Setelah build selesai, APK dapat ditemukan di:
```
VibeAgent-Android/app/build/outputs/apk/debug/app-debug.apk
```

## Testing
1. Transfer APK ke device Android
2. Enable "Install from Unknown Sources" di Settings
3. Install dan jalankan aplikasi

## Network Info
- Chain: BSC Testnet
- Chain ID: 97
- Currency: tBNB
- Faucet: https://testnet.bnbchain.org/faucet-smart
