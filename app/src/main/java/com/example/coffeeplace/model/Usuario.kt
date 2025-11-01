package com.example.coffeeplace.model

// Data class que representa un perfil de usuario registrado en la aplicación.
data class Usuario(
    // ID único del usuario, generado típicamente por un sistema de autenticación (e.g., Firebase Auth).
    val id: String? = null,
    // Nombre completo o de perfil del usuario.
    val nombre: String? = null,
    // Correo electrónico del usuario (utilizado a menudo para iniciar sesión).
    val email: String? = null,
    // La contraseña del usuario. **Nota:** En un entorno de producción, este campo
    // nunca debe almacenarse directamente en una base de datos, solo su hash seguro.
    val contrasena: String? = null
)