package com.example.coffeeplace.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels // Delegado para inicializar el ViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeplace.adapter.CarritoAdapter
import com.example.coffeeplace.databinding.ActivityCarritoBinding // Binding de la vista
import com.example.coffeeplace.model.CarritoItem
import com.example.coffeeplace.viewModel.AppViewModel // El ViewModel para la lógica de datos

// Activity que muestra la lista de productos en el carrito y gestiona la compra.
class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    // Inicializa el ViewModel asociado al ciclo de vida de la Activity.
    private val viewModel: AppViewModel by viewModels()
    private lateinit var adapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa View Binding para acceder a los componentes del layout de forma segura.
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el adaptador: le pasa una lista vacía inicial y la función de eliminación.
        adapter = CarritoAdapter(listOf()) { item -> eliminarItem(item) }
        // Configura el RecyclerView con un LayoutManager lineal.
        binding.recyclerCarrito.layoutManager = LinearLayoutManager(this)
        binding.recyclerCarrito.adapter = adapter

        // Observa los cambios en la lista de ítems del carrito (LiveData del ViewModel).
        viewModel.carrito.observe(this) { lista ->
            adapter.actualizarLista(lista) // Actualiza el RecyclerView.
            actualizarTotales(lista) // Recalcula y muestra los precios.
        }

        // Listener para el botón de finalizar compra.
        binding.btnFinalizarCompra.setOnClickListener {
            // Muestra un mensaje simple de éxito al "finalizar" la compra.
            Toast.makeText(this, "Compra finalizada exitosamente ☕", Toast.LENGTH_SHORT).show()
        }

        // Inicia la carga de los ítems del carrito desde el repositorio/Firebase.
        viewModel.cargarCarrito()
    }

    // Lógica para eliminar un ítem específico del carrito.
    private fun eliminarItem(item: CarritoItem) {
        // Asegura que el productoId no sea nulo antes de llamar al ViewModel.
        item.productoId?.let { id ->
            viewModel.eliminarDelCarrito(id) { ok ->
                // Muestra un Toast con el resultado de la operación.
                val msg = if (ok) "Producto eliminado del carrito" else "Error al eliminar"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Calcula el subtotal y el total con impuestos, y actualiza los TextViews.
    private fun actualizarTotales(lista: List<CarritoItem>) {
        // Calcula el subtotal sumando el precio (unitario * cantidad) de cada ítem.
        val subtotal = lista.sumOf { (it.precio ?: 0.0) * it.cantidad }
        // Calcula el total (Subtotal con 10% de impuesto).
        val total = subtotal * 1.1
        // Actualiza el TextView del Subtotal, formateando a dos decimales.
        binding.tvSubtotal.text = "Subtotal: $${String.format("%.2f", subtotal)}"
        // Actualiza el TextView del Total.
        binding.tvTotal.text = "Total: $${String.format("%.2f", total)}"
    }
}