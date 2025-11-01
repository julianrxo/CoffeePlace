package com.example.coffeeplace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Importa la librería Glide para cargar imágenes
import com.example.coffeeplace.R
import com.example.coffeeplace.model.Producto

// Adaptador para el RecyclerView que muestra la lista de productos disponibles.
class ProductoAdapter(
    private var productos: List<Producto>, // Lista de productos a mostrar.
    private val onEliminar: (Producto) -> Unit, // Función para manejar la acción de eliminar.
    private val onEditar: (Producto) -> Unit,   // Función para manejar la acción de editar.
    private val onAgregarCarrito: (Producto) -> Unit // Función para agregar al carrito.
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    // Contenedor de Vistas (ViewHolder). Guarda referencias de los componentes del ítem.
    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val precio: TextView = view.findViewById(R.id.tvPrecio)
        val imagen: ImageView = view.findViewById(R.id.ivImagen)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
        val btnEditar: Button = view.findViewById(R.id.btnEditar)
        // ImageButton para agregar al carrito (itemView.findViewById es otra forma de buscar)
        val btnAgregarCarrito: ImageButton = itemView.findViewById(R.id.btnAgregarCarrito)
    }

    // Crea e infla el layout (item_producto.xml) para un nuevo ítem.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    // Enlaza los datos de un Producto con las vistas.
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // Asignación de datos
        holder.nombre.text = producto.nombre
        holder.precio.text = "$${producto.precio}"

        // Carga la imagen desde una URL usando Glide.
        Glide.with(holder.itemView.context)
            .load(producto.imagenUrl) // URL de la imagen.
            .placeholder(R.drawable.ic_launcher_foreground) // Placeholder mientras carga.
            .into(holder.imagen) // Dónde mostrar la imagen.

        // Configuracion de Listeners (funciones lambda pasadas en el constructor)
        holder.btnEliminar.setOnClickListener { onEliminar(producto) }
        holder.btnEditar.setOnClickListener { onEditar(producto) }
        holder.btnAgregarCarrito.setOnClickListener { onAgregarCarrito(producto) }
    }

    // Devuelve el número total de productos en la lista.
    override fun getItemCount(): Int = productos.size

    // Metodo para reemplazar la lista de productos y forzar la actualización del RecyclerView.
    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista
        notifyDataSetChanged()
    }
}
