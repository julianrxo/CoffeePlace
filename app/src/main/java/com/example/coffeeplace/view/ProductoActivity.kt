package com.example.coffeeplace.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeplace.R
import com.example.coffeeplace.adapter.ProductoAdapter
import com.example.coffeeplace.databinding.ActivityProductosBinding // Binding para acceder a la vista
import com.example.coffeeplace.model.CarritoItem
import com.example.coffeeplace.model.Producto
import com.example.coffeeplace.viewModel.AppViewModel


class ProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductosBinding
    private val viewModel: AppViewModel by viewModels() // ViewModel asociado a la Activity
    private lateinit var adapter: ProductoAdapter
    private var contadorCarrito = 0 // Contador para la cantidad de ítems en el carrito (aunque no se muestra aquí)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configuración de View Binding y ToolBar
        binding = ActivityProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar) // Establece el Toolbar como la ActionBar

        // Inicializa el adaptador, pasando las funciones de callback para cada acción.
        adapter = ProductoAdapter(
            productos = listOf(),
            onEliminar = { producto -> eliminarProducto(producto) },
            onEditar = { producto -> mostrarDialogEditar(producto) },
            onAgregarCarrito = { producto -> agregarAlCarrito(producto) }
        )

        // Configura el RecyclerView.
        binding.recyclerProductos.layoutManager = LinearLayoutManager(this)
        binding.recyclerProductos.adapter = adapter

        // Observa la lista de productos (LiveData) y actualiza el RecyclerView.
        viewModel.productos.observe(this) { lista ->
            adapter.actualizarLista(lista)
        }

        // Observa el carrito solo para actualizar el contador.
        viewModel.carrito.observe(this) { lista ->
            contadorCarrito = lista.size
        }

        // Botón Floating Action Button (FAB) para agregar un nuevo producto.
        binding.btnAgregar.setOnClickListener { mostrarDialogAgregar() }

        // FAB para ir a la pantalla del carrito.
        binding.fabCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        // Carga inicial de datos al iniciar la Activity.
        viewModel.obtenerProductos()
        viewModel.cargarCarrito()
    }

    // Menú superior (Toolbar)
    // Infla el menú_principal en el Toolbar.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    // Maneja la selección de ítems del menú.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuPerfil -> {
                // Navega a la Activity de Perfil.
                startActivity(Intent(this, PerfilActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Muestra un AlertDialog con campos para crear un nuevo producto.
    private fun mostrarDialogAgregar() {
        // Creación dinámica de un LinearLayout y EditTexts para el diálogo.
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val inputNombre = EditText(this).apply { hint = "Nombre del producto" }
        val inputPrecio = EditText(this).apply { hint = "Precio (ej: 3500)" }
        val inputDescripcion = EditText(this).apply { hint = "Descripción" }
        val inputImagen = EditText(this).apply { hint = "URL de la imagen" }

        layout.addView(inputNombre)
        layout.addView(inputPrecio)
        layout.addView(inputDescripcion)
        layout.addView(inputImagen)

        AlertDialog.Builder(this)
            .setTitle("Agregar nuevo producto ☕")
            .setView(layout)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = inputNombre.text.toString().trim()
                val precio = inputPrecio.text.toString().toDoubleOrNull()
                val descripcion = inputDescripcion.text.toString().trim()
                val imagenUrl = inputImagen.text.toString().trim()

                // Valida que los campos requeridos estén llenos.
                if (nombre.isNotEmpty() && precio != null && descripcion.isNotEmpty()) {
                    // Crea el objeto Producto con un fallback de imagen.
                    val nuevo = Producto(
                        nombre = nombre,
                        precio = precio,
                        descripcion = descripcion,
                        imagenUrl = if (imagenUrl.isNotEmpty()) imagenUrl
                        else "https://upload.wikimedia.org/wikipedia/commons/4/45/A_small_cup_of_coffee.JPG",
                        categoria = "General"
                    )
                    // Llama al ViewModel para guardar en Firebase.
                    viewModel.agregarProducto(nuevo) { ok ->
                        Toast.makeText(this, if (ok) "Producto agregado ✅" else "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra un AlertDialog con campos para editar un producto existente.
    private fun mostrarDialogEditar(producto: Producto) {
        // Creación dinámica de la UI del diálogo de edición.
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        // EditTexts precargados con los datos actuales del producto.
        val inputNombre = EditText(this).apply { setText(producto.nombre) }
        val inputPrecio = EditText(this).apply { setText(producto.precio.toString()) }
        val inputDesc = EditText(this).apply { setText(producto.descripcion) }

        layout.addView(inputNombre)
        layout.addView(inputPrecio)
        layout.addView(inputDesc)

        AlertDialog.Builder(this)
            .setTitle("Editar producto")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                // Crea una copia del producto con los datos actualizados.
                val actualizado = producto.copy(
                    nombre = inputNombre.text.toString(),
                    // Intenta convertir el precio a Double o mantiene el precio anterior.
                    precio = inputPrecio.text.toString().toDoubleOrNull() ?: producto.precio,
                    descripcion = inputDesc.text.toString()
                )
                // Llama al ViewModel para actualizar en Firebase.
                viewModel.actualizarProducto(actualizado) { ok ->
                    Toast.makeText(this, if (ok) "Actualizado ✅" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Llama al ViewModel para eliminar un producto por su ID.
    private fun eliminarProducto(producto: Producto) {
        producto.id?.let { id ->
            viewModel.eliminarProducto(id) {
                Toast.makeText(this, if (it) "Eliminado" else "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Crea un CarritoItem y lo agrega al carrito a través del ViewModel.
    private fun agregarAlCarrito(producto: Producto) {
        val item = CarritoItem(
            productoId = producto.id,
            nombre = producto.nombre,
            precio = producto.precio,
            cantidad = 1 // Por defecto, agrega 1 unidad.
        )
        viewModel.agregarAlCarrito(item) { ok ->
            if (ok) {
                Toast.makeText(this, "Agregado al carrito 🛒", Toast.LENGTH_SHORT).show()
                animarCarrito() // Efecto visual al agregar.
                viewModel.cargarCarrito() // Vuelve a cargar el carrito para actualizar el contador.
            } else {
                Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Animación simple del Floating Action Button del carrito.
    private fun animarCarrito() {
        binding.fabCarrito.animate()
            .scaleX(1.3f) // Aumenta el tamaño
            .scaleY(1.3f)
            .setDuration(150)
            .withEndAction {
                // Vuelve al tamaño original.
                binding.fabCarrito.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }.start()
    }
}
