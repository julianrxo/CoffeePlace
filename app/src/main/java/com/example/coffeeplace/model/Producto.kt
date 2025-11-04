package com.example.coffeeplace.model

// Data class que representa un producto individual ofrecido en la cafetería.
data class Producto(
    // ID único del producto, útil para bases de datos (e.g., Firebase).
    val id: String? = null,
    // Nombre del producto (e.g., "Latte", "Croissant").
    val nombre: String? = null,
    // Descripción detallada del producto.
    val descripcion: String? = null,
    // Precio del producto.
    val precio: Double? = null,
    // URL para cargar la imagen del producto (usada con Glide en el Adapter).
    val imagenUrl: String? = null,
    // Categoría a la que pertenece el producto (e.g., "Bebidas Calientes", "Accesorios").
    val categoria: String? = null
)