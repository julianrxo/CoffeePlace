# ☕ CoffeePlace

**CoffeePlace** es una aplicación Android desarrollada en **Kotlin**, diseñada como una tienda virtual de café.

---

## 🚀 Características principales  

### 🧩 Requerimientos funcionales
- 🔐 **Autenticación de usuarios** (registro e inicio de sesión).  
- 🧾 **CRUD de productos** (crear, leer, actualizar y eliminar).  
- 🛒 **Gestión de carrito de compras** (agregar, visualizar y eliminar productos).  
- 👤 **Perfil de usuario** con datos personales.  
- 📍 **Mapa con ubicaciones** muestra dos tiendas fijas y la ubicación actual del usuario.
- 🛑 **Gestión de permisos en tiempo real** (cámara, ubicación, almacenamiento).
- 🚪 **Cierre de sesión** seguro y persistente.  

### ⚙️ Requerimientos no funcionales
- 💾 **Integración con Firebase Realtime Database** para el almacenamiento en la nube.  
- 🎨 **Interfaz intuitiva y responsiva** con diseño limpio y moderno.  
- ⚡ **Flujo de navegación fluido** con `Splash`, `Bienvenida`, `Login` , `Registro` , `Perfil`, `Mapa`, `Lista de productos`, `Carrito de compras`.  
- 🔔 **Feedback visual y sonoro** (toasts y animaciones básicas).  
- 📱 **Compatibilidad mínima con Android 8.0 (API 26)** y máxima con Android 15 (API 36).
- 🔒 **Manejo seguro de permisos** y datos del usuario.
- 🗺️ **Rendimiento estable del GPS** y cargas eficientes del mapa.

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin  
- **Entorno:** Android Studio  
- **Base de datos:** Firebase Realtime Database  
- **Arquitectura:** MVVM (ViewModel + LiveData + Repository)  
- **Diseño:** XML y Material Design Components  
- **SDK:** Compile SDK 36 / Min SDK 26  

---

## 📁 Estructura del proyecto

```text
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/
│   └── com.example.coffeeplace/
│        ├── view/
│        │    ├── SplashActivity.kt
│        │    ├── BienvenidaActivity.kt
│        │    ├── LoginActivity.kt
│        │    ├── MapaActivity.kt
│        │    ├── RegistroActivity.kt
│        │    ├── ProductoActivity.kt
│        │    ├── CarritoActivity.kt
│        │    └── PerfilActivity.kt
│        ├── adapter/
│        │    ├── ProductoAdapter.kt
│        │    └── CarritoAdapter.kt
│        ├── model/
│        │    ├── Producto.kt
│        │    ├── CarritoItem.kt
│        │    └── Usuario.kt
│        ├── repository/
│        │    └── FirebaseRepository.kt
│        └── viewModel/
│             └── AppViewModel.kt
└── res/
    ├── layout/
    │    ├── activity_splash.xml
    │    ├── activity_bienvenida.xml
    │    ├── activity_login.xml
    │    ├── activity_mapa.xml
    │    ├── activity_registro.xml
    │    ├── activity_productos.xml
    │    ├── activity_carrito.xml
    │    ├── activity_perfil.xml
    │    ├── item_producto.xml
    │    └── item_carrito.xml
    ├── values/
    │    ├── colors.xml
    │    ├── strings.xml
    │    └── themes.xml
    └── mipmap/
         └── iconplace.png
```

---

## 👨‍💻 Autores

* Oscar Riveros
* Sebastián Paéz
---

## 📚 Curso

2025 2-DESARROLLO DE APLICACIONES MOVILES NATIVAS-2310-6B MOM 2 VIRTUAL
