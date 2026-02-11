package com.vibeagent.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecureWalletStorage — Non-Custodial Wallet Storage
 * 
 * ⚠️ IMPORTANT SECURITY PRINCIPLES:
 * - Seed phrase & private key NEVER leave the device
 * - Data encrypted with AES-256 via Android Keystore
 * - NEVER sent to Firebase or any remote server
 * - Cleared completely on logout
 */
class SecureWalletStorage(context: Context) {

    companion object {
        private const val ENCRYPTED_PREFS_NAME = "vibeagent_secure_wallet"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
        private const val KEY_PRIVATE_KEY = "private_key"
        private const val KEY_MNEMONIC = "mnemonic"
        private const val KEY_WALLET_CONNECTED = "wallet_connected"
        private const val KEY_LOGIN_METHOD = "login_method"
    }

    private val encryptedPrefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // ===== SAVE WALLET (Encrypted) =====
    fun saveWallet(address: String, privateKey: String, mnemonic: String = "", loginMethod: String = "wallet") {
        encryptedPrefs.edit().apply {
            putString(KEY_WALLET_ADDRESS, address)
            putString(KEY_PRIVATE_KEY, privateKey)
            putString(KEY_MNEMONIC, mnemonic)
            putBoolean(KEY_WALLET_CONNECTED, true)
            putString(KEY_LOGIN_METHOD, loginMethod)
            apply()
        }
    }

    // ===== READ WALLET =====
    fun getWalletAddress(): String? = encryptedPrefs.getString(KEY_WALLET_ADDRESS, null)
    fun getPrivateKey(): String? = encryptedPrefs.getString(KEY_PRIVATE_KEY, null)
    fun getMnemonic(): String? = encryptedPrefs.getString(KEY_MNEMONIC, null)
    fun isWalletConnected(): Boolean = encryptedPrefs.getBoolean(KEY_WALLET_CONNECTED, false)
    fun getLoginMethod(): String? = encryptedPrefs.getString(KEY_LOGIN_METHOD, null)

    // ===== CLEAR ALL — Called on logout =====
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }

    // ===== CHECK IF WALLET EXISTS =====
    fun hasWallet(): Boolean {
        return !getWalletAddress().isNullOrBlank()
    }
}
