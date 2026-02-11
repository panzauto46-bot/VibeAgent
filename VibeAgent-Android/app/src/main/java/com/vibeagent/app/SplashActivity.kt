package com.vibeagent.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.vibeagent.app.util.PrefsManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuth()
        }, 2000)
    }

    private fun checkAuth() {
        val prefs = PrefsManager(this)
        if (prefs.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }
}
