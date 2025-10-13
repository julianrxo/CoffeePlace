package com.example.coffeeplace.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeplace.R

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Referencias a los elementos del layout
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Recibir datos del usuario desde el registro o login
        val name = intent.getStringExtra("userName") ?: "Usuario"
        val email = intent.getStringExtra("userEmail") ?: "correo@coffeeplace.com"

        tvUserName.text = name
        tvUserEmail.text = email

        // Acción del botón editar perfil (solo muestra mensaje por ahora)
        btnEditProfile.setOnClickListener {
            android.widget.Toast.makeText(this, "Función próximamente ☕", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Acción del botón cerrar sesión
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
