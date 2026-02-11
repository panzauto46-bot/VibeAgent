package com.vibeagent.app.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("VibeAgentPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_LOGIN_METHOD = "login_method"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
        private const val KEY_WALLET_CONNECTED = "wallet_connected"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getWalletAddress(): String? {
        return prefs.getString(KEY_WALLET_ADDRESS, null)
    }

    fun isWalletConnected(): Boolean {
        return prefs.getBoolean(KEY_WALLET_CONNECTED, false)
    }

    /**
     * FULL LOGOUT — Clears ALL regular SharedPreferences.
     * ⚠️ NOTE: This does NOT clear EncryptedSharedPreferences.
     * Call SecureWalletStorage.clearAll() separately to wipe wallet secrets.
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}
