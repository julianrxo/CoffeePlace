package com.example.coffeeplace.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeplace.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logoImage)
        val appName = findViewById<TextView>(R.id.appName)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        logo.startAnimation(fadeIn)
        appName.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("CoffeePlacePrefs", MODE_PRIVATE)
            val email = prefs.getString("email", null)

            if (email != null) {
                // ✅ Ya hay sesión activa
                startActivity(Intent(this, ProductoActivity::class.java))
            } else {
                // 🚀 No hay sesión → ir a bienvenida
                startActivity(Intent(this, BienvenidaActivity::class.java))
            }
            finish()
        }, 1000)
    }
}
