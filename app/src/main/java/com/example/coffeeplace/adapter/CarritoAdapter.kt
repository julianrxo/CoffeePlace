package com.example.coffeeplace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeplace.R
import com.example.coffeeplace.model.CarritoItem

// Adaptador del RecyclerView para mostrar los ítems del carrito.
class CarritoAdapter(
    private var items: List<CarritoItem>, // Lista de ítems del carrito.
    // Función a ejecutar cuando se elimina un ítem.
    private val onEliminar: (CarritoItem) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    // Contenedor de Vistas (ViewHolder). Guarda referencias de los componentes de la UI.
    inner class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvCarritoNombre)
        val cantidad: TextView = view.findViewById(R.id.tvCarritoCantidad)
        val precio: TextView = view.findViewById(R.id.tvCarritoPrecio)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarCarrito)
    }

    // Crea e infla el layout para un nuevo ítem de la lista.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    // Asigna los datos del ítem a las vistas en la posición actual.
    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = items[position]

        // Asignación de datos
        holder.nombre.text = item.nombre ?: "Producto"
        holder.cantidad.text = "Cantidad: ${item.cantidad}"
        // Calcula el precio total (unitario * cantidad).
        holder.precio.text = "$${(item.precio ?: 0.0) * item.cantidad}"

        // Listener para el botón Eliminar. Ejecuta la función 'onEliminar'.
        holder.btnEliminar.setOnClickListener { onEliminar(item) }
    }

    // Devuelve el número total de ítems en la lista.
    override fun getItemCount(): Int = items.size

    // Metodo para actualizar la lista de datos y notificar el cambio.
    fun actualizarLista(nuevaLista: List<CarritoItem>) {
        items = nuevaLista
        notifyDataSetChanged() // Notifica al RecyclerView que debe redibujar.
    }
}