package com.example.coffeeplace.view

import android.content.Intent
import android.os.Bundle
import android.widget.* // Importa todos los widgets (EditText, Button, TextView, Toast)
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeplace.R
import com.example.coffeeplace.viewModel.AppViewModel // El ViewModel para la lógica de autenticación

// Activity que gestiona la interfaz y lógica de inicio de sesión de usuarios.
class LoginActivity : AppCompatActivity() {

    // Inicializa el ViewModel que manejará la comunicación con el repositorio (Firebase).
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los componentes de la interfaz usando findViewById
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

        // Listener del botón de inicio de sesión
        btnLogin.setOnClickListener {
            // Obtiene y limpia los textos de email y contraseña.
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            // 1. Validación simple de campos vacíos.
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // 2. Llama al ViewModel para intentar iniciar sesión.
                viewModel.loginUsuario(email, password) { success ->
                    if (success) {
                        val usuario = viewModel.usuarioActual.value
                        if (usuario != null) {
                            // ✅ Guardar sesión (Persistencia simple con SharedPreferences)
                            val prefs = getSharedPreferences("CoffeePlacePrefs", MODE_PRIVATE)
                            prefs.edit()
                                .putString("nombre", usuario.nombre)
                                .putString("email", usuario.email)
                                .apply() // Aplica los cambios de forma asíncrona.

                            // Ir a la pantalla principal de productos y cierra esta Activity.
                            startActivity(Intent(this, ProductoActivity::class.java))
                            finish()
                        }
                    } else {
                        // Muestra un error si la autenticación falla.
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Enlace al registro: Navega a la Activity de registro y cierra la actual.
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }
    }
}