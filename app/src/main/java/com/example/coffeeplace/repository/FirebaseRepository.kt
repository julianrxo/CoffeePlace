package com.example.coffeeplace.repository

import com.example.coffeeplace.model.Producto
import com.example.coffeeplace.model.Usuario
import com.example.coffeeplace.model.CarritoItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// Repositorio central que abstrae las operaciones de acceso a datos con Firebase Realtime Database.
class FirebaseRepository {

    // Inicializa la instancia de Firebase Database.
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Referencias a las colecciones (nodos) principales de la base de datos.
    private val usuariosRef: DatabaseReference = database.getReference("usuarios")
    private val productosRef: DatabaseReference = database.getReference("productos")
    private val carritoRef: DatabaseReference = database.getReference("carrito") // Nota: Esto asume un carrito global/único.


    // --- USUARIOS ---

    // Registra un nuevo usuario, generando un ID y usando setValue.
    fun registrarUsuario(usuario: Usuario, onResult: (Boolean) -> Unit) {
        val id = usuariosRef.push().key // Genera una clave única para el nuevo usuario.
        if (id != null) {
            // Guarda el usuario en el nodo 'usuarios/id_generado'.
            usuariosRef.child(id).setValue(usuario.copy(id = id))
                .addOnSuccessListener { onResult(true) } // Éxito
                .addOnFailureListener { onResult(false) } // Fallo
        } else {
            onResult(false) // Fallo al obtener el ID.
        }
    }

    // Intenta iniciar sesión buscando el email y la contraseña en la base de datos.
    fun loginUsuario(email: String, contrasena: String, onResult: (Usuario?) -> Unit) {
        // Obtiene todos los usuarios (ineficiente para bases de datos grandes,
        // pero simple para un ejemplo).
        usuariosRef.get().addOnSuccessListener { snapshot ->
            for (userSnap in snapshot.children) {
                val user = userSnap.getValue(Usuario::class.java)
                // Compara credenciales
                if (user?.email == email && user.contrasena == contrasena) {
                    onResult(user) // Usuario encontrado.
                    return@addOnSuccessListener // Sale del ciclo y del listener.
                }
            }
            onResult(null) // No se encontró coincidencia.
        }
    }


    // --- CRUD PRODUCTOS ---

    // Añade un nuevo producto. Genera un ID único.
    fun agregarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        val id = productosRef.push().key
        if (id != null) {
            productosRef.child(id).setValue(producto.copy(id = id))
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        } else onResult(false)
    }

    // Obtiene toda la lista de productos de la base de datos.
    fun obtenerProductos(onDataChange: (List<Producto>) -> Unit) {
        productosRef.get().addOnSuccessListener { snapshot ->
            val lista = mutableListOf<Producto>()
            // Itera sobre los resultados y convierte cada uno a un objeto Producto.
            for (item in snapshot.children) {
                item.getValue(Producto::class.java)?.let { lista.add(it) }
            }
            onDataChange(lista) // Retorna la lista de productos.
        }
    }

    // Actualiza un producto existente usando su ID.
    fun actualizarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        if (producto.id != null) {
            // Sobrescribe el valor del nodo existente con el objeto Producto.
            productosRef.child(producto.id!!).setValue(producto)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        } else onResult(false)
    }

    // Elimina un producto por su ID.
    fun eliminarProducto(id: String, onResult: (Boolean) -> Unit) {
        productosRef.child(id).removeValue() // Metodo de eliminación de Firebase.
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    // ---CARRITO---

    // Agrega un ítem al carrito. Genera un ID de carritoItem.
    fun agregarAlCarrito(item: CarritoItem, onResult: (Boolean) -> Unit) {
        val productoId = carritoRef.push().key
        if (productoId != null) {
            carritoRef.child(productoId).setValue(item.copy(productoId = productoId))
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        } else onResult(false)
    }

    // Obtiene todos los ítems del carrito.
    fun obtenerCarrito(onDataChange: (List<CarritoItem>) -> Unit) {
        carritoRef.get().addOnSuccessListener { snapshot ->
            val lista = mutableListOf<CarritoItem>()
            for (item in snapshot.children) {
                item.getValue(CarritoItem::class.java)?.let { lista.add(it) }
            }
            onDataChange(lista)
        }
    }

    // Elimina un ítem específico del carrito por su ID.
    fun eliminarDelCarrito(id: String, onResult: (Boolean) -> Unit) {
        carritoRef.child(id).removeValue()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}