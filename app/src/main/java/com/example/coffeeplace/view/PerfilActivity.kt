package com.example.coffeeplace.view

import android.Manifest // Permisos de Android
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder // Para convertir coordenadas en direcciones (geocodificación inversa)
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog // Para diálogos modales
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat // Para solicitar permisos
import androidx.core.content.ContextCompat
import com.example.coffeeplace.R
import com.google.android.gms.location.FusedLocationProviderClient // Cliente para obtener la última ubicación
import com.google.android.gms.location.LocationServices
import java.util.* // Para el Locale del Geocoder

class PerfilActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient // Cliente de ubicación de Google Play Services
    private lateinit var tvLocation: TextView // TextView para mostrar la ciudad/dirección
    private lateinit var tvCoords: TextView // TextView para mostrar las coordenadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // 1. Referencias a los componentes (vistas de perfil y botones)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        tvLocation = findViewById(R.id.tvLocation)
        tvCoords = findViewById(R.id.tvCoords)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 2. Obtener datos de la sesión guardada (SharedPreferences)
        val prefs = getSharedPreferences("CoffeePlacePrefs", MODE_PRIVATE)
        var nombre = prefs.getString("nombre", "Usuario") ?: "Usuario"
        var email = prefs.getString("email", "correo@ejemplo.com") ?: "correo@ejemplo.com"

        // 3. Mostrar datos del usuario en la UI
        tvUserName.text = nombre
        tvUserEmail.text = email

        // 4. Configuración de Ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtenerUbicacion() // Llama al metodo para iniciar la lógica de ubicación


        // Lógica de Cerrar Sesión
        btnLogout.setOnClickListener {
            prefs.edit().clear().apply() // Elimina todos los datos guardados de la sesión.
            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            // Navega a LoginActivity, limpiando la pila de Activities para que no se pueda volver atrás.
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Metodo para obtener la ubicación actual del dispositivo.
    private fun obtenerUbicacion() {
        // 1. Verifica si ya tiene el permiso ACCESS_FINE_LOCATION.
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tiene permiso, lo solicita al usuario.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
            return
        }

        // 2. Si tiene permiso, solicita la última ubicación conocida.
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Utiliza Geocoder para convertir coordenadas (lat, lon) a una dirección legible.
                val geocoder = Geocoder(this, Locale.getDefault())
                @Suppress("DEPRECATION")
                val direcciones = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                // Obtiene la ciudad (locality) o un mensaje de fallback.
                val ciudad = direcciones?.firstOrNull()?.locality ?: "Ubicación desconocida"

                tvLocation.text = "📍 Estás en: $ciudad"
                tvCoords.text = "🗺️ Coordenadas: ${location.latitude}, ${location.longitude}"
            } else {
                tvLocation.text = "No se pudo obtener ubicación"
                tvCoords.text = ""
            }
        }
    }

    // Metodo de callback que se ejecuta después de que el usuario responde a la solicitud de permisos.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Si el código es 100 (el que usamos) y el permiso fue concedido.
        if (requestCode == 100 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacion() // Intenta obtener la ubicación de nuevo.
        }
    }
}
