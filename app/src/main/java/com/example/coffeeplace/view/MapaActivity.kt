package com.example.coffeeplace.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.coffeeplace.R
import com.example.coffeeplace.databinding.ActivityMapaBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.widget.ImageButton

class MapaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapaBinding
    private lateinit var miUbicacionOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        binding = ActivityMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val map = binding.mapa
        map.setMultiTouchControls(true)

        // ✅ Pedir permisos y luego iniciar ubicación si se tiene permiso
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            iniciarUbicacion()
        }

        // ✅ Marcadores de tiendas
        agregarMarcador(4.7110, -74.0721, "CoffeePlace Centro", "Horario: 7:00 AM - 9:00 PM")
        agregarMarcador(4.6500, -74.0830, "CoffeePlace Norte", "Horario: 8:00 AM - 8:00 PM")

        // ✅ Vista inicial del mapa
        map.controller.setZoom(15.0)
        map.controller.setCenter(GeoPoint(4.7110, -74.0721))

        // ✅ Botón "Mi Ubicación"
        findViewById<ImageButton>(R.id.btnMiUbicacion).setOnClickListener {
            val loc = miUbicacionOverlay.lastFix
            if (loc != null) {
                val punto = GeoPoint(loc.latitude, loc.longitude)
                binding.mapa.controller.animateTo(punto)
            } else {
                Toast.makeText(this, "📍 Obteniendo ubicación...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Nueva función para iniciar la ubicación
    private fun iniciarUbicacion() {
        val map = binding.mapa

        miUbicacionOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        map.overlays.add(miUbicacionOverlay)
        miUbicacionOverlay.enableMyLocation()

        // ✅ Cuando obtenga la ubicación por primera vez, centrar automáticamente
        miUbicacionOverlay.runOnFirstFix {
            runOnUiThread {
                val loc = miUbicacionOverlay.myLocation
                if (loc != null) {
                    val punto = GeoPoint(loc.latitude, loc.longitude)
                    map.controller.animateTo(punto)
                    map.controller.setZoom(17.0)
                    Toast.makeText(this, "📍 Ubicación detectada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun agregarMarcador(lat: Double, lon: Double, titulo: String, horario: String) {
        val marker = Marker(binding.mapa)
        marker.position = GeoPoint(lat, lon)
        marker.title = titulo
        marker.subDescription = horario
        marker.icon = resources.getDrawable(R.mipmap.ic_launcher_cp, null)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapa.overlays.add(marker)
    }

    // ✅ Manejo del resultado del permiso
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarUbicacion()
        } else {
            Toast.makeText(this, "❗ Debes permitir la ubicación para mostrar tu posición", Toast.LENGTH_LONG).show()
        }
    }
}
