package com.example.coffeeplace.model

// Data class que representa un ítem específico dentro del carrito de compras.

// Se usa para almacenar los datos de un producto tal como se añade al carrito.
data class CarritoItem(
    // ID único del producto. Puede ser nulo (opcional).
    val productoId: String? = null,

    // Nombre del producto. Puede ser nulo (opcional).
    val nombre: String? = null,

    // Cantidad del producto seleccionado. Valor predeterminado de 1.
    val cantidad: Int = 1,

    // Precio unitario del producto. Puede ser nulo (opcional).
    val precio: Double? = null,

    // Tamaño o variación seleccionada (e.g., "Grande", "Mediano"). Puede ser nulo (opcional).
    val tamaño: String? = null
)