package com.example.coffeeplace.viewModel

import androidx.lifecycle.LiveData // Componente para datos observables e inmutables.
import androidx.lifecycle.MutableLiveData // Componente para datos observables y mutables.
import androidx.lifecycle.ViewModel // Clase base para ViewModels (gestionan el ciclo de vida).
import com.example.coffeeplace.model.*
import com.example.coffeeplace.repository.FirebaseRepository // Importa la capa de datos.

// ViewModel que contiene la lógica de negocio y mantiene el estado de la UI (LiveData).
class AppViewModel : ViewModel() {

    private val repository = FirebaseRepository() // Instancia del Repositorio de datos.

    // LiveData para observar desde la UI (propiedades privadas mutables)
    private val _productos = MutableLiveData<List<Producto>>()
    // Propiedad pública de solo lectura que expone el LiveData a la UI.
    val productos: LiveData<List<Producto>> get() = _productos

    private val _carrito = MutableLiveData<List<CarritoItem>>()
    val carrito: LiveData<List<CarritoItem>> get() = _carrito

    private val _usuarioActual = MutableLiveData<Usuario?>()
    val usuarioActual: LiveData<Usuario?> get() = _usuarioActual



    //  --- USUARIOS ---

    // Llama al repositorio para registrar el usuario.
    fun registrarUsuario(usuario: Usuario, onResult: (Boolean) -> Unit) {
        repository.registrarUsuario(usuario) { onResult(it) }
    }

    // Llama al repositorio para iniciar sesión.
    fun loginUsuario(email: String, contrasena: String, onResult: (Boolean) -> Unit) {
        repository.loginUsuario(email, contrasena) { user ->
            // Si el login tiene éxito, actualiza el usuario actual.
            _usuarioActual.value = user
            // Retorna 'true' si se encontró el usuario, 'false' si es null.
            onResult(user != null)
        }
    }


    // --- PRODUCTOS --- (con callbacks)

    // Solicita la lista de productos al repositorio.
    fun obtenerProductos() {
        repository.obtenerProductos { lista ->
            // Cuando los datos llegan, actualiza el LiveData, notificando a la UI.
            _productos.value = lista
        }
    }

    // Agrega un producto; si tiene éxito, vuelve a obtener la lista para actualizar la UI.
    fun agregarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        repository.agregarProducto(producto) { ok ->
            if (ok) obtenerProductos()
            onResult(ok)
        }
    }

    // Actualiza un producto; si tiene éxito, vuelve a obtener la lista.
    fun actualizarProducto(producto: Producto, onResult: (Boolean) -> Unit) {
        repository.actualizarProducto(producto) { ok ->
            if (ok) obtenerProductos()
            onResult(ok)
        }
    }

    // Elimina un producto; si tiene éxito, vuelve a obtener la lista.
    fun eliminarProducto(id: String, onResult: (Boolean) -> Unit) {
        repository.eliminarProducto(id) { ok ->
            if (ok) obtenerProductos()
            onResult(ok)
        }
    }


    // --- CARRITO ---

    // Agrega un ítem; si tiene éxito, vuelve a cargar el carrito para actualizar la UI.
    fun agregarAlCarrito(item: CarritoItem, onResult: (Boolean) -> Unit) {
        repository.agregarAlCarrito(item) { ok ->
            if (ok) cargarCarrito()
            onResult(ok)
        }
    }

    // Solicita la lista del carrito al repositorio.
    fun cargarCarrito() {
        repository.obtenerCarrito { lista ->
            _carrito.value = lista // Actualiza el LiveData del carrito.
        }
    }

    // Elimina un ítem; si tiene éxito, vuelve a cargar el carrito.
    fun eliminarDelCarrito(id: String, onResult: (Boolean) -> Unit) {
        repository.eliminarDelCarrito(id) { ok ->
            if (ok) cargarCarrito()
            onResult(ok)
        }
    }
}