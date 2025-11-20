package com.example.coffeeplace.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeplace.R
import com.example.coffeeplace.adapter.ProductoAdapter
import com.example.coffeeplace.databinding.ActivityProductosBinding
import com.example.coffeeplace.model.CarritoItem
import com.example.coffeeplace.model.Producto
import com.example.coffeeplace.viewModel.AppViewModel

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import android.content.pm.PackageManager

class ProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductosBinding
    private val viewModel: AppViewModel by viewModels()
    private lateinit var adapter: ProductoAdapter
    private var contadorCarrito = 0

    // ✅ IMGBB
    private var selectedImageUrl: String? = null
    private val apiKeyImgBB = "f1fbaa8a88332125fdc4fa71f2753a07"
    private val client = OkHttpClient()

    // ✅ PERMISOS DINÁMICOS
    private val permisos = arrayOf(
        android.Manifest.permission.CAMERA,
        if (android.os.Build.VERSION.SDK_INT >= 33)
            android.Manifest.permission.READ_MEDIA_IMAGES
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val requestPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Puedes validar si quieres */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // ✅ Solicitar permisos al inicio
        requestPermisos.launch(permisos)

        adapter = ProductoAdapter(
            productos = listOf(),
            onEliminar = { producto -> eliminarProducto(producto) },
            onEditar = { producto -> mostrarDialogEditar(producto) },
            onAgregarCarrito = { producto -> agregarAlCarrito(producto) }
        )

        binding.recyclerProductos.layoutManager = LinearLayoutManager(this)
        binding.recyclerProductos.adapter = adapter

        viewModel.productos.observe(this) { lista -> adapter.actualizarLista(lista) }
        viewModel.carrito.observe(this) { lista -> contadorCarrito = lista.size }

        binding.btnAgregar.setOnClickListener { mostrarDialogAgregar() }
        binding.fabCarrito.setOnClickListener { startActivity(Intent(this, CarritoActivity::class.java)) }

        viewModel.obtenerProductos()
        viewModel.cargarCarrito()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuPerfil -> {
                startActivity(Intent(this, PerfilActivity::class.java))
                true
            }
            R.id.menuMapa -> {
                startActivity(Intent(this, MapaActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ✅ DIALOGO AGREGAR PRODUCTO
    private fun mostrarDialogAgregar() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputNombre = EditText(this).apply { hint = "Nombre del producto" }
        val inputPrecio = EditText(this).apply { hint = "Precio (ej: 3500)" }
        val inputDescripcion = EditText(this).apply { hint = "Descripción" }
        val btnSeleccionar = Button(this).apply { text = "Seleccionar Imagen" }

        layout.addView(inputNombre)
        layout.addView(inputPrecio)
        layout.addView(inputDescripcion)
        layout.addView(btnSeleccionar)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar nuevo producto ☕")
            .setView(layout)
            .setPositiveButton("Agregar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        btnSeleccionar.setOnClickListener { mostrarOpcionesImagen() }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val precio = inputPrecio.text.toString().toDoubleOrNull()
            val descripcion = inputDescripcion.text.toString().trim()

            if (nombre.isEmpty() || precio == null || descripcion.isEmpty() || selectedImageUrl == null) {
                Toast.makeText(this, "Completa todos los campos y sube una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevo = Producto(
                nombre = nombre,
                precio = precio,
                descripcion = descripcion,
                imagenUrl = selectedImageUrl!!,
                categoria = "General"
            )

            viewModel.agregarProducto(nuevo) { ok ->
                Toast.makeText(this, if (ok) "Producto agregado ✅" else "Error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }

    // ✅ OPCIONES DE IMAGEN
    private fun mostrarOpcionesImagen() {
        val opciones = arrayOf("Tomar foto 📷", "Elegir de la galería 🖼")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, i ->
                if (i == 0) abrirCamara()
                else abrirGaleria()
            }
            .show()
    }

    // ✅ GALERÍA
    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val uri = it.data?.data ?: return@registerForActivityResult
        subirImagenAGaleria(uri)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaLauncher.launch(intent)
    }

    // ✅ CÁMARA (Actualizado con verificación de permisos)
    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val bitmap = it.data?.extras?.get("data") as? Bitmap ?: return@registerForActivityResult
        subirImagenDeCamara(bitmap)
    }

    private fun abrirCamara() {
        val tienePermiso = permisos.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!tienePermiso) {
            requestPermisos.launch(permisos)
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraLauncher.launch(intent)
    }

    // ✅ Subida a IMGBB
    private fun subirImagenAGaleria(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_image.jpg")
        val output = FileOutputStream(file)
        inputStream?.copyTo(output)
        output.close()
        subirImagenAImgBB(file)
    }

    private fun subirImagenDeCamara(bitmap: Bitmap) {
        val file = File(cacheDir, "temp_cam_image.jpg")
        val output = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        output.close()
        subirImagenAImgBB(file)
    }

    private fun subirImagenAImgBB(file: File) {
        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("key", apiKeyImgBB)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                runOnUiThread { Toast.makeText(this@ProductoActivity, "Error subiendo imagen ❌", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: return
                selectedImageUrl = JSONObject(json).getJSONObject("data").getString("url")
                runOnUiThread { Toast.makeText(this@ProductoActivity, "Imagen lista ✅", Toast.LENGTH_SHORT).show() }
            }
        })
    }

    // ✅ Resto: Editar, eliminar y carrito
    private fun mostrarDialogEditar(producto: Producto) {

        // Reiniciamos selectedImageUrl para evitar arrastrar la imagen del modo "agregar"
        selectedImageUrl = producto.imagenUrl

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputNombre = EditText(this).apply {
            setText(producto.nombre)
            hint = "Nombre"
        }

        val inputPrecio = EditText(this).apply {
            setText(producto.precio.toString())
            hint = "Precio"
        }

        val inputDesc = EditText(this).apply {
            setText(producto.descripcion)
            hint = "Descripción"
        }


        val btnCambiarImagen = Button(this).apply {
            text = "📸 Cambiar Imagen"
            setOnClickListener {
                mostrarOpcionesImagen() // Usa la misma función de agregar
            }
        }

        layout.addView(inputNombre)
        layout.addView(inputPrecio)
        layout.addView(inputDesc)
        layout.addView(btnCambiarImagen)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar producto ☕")
            .setView(layout)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre = inputNombre.text.toString().trim()
            val precio = inputPrecio.text.toString().toDoubleOrNull()
            val descripcion = inputDesc.text.toString().trim()

            if (nombre.isEmpty() || precio == null || descripcion.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val urlFinal = selectedImageUrl ?: producto.imagenUrl ?: ""

            val actualizado = producto.copy(
                nombre = nombre,
                precio = precio,
                descripcion = descripcion,
                imagenUrl = urlFinal
            )

            viewModel.actualizarProducto(actualizado) { ok ->
                Toast.makeText(this, if (ok) "Producto actualizado ✅" else "Error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }


    private fun eliminarProducto(producto: Producto) {
        producto.id?.let { id ->
            viewModel.eliminarProducto(id) {
                Toast.makeText(this, if (it) "Eliminado" else "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarAlCarrito(producto: Producto) {
        val item = CarritoItem(
            productoId = producto.id,
            nombre = producto.nombre,
            precio = producto.precio,
            cantidad = 1
        )
        viewModel.agregarAlCarrito(item) { ok ->
            if (ok) {
                Toast.makeText(this, "Agregado al carrito 🛒", Toast.LENGTH_SHORT).show()
                animarCarrito()
                viewModel.cargarCarrito()
            }
        }
    }

    private fun animarCarrito() {
        binding.fabCarrito.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(150)
            .withEndAction {
                binding.fabCarrito.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }.start()
    }
}
