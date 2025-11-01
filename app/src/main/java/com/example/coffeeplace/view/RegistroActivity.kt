package com.example.coffeeplace.view

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeplace.R
import com.example.coffeeplace.model.Usuario // Importa el modelo de datos Usuario
import com.example.coffeeplace.viewModel.AppViewModel

// Activity que gestiona la interfaz y lógica para registrar nuevos usuarios.
class RegistroActivity : AppCompatActivity() {

    // Inicializa el ViewModel para manejar la lógica de registro.
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Referencias a los campos de entrada y botones.
        val inputNombre = findViewById<EditText>(R.id.inputName)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegister)
        val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)

        // Listener del botón de registro.
        btnRegistrar.setOnClickListener {
            // Obtiene y limpia los textos de los campos.
            val nombre = inputNombre.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            // 1. Validación de campos vacíos.
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Sale del Listener si falla la validación.
            }

            // 2. Crea el objeto Usuario a partir de los datos.
            val usuario = Usuario(nombre = nombre, email = email, contrasena = password)

            // 3. Llama al ViewModel para registrar el usuario en Firebase.
            viewModel.registrarUsuario(usuario) { exito ->
                if (exito) {
                    // ✅ Guardar sesión automática (asume que el registro exitoso es un login automático).
                    val prefs = getSharedPreferences("CoffeePlacePrefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("nombre", usuario.nombre)
                        .putString("email", usuario.email)
                        .apply() // Guarda la sesión.

                    Toast.makeText(this, "Registro exitoso ☕", Toast.LENGTH_SHORT).show()
                    // Navega a la pantalla principal de productos y cierra esta Activity.
                    startActivity(Intent(this, ProductoActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Enlace para ir a la Activity de Login.
        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Cierra la Activity de registro.
        }
    }
}
