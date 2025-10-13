package com.example.coffeeplace.Activity

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

        // Referencias a los elementos del layout
        val logo = findViewById<ImageView>(R.id.logoImage)
        val appName = findViewById<TextView>(R.id.appName)

        // Cargar animación desde res/anim
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Aplicar la animación
        logo.startAnimation(fadeIn)
        appName.startAnimation(fadeIn)

        // Espera 2.5 segundos y luego pasa al Welcome
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BienvenidaActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}
