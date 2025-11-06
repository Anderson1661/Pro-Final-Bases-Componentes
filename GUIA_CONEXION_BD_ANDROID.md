# Guía Completa: Conexión de Aplicación Android con Base de Datos

## Tabla de Contenidos
1. [Arquitectura General](#arquitectura-general)
2. [Requisitos Previos](#requisitos-previos)
3. [Configuración del Backend (PHP)](#configuración-del-backend-php)
   - [Organización por Tablas](#-importante-organización-por-tablas)
   - [Estructura de Carpetas](#estructura-de-carpetas-recomendada-organizada)
   - [Archivo de Conexión](#paso-2-crear-el-archivo-de-conexión-uno-solo-para-todo-el-proyecto)
   - [Endpoints por Tabla](#paso-3-crear-endpoints-para-cada-tabla)
4. [Configuración de la Aplicación Android](#configuración-de-la-aplicación-android)
   - [Clase de Configuración de URLs](#paso-4-crear-clase-de-configuración-de-urls)
   - [Implementación CRUD](#implementación-en-android)
5. [Flujo de Datos Completo](#flujo-de-datos-completo)
6. [Implementación Paso a Paso](#implementación-paso-a-paso)
7. [Mejores Prácticas y Seguridad](#mejores-prácticas-y-seguridad)
8. [Resolución de Problemas Comunes](#resolución-de-problemas-comunes)
9. [Resumen Ejecutivo y Checklist Completo](#resumen-ejecutivo-y-checklist-completo)

---

## Arquitectura General

### ¿Cómo Funciona?

La aplicación Android **NO se conecta directamente** a la base de datos. En su lugar, utiliza una arquitectura **Cliente-Servidor** de tres capas:

```
┌─────────────────┐         HTTP/JSON         ┌──────────────┐         SQL         ┌─────────────┐
│                 │   ──────────────────────>  │              │  ─────────────────>  │             │
│  App Android    │                           │  Servidor    │                      │   Base de   │
│  (Cliente)      │   <────────────────────── │  PHP         │  <─────────────────  │   Datos     │
│                 │         HTTP/JSON         │              │         SQL          │  MySQL      │
└─────────────────┘                           └──────────────┘                       └─────────────┘
```

**Flujo de Comunicación:**
1. La app Android envía una petición HTTP al servidor PHP
2. El servidor PHP procesa la petición y ejecuta consultas SQL
3. La base de datos devuelve los resultados al PHP
4. El PHP formatea la respuesta (JSON o texto) y la envía a Android
5. La app Android recibe y procesa la respuesta

**Ventajas de esta Arquitectura:**
- ✅ Seguridad: La base de datos no está expuesta directamente
- ✅ Flexibilidad: Puedes cambiar el backend sin afectar la app
- ✅ Escalabilidad: Múltiples apps pueden usar el mismo servidor
- ✅ Control: Validación centralizada en el servidor

---

## Requisitos Previos

### 1. Servidor Web con PHP
- **XAMPP** (Windows/Mac/Linux): https://www.apachefriends.org/
- **WAMP** (Windows): https://www.wampserver.com/
- **MAMP** (Mac): https://www.mamp.info/
- **Servidor Linux** con Apache/Nginx + PHP

**Requisitos PHP:**
- PHP 7.0 o superior
- Extensión MySQLi habilitada
- Apache/Nginx configurado

### 2. Base de Datos
- **MySQL** o **MariaDB**
- Acceso a phpMyAdmin o cliente MySQL

### 3. Android Studio
- **Android Studio** (versión estable más reciente)
- **JDK 11** o superior
- **Android SDK** con API nivel 24 como mínimo

### 4. Conectividad
- El dispositivo Android y el servidor deben estar en la misma red
- O el servidor debe ser accesible públicamente (producción)

---

## Configuración del Backend (PHP)

### ⚠️ IMPORTANTE: Organización por Tablas

**Cada tabla de tu base de datos necesita su propio conjunto de archivos PHP** para realizar las operaciones CRUD (Create, Read, Update, Delete). Por ejemplo:

- Si tienes tabla `alumnos` → necesitas: `alumnos/mostrar.php`, `alumnos/registrar.php`, etc.
- Si tienes tabla `usuarios` → necesitas: `usuarios/mostrar.php`, `usuarios/registrar.php`, etc.
- Si tienes tabla `productos` → necesitas: `productos/mostrar.php`, `productos/registrar.php`, etc.

### Estructura de Carpetas Recomendada (Organizada)

```
proyecto_backend/
├── config/
│   └── conexion.php          # ⭐ UN SOLO archivo de conexión compartido
│
├── alumnos/                   # 📁 Carpeta para tabla "alumnos"
│   ├── mostrar.php           # SELECT - Listar todos los alumnos
│   ├── registrar.php         # INSERT - Crear nuevo alumno
│   ├── editar.php            # UPDATE - Actualizar alumno
│   └── eliminar.php          # DELETE - Eliminar alumno
│
├── usuarios/                  # 📁 Carpeta para tabla "usuarios"
│   ├── mostrar.php           # SELECT - Listar usuarios
│   ├── registrar.php         # INSERT - Registrar usuario
│   ├── editar.php            # UPDATE - Editar usuario
│   ├── eliminar.php          # DELETE - Eliminar usuario
│   └── login.php             # SELECT especial - Autenticación
│
├── productos/                 # 📁 Carpeta para tabla "productos"
│   ├── mostrar.php
│   ├── registrar.php
│   ├── editar.php
│   └── eliminar.php
│
└── categorias/                # 📁 Carpeta para tabla "categorias"
    ├── mostrar.php
    ├── registrar.php
    ├── editar.php
    └── eliminar.php
```

### Ventajas de esta Estructura

✅ **Organización clara**: Cada tabla tiene su propia carpeta  
✅ **Fácil mantenimiento**: Encontrar archivos es rápido  
✅ **Escalable**: Agregar nuevas tablas no desordena el proyecto  
✅ **Conexión compartida**: Un solo archivo `conexion.php` para todas las tablas  
✅ **Profesional**: Estructura estándar en proyectos grandes

### Paso 1: Crear la Estructura de Carpetas

**Ubicación:** En tu servidor web (XAMPP: `C:\xampp\htdocs\`, Linux: `/var/www/html/`)

**Crear las carpetas:**
```bash
proyecto_backend/
├── config/          (crear esta carpeta)
├── alumnos/         (crear esta carpeta)
├── usuarios/        (crear esta carpeta)
└── productos/       (crear esta carpeta - si la necesitas)
```

**Verificación 1.1:** 
- [ ] Abre tu explorador de archivos
- [ ] Navega a la carpeta `htdocs` (o `www/html`)
- [ ] Crea la carpeta `proyecto_backend`
- [ ] Dentro de `proyecto_backend`, crea la carpeta `config`
- [ ] Crea las carpetas para cada tabla que tengas en tu BD

---

### Paso 2: Crear el Archivo de Conexión (UNO SOLO para todo el proyecto)

**📁 Ubicación:** `proyecto_backend/config/conexion.php`

**⭐ IMPORTANTE:** Este archivo se crea UNA VEZ y se usa en todos los demás archivos PHP.

```php
<?php
function Conectar(){
    // Configuración de la base de datos
    $host = "localhost";      // Dirección del servidor MySQL
    $user = "root";           // Usuario de MySQL
    $pass = "";               // Contraseña de MySQL (vacío por defecto en XAMPP)
    $dbname = "nombre_bd";    // ⚠️ CAMBIAR: Nombre de tu base de datos
    
    // Establecer conexión
    $link = mysqli_connect($host, $user, $pass);
    
    // Verificar conexión
    if (!$link) {
        die("Error de conexión: " . mysqli_connect_error());
    }
    
    // Seleccionar la base de datos
    mysqli_select_db($link, $dbname);
    
    // Verificar selección de BD
    if (mysqli_errno($link)) {
        die("Error al seleccionar BD: " . mysqli_error($link));
    }
    
    // Establecer codificación UTF-8 (importante para caracteres especiales)
    mysqli_set_charset($link, "utf8");
    
    return $link;
}
?>
```

**Explicación:**
- `mysqli_connect()`: Establece la conexión con MySQL
- `mysqli_select_db()`: Selecciona la base de datos a usar
- `mysqli_set_charset()`: Evita problemas con caracteres especiales (ñ, tildes, etc.)
- `mysqli_errno()` y `mysqli_error()`: Capturan errores para debugging

**Verificación 2.1:**
- [ ] Crear archivo `config/conexion.php`
- [ ] Cambiar `$dbname` por el nombre real de tu base de datos
- [ ] Verificar que el archivo esté guardado correctamente

**Verificación 2.2 - Probar la conexión:**
Crea un archivo temporal `test_conexion.php` en la raíz del proyecto:

```php
<?php
include('config/conexion.php');
$link = Conectar();
if ($link) {
    echo "✅ Conexión exitosa a la base de datos";
} else {
    echo "❌ Error de conexión";
}
mysqli_close($link);
?>
```

- [ ] Abrir en navegador: `http://localhost/proyecto_backend/test_conexion.php`
- [ ] Debe mostrar "✅ Conexión exitosa"
- [ ] Si hay error, verificar credenciales en `conexion.php`
- [ ] **Eliminar `test_conexion.php` después de verificar**

### Paso 3: Crear Endpoints para Cada Tabla

**📋 IMPORTANTE:** Repite este proceso para CADA tabla de tu base de datos.

Por ejemplo, si tienes 3 tablas (`alumnos`, `usuarios`, `productos`), necesitarás crear:
- 3 carpetas (una por tabla)
- 4 archivos PHP en cada carpeta (mostrar, registrar, editar, eliminar)
- Total: 12 archivos PHP + 1 archivo de conexión

---

#### Ejemplo: Tabla "alumnos"

**📁 Ubicación:** `proyecto_backend/alumnos/`

**Tabla de ejemplo:**
```sql
CREATE TABLE alumnos (
    id_a INT PRIMARY KEY AUTO_INCREMENT,
    nomb_a VARCHAR(100),
    apel_a VARCHAR(100),
    email_a VARCHAR(100),
    tel_a VARCHAR(20),
    fecha_a DATE
);
```

---

#### 3.1. Crear Endpoint para Consultar Datos (SELECT)

**📁 Archivo:** `proyecto_backend/alumnos/mostrar.php`

```php
<?php
// Incluir el archivo de conexión (ruta relativa desde esta carpeta)
include('../config/conexion.php');

// Establecer conexión
$link = Conectar();

// Preparar el array de respuesta
$res = array();
$res['datos'] = array();

// Consulta SQL - Seleccionar todos los alumnos
$sql = "SELECT * FROM alumnos";

// Ejecutar consulta
$res1 = mysqli_query($link, $sql);

// Verificar si hay resultados
if ($res1) {
    // Verificar si hay filas
    if (mysqli_num_rows($res1) > 0) {
        // Recorrer cada fila
        while ($row = mysqli_fetch_array($res1)) {
            // Crear objeto con los datos de cada fila
            $item = array();
            $item['id_a'] = $row['id_a'];           // ID del alumno
            $item['nomb_a'] = $row['nomb_a'];        // Nombre
            $item['apel_a'] = $row['apel_a'];        // Apellido
            $item['email_a'] = $row['email_a'];       // Email
            $item['tel_a'] = $row['tel_a'];          // Teléfono
            $item['fecha_a'] = $row['fecha_a'];      // Fecha
            
            // Agregar al array de datos
            array_push($res['datos'], $item);
        }
        
        // Indicar éxito
        $res["success"] = "1";
    } else {
        // No hay datos
        $res["success"] = "1";
        $res["mensaje"] = "No hay alumnos registrados";
    }
} else {
    // Error en la consulta
    $res["success"] = "0";
    $res["mensaje"] = "Error al consultar datos: " . mysqli_error($link);
}

// Establecer header para JSON
header('Content-Type: application/json; charset=utf-8');

// Convertir a JSON y enviar respuesta
echo json_encode($res, JSON_UNESCAPED_UNICODE);

// Cerrar conexión
mysqli_close($link);
?>
```

**📝 Notas importantes:**
- `include('../config/conexion.php')`: La ruta `../` sube un nivel desde `alumnos/` a `config/`
- `$row['nombre_columna']`: Acceso por nombre de columna (más seguro que por índice)
- `JSON_UNESCAPED_UNICODE`: Evita problemas con caracteres especiales

**Verificación 3.1.1:**
- [ ] Crear carpeta `alumnos/` dentro de `proyecto_backend/`
- [ ] Crear archivo `alumnos/mostrar.php` con el código anterior
- [ ] **Ajustar los nombres de columnas** según tu tabla real
- [ ] Guardar el archivo

**Verificación 3.1.2 - Probar en navegador:**
- [ ] Abrir: `http://localhost/proyecto_backend/alumnos/mostrar.php`
- [ ] Debe mostrar JSON con estructura:
```json
{
  "success": "1",
  "datos": [
    {
      "id_a": "1",
      "nomb_a": "Juan",
      "apel_a": "Pérez",
      ...
    }
  ]
}
```
- [ ] Si hay error, revisar:
  - [ ] Ruta del archivo `conexion.php` es correcta
  - [ ] Los nombres de columnas coinciden con tu tabla
  - [ ] La base de datos existe y tiene datos

**Estructura de Respuesta JSON:**
```json
{
  "success": "1",
  "datos": [
    {
      "id": "1",
      "campo1": "Valor 1",
      "campo2": "Valor 2"
    },
    {
      "id": "2",
      "campo1": "Valor 3",
      "campo2": "Valor 4"
    }
  ]
}
```

#### 3.2. Crear Endpoint para Insertar Datos (INSERT)

**📁 Archivo:** `proyecto_backend/alumnos/registrar.php`

```php
<?php
// Incluir conexión
include('../config/conexion.php');

// Establecer conexión
$link = Conectar();

// Recibir parámetros desde Android
$cod = isset($_REQUEST['cod']) ? $_REQUEST['cod'] : '';
$nom = isset($_REQUEST['nom']) ? $_REQUEST['nom'] : '';
$ape = isset($_REQUEST['ape']) ? $_REQUEST['ape'] : '';
$em = isset($_REQUEST['em']) ? $_REQUEST['em'] : '';
$tel = isset($_REQUEST['tel']) ? $_REQUEST['tel'] : '';
$fen = isset($_REQUEST['fen']) ? $_REQUEST['fen'] : '';

// Validar que los campos no estén vacíos
if (empty($cod) || empty($nom) || empty($ape) || empty($em) || empty($tel) || empty($fen)) {
    echo "ERROR: Todos los campos son requeridos";
} else {
    // Sanitizar datos para prevenir SQL Injection
    $cod = mysqli_real_escape_string($link, $cod);
    $nom = mysqli_real_escape_string($link, $nom);
    $ape = mysqli_real_escape_string($link, $ape);
    $em = mysqli_real_escape_string($link, $em);
    $tel = mysqli_real_escape_string($link, $tel);
    $fen = mysqli_real_escape_string($link, $fen);
    
    // Construir consulta SQL
    $sql = "INSERT INTO alumnos (id_a, nomb_a, apel_a, email_a, tel_a, fecha_a) 
            VALUES ('$cod', '$nom', '$ape', '$em', '$tel', '$fen')";
    
    // Ejecutar consulta
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo "Alumno Registrado";
    } else {
        echo "ERROR: No se pudieron registrar los datos. " . mysqli_error($link);
    }
}

// Cerrar conexión
mysqli_close($link);
?>
```

**📝 Notas importantes:**
- `isset($_REQUEST['campo'])`: Verifica que el parámetro exista
- `mysqli_real_escape_string()`: Previene SQL Injection
- Ajusta los nombres de columnas según tu tabla

**Verificación 3.2.1:**
- [ ] Crear archivo `alumnos/registrar.php`
- [ ] Ajustar nombres de columnas y parámetros según tu tabla
- [ ] Guardar el archivo

**Verificación 3.2.2 - Probar con navegador (simular petición Android):**
- [ ] Abrir: `http://localhost/proyecto_backend/alumnos/registrar.php?cod=123&nom=Juan&ape=Perez&em=test@test.com&tel=123456&fen=2024-01-01`
- [ ] Debe mostrar: "Alumno Registrado"
- [ ] Verificar en phpMyAdmin que el registro se insertó
- [ ] Si hay error, revisar nombres de columnas y estructura de tabla

#### 3.3. Crear Endpoint para Actualizar Datos (UPDATE)

**📁 Archivo:** `proyecto_backend/alumnos/editar.php`

```php
<?php
// Verificar que sea una petición POST (recomendado)
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    // Recibir parámetros
    $id = isset($_REQUEST['cod']) ? $_REQUEST['cod'] : '';
    $nom = isset($_REQUEST['nom']) ? $_REQUEST['nom'] : '';
    $ape = isset($_REQUEST['ape']) ? $_REQUEST['ape'] : '';
    $em = isset($_REQUEST['em']) ? $_REQUEST['em'] : '';
    $tel = isset($_REQUEST['tel']) ? $_REQUEST['tel'] : '';
    $fen = isset($_REQUEST['fen']) ? $_REQUEST['fen'] : '';
    
    // Validar que el ID no esté vacío
    if (empty($id)) {
        echo "ERROR: ID requerido";
    } else {
        // Sanitizar datos
        $id = mysqli_real_escape_string($link, $id);
        $nom = mysqli_real_escape_string($link, $nom);
        $ape = mysqli_real_escape_string($link, $ape);
        $em = mysqli_real_escape_string($link, $em);
        $tel = mysqli_real_escape_string($link, $tel);
        $fen = mysqli_real_escape_string($link, $fen);
        
        // Construir consulta UPDATE
        $sql = "UPDATE alumnos 
                SET nomb_a='$nom', 
                    apel_a='$ape', 
                    email_a='$em', 
                    tel_a='$tel', 
                    fecha_a='$fen' 
                WHERE id_a='$id'";
        
        // Ejecutar consulta
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            // Verificar si se actualizó alguna fila
            if (mysqli_affected_rows($link) > 0) {
                echo "Alumno Actualizado";
            } else {
                echo "ERROR: No se encontró el alumno con ese ID";
            }
        } else {
            echo "ERROR: No se pudieron actualizar los datos. " . mysqli_error($link);
        }
    }
    
    mysqli_close($link);
} else {
    echo "ERROR: Método no permitido. Use POST.";
}
?>
```

**📝 Notas importantes:**
- `mysqli_affected_rows()`: Verifica cuántas filas se actualizaron
- El `WHERE` debe usar el campo de ID único de tu tabla

**Verificación 3.3.1:**
- [ ] Crear archivo `alumnos/editar.php`
- [ ] Ajustar nombres de columnas según tu tabla
- [ ] Verificar que el campo del WHERE sea el correcto (generalmente el ID)

**Verificación 3.3.2 - Probar:**
- [ ] Usar herramienta como Postman o crear formulario HTML temporal
- [ ] Enviar petición POST con parámetros
- [ ] Verificar que el registro se actualice en la BD

#### 3.4. Crear Endpoint para Eliminar Datos (DELETE)

**📁 Archivo:** `proyecto_backend/alumnos/eliminar.php`

```php
<?php
// Verificar que sea una petición POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    // Recibir ID del registro a eliminar
    $id = isset($_REQUEST['id']) ? $_REQUEST['id'] : '';
    
    // Validar que el ID no esté vacío
    if (empty($id)) {
        echo "ERROR: ID requerido";
    } else {
        // Sanitizar ID
        $id = mysqli_real_escape_string($link, $id);
        
        // Construir consulta DELETE
        $sql = "DELETE FROM alumnos WHERE id_a='$id'";
        
        // Ejecutar consulta
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            // Verificar si se eliminó alguna fila
            if (mysqli_affected_rows($link) > 0) {
                echo "Alumno Eliminado";
            } else {
                echo "ERROR: No se encontró el alumno con ese ID";
            }
        } else {
            echo "ERROR: No se pudieron eliminar los datos. " . mysqli_error($link);
        }
    }
    
    mysqli_close($link);
} else {
    echo "ERROR: Método no permitido. Use POST.";
}
?>
```

**📝 Notas importantes:**
- ⚠️ **CUIDADO:** DELETE elimina permanentemente. Considera usar "soft delete" (marcar como eliminado) en producción
- `mysqli_affected_rows()`: Verifica cuántas filas se eliminaron

**Verificación 3.4.1:**
- [ ] Crear archivo `alumnos/eliminar.php`
- [ ] Verificar que el campo del WHERE sea el correcto

**Verificación 3.4.2 - Probar:**
- [ ] Hacer petición POST con un ID existente
- [ ] Verificar que el registro se elimine de la BD
- [ ] Probar con ID inexistente (debe mostrar error apropiado)

---

### Paso 4: Repetir el Proceso para Cada Tabla

**📋 Checklist para cada tabla nueva:**

1. **Crear carpeta:**
   - [ ] Crear carpeta `nombre_tabla/` dentro de `proyecto_backend/`

2. **Crear archivos PHP:**
   - [ ] `nombre_tabla/mostrar.php` (SELECT)
   - [ ] `nombre_tabla/registrar.php` (INSERT)
   - [ ] `nombre_tabla/editar.php` (UPDATE)
   - [ ] `nombre_tabla/eliminar.php` (DELETE)

3. **Ajustar en cada archivo:**
   - [ ] Ruta de conexión: `include('../config/conexion.php')`
   - [ ] Nombre de tabla en SQL: `FROM nombre_tabla`
   - [ ] Nombres de columnas según tu estructura
   - [ ] Nombres de parámetros según tu modelo Android

4. **Probar cada endpoint:**
   - [ ] Probar `mostrar.php` en navegador
   - [ ] Probar `registrar.php` con datos de prueba
   - [ ] Probar `editar.php` con ID existente
   - [ ] Probar `eliminar.php` con ID existente

---

### Ejemplo Completo: Tabla "usuarios"

**Estructura de tabla:**
```sql
CREATE TABLE usuarios (
    id_u INT PRIMARY KEY AUTO_INCREMENT,
    user_u VARCHAR(50) UNIQUE,
    pass_u VARCHAR(255),
    email_u VARCHAR(100)
);
```

**📁 Archivo:** `proyecto_backend/usuarios/mostrar.php`
```php
<?php
include('../config/conexion.php');
$link = Conectar();
$res = array();
$res['datos'] = array();
$sql = "SELECT * FROM usuarios";
$res1 = mysqli_query($link, $sql);
if ($res1) {
    while ($row = mysqli_fetch_array($res1)) {
        $item = array();
        $item['id_u'] = $row['id_u'];
        $item['user_u'] = $row['user_u'];
        $item['email_u'] = $row['email_u'];
        // ⚠️ NO enviar contraseñas en la respuesta
        array_push($res['datos'], $item);
    }
    $res["success"] = "1";
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Error: " . mysqli_error($link);
}
header('Content-Type: application/json; charset=utf-8');
echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
```

**📁 Archivo:** `proyecto_backend/usuarios/login.php` (Ejemplo especial)
```php
<?php
include('../config/conexion.php');
$link = Conectar();
$user = isset($_REQUEST['user']) ? $_REQUEST['user'] : '';
$pass = isset($_REQUEST['passw']) ? $_REQUEST['passw'] : '';

if (empty($user) || empty($pass)) {
    echo "ERROR: Campos vacíos";
} else {
    $user = mysqli_real_escape_string($link, $user);
    $pass = mysqli_real_escape_string($link, $pass);
    
    // ⚠️ En producción, usar hash de contraseña
    $sql = "SELECT * FROM usuarios WHERE user_u = '$user' AND pass_u = '$pass'";
    $res = mysqli_query($link, $sql);
    
    if ($res && mysqli_num_rows($res) > 0) {
        $data = array();
        while ($row = mysqli_fetch_assoc($res)) {
            // No incluir contraseña en respuesta
            $item = array();
            $item['id_u'] = $row['id_u'];
            $item['user_u'] = $row['user_u'];
            $item['email_u'] = $row['email_u'];
            $data[] = $item;
        }
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode($data);
    } else {
        echo "ERROR: Usuario o contraseña incorrectos";
    }
}
mysqli_close($link);
?>
```

### Paso 5: Configurar el Servidor y Obtener IP

**5.1. Colocar archivos PHP en el servidor:**

**Ubicaciones según el servidor:**
- **XAMPP (Windows):** `C:\xampp\htdocs\proyecto_backend\`
- **XAMPP (Mac):** `/Applications/XAMPP/htdocs/proyecto_backend/`
- **XAMPP (Linux):** `/opt/lampp/htdocs/proyecto_backend/`
- **WAMP (Windows):** `C:\wamp64\www\proyecto_backend\`
- **MAMP (Mac):** `/Applications/MAMP/htdocs/proyecto_backend/`
- **Linux Apache:** `/var/www/html/proyecto_backend/`

**Verificación 5.1:**
- [ ] Copiar toda la carpeta `proyecto_backend` con su estructura completa
- [ ] Verificar que la estructura de carpetas se mantenga:
  ```
  proyecto_backend/
  ├── config/
  │   └── conexion.php
  ├── alumnos/
  │   ├── mostrar.php
  │   ├── registrar.php
  │   ├── editar.php
  │   └── eliminar.php
  └── usuarios/
      └── ...
  ```

**5.2. Iniciar el servidor:**

- **XAMPP:** Abrir XAMPP Control Panel → Iniciar Apache y MySQL
- **WAMP:** Click en el icono de WAMP → Start All Services
- **MAMP:** Abrir MAMP → Start Servers

**Verificación 5.2:**
- [ ] Verificar que Apache esté corriendo (debe aparecer en verde)
- [ ] Verificar que MySQL esté corriendo (debe aparecer en verde)
- [ ] Abrir navegador: `http://localhost` → Debe mostrar página de XAMPP/WAMP/MAMP

**5.3. Obtener la IP del servidor:**

**Windows:**
```cmd
ipconfig
```
Buscar "Dirección IPv4" (ejemplo: `192.168.0.101`)

**Linux/Mac:**
```bash
ifconfig
# o
ip addr
```
Buscar la dirección IP en la interfaz de red activa

**Verificación 5.3:**
- [ ] Ejecutar comando según tu sistema operativo
- [ ] Anotar la dirección IP (ejemplo: `192.168.0.101`)
- [ ] ⚠️ **IMPORTANTE:** Esta IP cambiará si cambias de red WiFi

**5.4. Probar endpoints localmente:**

**Verificación 5.4.1 - Probar desde navegador:**
- [ ] Abrir: `http://localhost/proyecto_backend/config/conexion.php` (debe mostrar error o estar vacío, es normal)
- [ ] Abrir: `http://localhost/proyecto_backend/alumnos/mostrar.php`
- [ ] Debe mostrar JSON con datos (o array vacío si no hay datos)
- [ ] Si hay error, revisar:
  - [ ] Apache está corriendo
  - [ ] MySQL está corriendo
  - [ ] La base de datos existe
  - [ ] La tabla existe y tiene el nombre correcto

**Verificación 5.4.2 - Probar desde dispositivo Android:**
- [ ] Conectar dispositivo Android a la misma red WiFi que el servidor
- [ ] Abrir navegador en el dispositivo
- [ ] Ir a: `http://TU_IP/proyecto_backend/alumnos/mostrar.php`
  - Ejemplo: `http://192.168.0.101/proyecto_backend/alumnos/mostrar.php`
- [ ] Debe mostrar el mismo JSON que en localhost
- [ ] Si no funciona, verificar:
  - [ ] Firewall de Windows/Linux puede estar bloqueando
  - [ ] Dispositivo y servidor en la misma red
  - [ ] IP es correcta

**5.5. Configurar Firewall (si es necesario):**

**Windows:**
1. Panel de Control → Sistema y Seguridad → Firewall de Windows
2. Permitir una aplicación → Apache HTTP Server → Permitir

**Linux:**
```bash
sudo ufw allow 80/tcp
```

**Verificación 5.5:**
- [ ] Si no puedes acceder desde Android, desactivar temporalmente el firewall
- [ ] Probar nuevamente desde el dispositivo
- [ ] Si funciona, configurar excepción permanente en firewall

---

## Configuración de la Aplicación Android

### Paso 1: Configurar Permisos en AndroidManifest.xml

**Archivo: `app/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permisos necesarios para conexión a internet -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.AppCompat"
        android:usesCleartextTraffic="true">
        <!-- usesCleartextTraffic permite HTTP (no solo HTTPS) -->
        
        <!-- Tus actividades aquí -->
        
    </application>
</manifest>
```

**Explicación de permisos:**
- `INTERNET`: Permite conexiones a internet
- `ACCESS_NETWORK_STATE`: Verifica si hay conexión disponible
- `ACCESS_WIFI_STATE`: Accede al estado de WiFi
- `usesCleartextTraffic`: Permite HTTP (necesario para desarrollo local)

### Paso 2: Agregar Dependencia Volley en build.gradle.kts

**Archivo: `app/build.gradle.kts`**

```kotlin
dependencies {
    // ... otras dependencias ...
    
    // Librería Volley para peticiones HTTP
    implementation("com.android.volley:volley:1.2.1")
    
    // Librerías de AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
```

**Sincronizar proyecto:**
- Click en "Sync Now" cuando aparezca la notificación
- O: File → Sync Project with Gradle Files

### Paso 3: Crear Clase Modelo de Datos

**Archivo: `app/src/main/java/com/tu_paquete/Modelo.java`**

```java
package com.tu_paquete;

public class Modelo {
    private String id;
    private String campo1;
    private String campo2;
    private String campo3;
    
    // Constructor
    public Modelo(String id, String campo1, String campo2, String campo3) {
        this.id = id;
        this.campo1 = campo1;
        this.campo2 = campo2;
        this.campo3 = campo3;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCampo1() {
        return campo1;
    }
    
    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }
    
    public String getCampo2() {
        return campo2;
    }
    
    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }
    
    public String getCampo3() {
        return campo3;
    }
    
    public void setCampo3(String campo3) {
        this.campo3 = campo3;
    }
}
```

### Paso 4: Crear Clase de Configuración de URLs

**📁 Archivo:** `app/src/main/java/com/tu_paquete/Config.java`

```java
package com.tu_paquete;

public class Config {
    // ⚠️ CAMBIAR: IP de tu servidor
    private static final String SERVER_IP = "192.168.0.101";
    private static final String BASE_URL = "http://" + SERVER_IP + "/proyecto_backend/";
    
    // URLs para tabla "alumnos"
    public static final String URL_ALUMNOS_MOSTRAR = BASE_URL + "alumnos/mostrar.php";
    public static final String URL_ALUMNOS_REGISTRAR = BASE_URL + "alumnos/registrar.php";
    public static final String URL_ALUMNOS_EDITAR = BASE_URL + "alumnos/editar.php";
    public static final String URL_ALUMNOS_ELIMINAR = BASE_URL + "alumnos/eliminar.php";
    
    // URLs para tabla "usuarios"
    public static final String URL_USUARIOS_MOSTRAR = BASE_URL + "usuarios/mostrar.php";
    public static final String URL_USUARIOS_REGISTRAR = BASE_URL + "usuarios/registrar.php";
    public static final String URL_USUARIOS_EDITAR = BASE_URL + "usuarios/editar.php";
    public static final String URL_USUARIOS_ELIMINAR = BASE_URL + "usuarios/eliminar.php";
    public static final String URL_USUARIOS_LOGIN = BASE_URL + "usuarios/login.php";
    
    // Agregar más URLs según tus tablas
}
```

**Ventajas de esta clase:**
- ✅ Un solo lugar para cambiar la IP
- ✅ URLs organizadas por tabla
- ✅ Fácil de mantener y actualizar

**Verificación 4.1:**
- [ ] Crear archivo `Config.java`
- [ ] Cambiar `SERVER_IP` por tu IP real
- [ ] Agregar URLs para todas tus tablas

---

### Paso 5: Implementar Consulta de Datos (GET)

**Ejemplo en Activity:**

```java
package com.tu_paquete;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListaAlumnosActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Alumnos> listaAlumnos = new ArrayList<>();
    // Usar la clase Config para obtener la URL
    String url = Config.URL_ALUMNOS_MOSTRAR;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        
        listView = findViewById(R.id.listView);
        
        // Cargar datos
        cargarDatos();
    }
    
    private void cargarDatos() {
        // Crear petición HTTP
        StringRequest request = new StringRequest(
            Request.Method.POST, 
            url, 
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Limpiar lista anterior
                    listaDatos.clear();
                    
                    try {
                        // Parsear respuesta JSON
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("datos");
                        
                        if (success.equals("1")) {
                            // Recorrer cada elemento del JSON
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                
                                String id = object.getString("id");
                                String campo1 = object.getString("campo1");
                                String campo2 = object.getString("campo2");
                                String campo3 = object.getString("campo3");
                                
                                // Crear objeto modelo
                                Modelo modelo = new Modelo(id, campo1, campo2, campo3);
                                
                                // Agregar a la lista
                                listaDatos.add(modelo);
                            }
                            
                            // Actualizar el adaptador
                            // adapter.notifyDataSetChanged();
                            
                        } else {
                            Toast.makeText(ListaActivity.this, 
                                "No hay datos disponibles", 
                                Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ListaActivity.this, 
                            "Error al procesar datos", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }, 
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ListaActivity.this, 
                        "Error de conexión: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // Agregar petición a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
```

### Paso 6: Implementar Inserción de Datos (POST)

```java
private void registrarAlumno() {
    // Obtener valores de los EditText
    final String cod = edtCod.getText().toString().trim();
    final String nom = edtNom.getText().toString().trim();
    final String ape = edtApe.getText().toString().trim();
    final String em = edtEm.getText().toString().trim();
    final String tel = edtTel.getText().toString().trim();
    final String fen = edtFen.getText().toString().trim();
    
    // Validar campos
    if (cod.isEmpty() || nom.isEmpty() || ape.isEmpty() || 
        em.isEmpty() || tel.isEmpty() || fen.isEmpty()) {
        Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // URL del endpoint usando Config
    String url = Config.URL_ALUMNOS_REGISTRAR;
    
    // Crear petición POST
    StringRequest stringRequest = new StringRequest(
        Request.Method.POST, 
        url, 
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("Datos Registrados Correctamente")) {
                    Toast.makeText(RegistrarActivity.this, 
                        "Registro exitoso", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Limpiar campos
                    edtCampo1.setText("");
                    edtCampo2.setText("");
                    edtCampo3.setText("");
                    
                    // Opcional: volver a la lista
                    // startActivity(new Intent(this, ListaActivity.class));
                } else {
                    Toast.makeText(RegistrarActivity.this, 
                        response, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, 
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrarActivity.this, 
                    "Error: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        }
    ) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            // Parámetros que se enviarán al servidor
            Map<String, String> params = new HashMap<>();
            params.put("campo1", campo1);
            params.put("campo2", campo2);
            params.put("campo3", campo3);
            return params;
        }
    };
    
    // Ejecutar petición
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
```

### Paso 6: Implementar Actualización de Datos

```java
private void actualizarAlumno(final String id) {
    final String cod = edtCod.getText().toString().trim();
    final String nom = edtNom.getText().toString().trim();
    final String ape = edtApe.getText().toString().trim();
    final String em = edtEm.getText().toString().trim();
    final String tel = edtTel.getText().toString().trim();
    final String fen = edtFen.getText().toString().trim();
    
    // Usar Config para obtener la URL
    String url = Config.URL_ALUMNOS_EDITAR;
    
    StringRequest stringRequest = new StringRequest(
        Request.Method.POST, 
        url, 
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("Datos Actualizados Correctamente")) {
                    Toast.makeText(EditarActivity.this, 
                        "Actualización exitosa", 
                        Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar actividad
                } else {
                    Toast.makeText(EditarActivity.this, 
                        response, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, 
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditarActivity.this, 
                    "Error: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        }
    ) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("id", id);
            params.put("campo1", campo1);
            params.put("campo2", campo2);
            params.put("campo3", campo3);
            return params;
        }
    };
    
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
```

### Paso 7: Implementar Eliminación de Datos

```java
private void eliminarAlumno(final String id) {
    // Usar Config para obtener la URL
    String url = Config.URL_ALUMNOS_ELIMINAR;
    
    StringRequest stringRequest = new StringRequest(
        Request.Method.POST, 
        url, 
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("Datos Eliminados Correctamente")) {
                    Toast.makeText(ListaActivity.this, 
                        "Eliminado exitosamente", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Recargar la lista
                    cargarDatos();
                } else {
                    Toast.makeText(ListaActivity.this, 
                        response, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, 
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListaActivity.this, 
                    "Error: " + error.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        }
    ) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("id", id);
            return params;
        }
    };
    
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
```

---

## Flujo de Datos Completo

### Ejemplo: Mostrar Lista de Productos

```
1. Usuario abre la app
   ↓
2. Activity llama a cargarDatos()
   ↓
3. Volley crea petición HTTP POST
   URL: http://192.168.0.101/proyecto/mostrar.php
   ↓
4. Servidor PHP recibe petición
   ↓
5. PHP ejecuta: SELECT * FROM productos
   ↓
6. MySQL devuelve resultados
   ↓
7. PHP formatea a JSON:
   {
     "success": "1",
     "datos": [
       {"id": "1", "nombre": "Producto 1"},
       {"id": "2", "nombre": "Producto 2"}
     ]
   }
   ↓
8. Android recibe JSON en onResponse()
   ↓
9. Se parsea JSON con JSONObject y JSONArray
   ↓
10. Se crean objetos Modelo y se agregan a ArrayList
   ↓
11. Adapter actualiza ListView/RecyclerView
   ↓
12. Usuario ve la lista en pantalla
```

---

## Implementación Paso a Paso

### Checklist de Implementación

#### Backend (PHP)
- [ ] Instalar servidor web (XAMPP/WAMP/MAMP)
- [ ] Crear base de datos en MySQL
- [ ] Crear tabla(s) con campos necesarios
- [ ] Crear archivo `conexion.php`
- [ ] Crear endpoint `mostrar.php` (SELECT)
- [ ] Crear endpoint `registrar.php` (INSERT)
- [ ] Crear endpoint `editar.php` (UPDATE)
- [ ] Crear endpoint `eliminar.php` (DELETE)
- [ ] Probar endpoints en navegador
- [ ] Obtener IP del servidor

#### Android
- [ ] Crear proyecto en Android Studio
- [ ] Configurar permisos en AndroidManifest.xml
- [ ] Agregar dependencia Volley en build.gradle
- [ ] Crear clase modelo de datos
- [ ] Crear Activity para mostrar lista
- [ ] Implementar método cargarDatos()
- [ ] Crear Activity para registrar
- [ ] Implementar método registrarDatos()
- [ ] Crear Activity para editar
- [ ] Implementar método actualizarDatos()
- [ ] Implementar método eliminarDatos()
- [ ] Probar en dispositivo/emulador

---

## Mejores Prácticas y Seguridad

### 1. Validación en el Servidor

**Siempre valida en PHP, nunca confíes en la app:**

```php
// Validar que los datos no estén vacíos
if (empty($campo1) || empty($campo2)) {
    echo "ERROR: Campos requeridos";
    exit;
}

// Validar tipo de dato
if (!is_numeric($id)) {
    echo "ERROR: ID inválido";
    exit;
}

// Sanitizar datos para prevenir SQL Injection
$campo1 = mysqli_real_escape_string($link, $campo1);
$campo2 = mysqli_real_escape_string($link, $campo2);
```

### 2. Usar Prepared Statements (Recomendado)

**Más seguro que concatenar strings:**

```php
// Preparar consulta
$stmt = $link->prepare("INSERT INTO tabla (campo1, campo2) VALUES (?, ?)");
$stmt->bind_param("ss", $campo1, $campo2); // "ss" = dos strings
$stmt->execute();
```

### 3. Manejo de Errores Mejorado

```php
// En PHP
if (!$res) {
    $error = array();
    $error["success"] = "0";
    $error["mensaje"] = mysqli_error($link);
    echo json_encode($error);
    exit;
}
```

```java
// En Android
try {
    JSONObject jsonObject = new JSONObject(response);
    if (jsonObject.has("success")) {
        String success = jsonObject.getString("success");
        if (success.equals("1")) {
            // Procesar datos
        } else {
            String mensaje = jsonObject.getString("mensaje");
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }
} catch (JSONException e) {
    Log.e("Error", "Error parsing JSON: " + e.getMessage());
}
```

### 4. Configuración de URLs

**Crear clase de configuración:**

```java
public class Config {
    // Cambiar esta IP según tu servidor
    public static final String SERVER_URL = "http://192.168.0.101/tu_proyecto/";
    
    public static final String URL_MOSTRAR = SERVER_URL + "mostrar.php";
    public static final String URL_REGISTRAR = SERVER_URL + "registrar.php";
    public static final String URL_EDITAR = SERVER_URL + "editar.php";
    public static final String URL_ELIMINAR = SERVER_URL + "eliminar.php";
}
```

**Uso:**
```java
String url = Config.URL_MOSTRAR;
```

### 5. Indicadores de Carga

```java
// Mostrar ProgressDialog mientras carga
ProgressDialog progressDialog = new ProgressDialog(this);
progressDialog.setMessage("Cargando...");
progressDialog.show();

// En onResponse() y onErrorResponse():
progressDialog.dismiss();
```

### 6. Manejo de Conexión

```java
// Verificar conexión antes de hacer petición
private boolean hayConexion() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
}

// Usar:
if (!hayConexion()) {
    Toast.makeText(this, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
    return;
}
```

### 7. Para Producción

- **Usar HTTPS** en lugar de HTTP
- **Autenticación**: Implementar tokens (JWT)
- **Rate Limiting**: Limitar peticiones por usuario
- **CORS**: Configurar headers adecuados si es necesario
- **Logging**: Registrar errores en servidor

---

## Resolución de Problemas Comunes

### Problema 1: "NetworkOnMainThreadException"

**Causa:** Volley ya maneja esto automáticamente, pero si usas otras librerías:

**Solución:** Usar AsyncTask o Thread (Volley ya lo hace internamente)

### Problema 2: "No se puede conectar al servidor"

**Soluciones:**
1. Verificar que el servidor esté corriendo
2. Verificar que la IP sea correcta
3. Verificar que el dispositivo y servidor estén en la misma red
4. Verificar firewall del servidor
5. Probar URL en navegador del dispositivo

### Problema 3: "Error parsing JSON"

**Causas:**
- El PHP devuelve texto en lugar de JSON
- El JSON está mal formado
- Hay caracteres especiales sin escapar

**Solución:**
```php
// Asegurar que la respuesta sea JSON válido
header('Content-Type: application/json');
echo json_encode($res);
```

### Problema 4: "Datos no se muestran"

**Verificar:**
1. Que el JSON tenga la estructura correcta
2. Que los nombres de las claves coincidan
3. Que el adapter esté actualizado: `adapter.notifyDataSetChanged()`

### Problema 5: "SQL Injection"

**Solución:** Usar `mysqli_real_escape_string()` o prepared statements

### Problema 6: "Código 403 Forbidden"

**Causa:** Permisos del servidor

**Solución:** Verificar permisos de archivos PHP:
```bash
chmod 644 *.php
```

### Problema 7: "Caracteres especiales se ven mal"

**Solución:**
```php
// En PHP
mysqli_set_charset($link, "utf8");
header('Content-Type: application/json; charset=utf-8');
```

```java
// En Android, Volley maneja UTF-8 automáticamente
```

---

## Ejemplo Completo: Sistema de Tareas

### Base de Datos

```sql
CREATE DATABASE tareas_db;
USE tareas_db;

CREATE TABLE tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha DATE,
    completada TINYINT(1) DEFAULT 0
);
```

### Backend PHP

**conexion.php:**
```php
<?php
function Conectar(){
    $host = "localhost";
    $user = "root";
    $pass = "";
    $dbname = "tareas_db";
    $link = mysqli_connect($host, $user, $pass);
    mysqli_select_db($link, $dbname);
    mysqli_set_charset($link, "utf8");
    return $link;
}
?>
```

**mostrar.php:**
```php
<?php
include('conexion.php');
$link = Conectar();
$res = array();
$res['datos'] = array();
$sql = "SELECT * FROM tareas";
$res1 = mysqli_query($link, $sql);
while($row = mysqli_fetch_array($res1)){
    $i = array();
    $i['id'] = $row[0];
    $i['titulo'] = $row[1];
    $i['descripcion'] = $row[2];
    $i['fecha'] = $row[3];
    $i['completada'] = $row[4];
    array_push($res['datos'], $i);
}
$res["success"] = "1";
echo json_encode($res);
mysqli_close($link);
?>
```

### Android

**Tarea.java (Modelo):**
```java
public class Tarea {
    private String id, titulo, descripcion, fecha;
    private int completada;
    
    public Tarea(String id, String titulo, String descripcion, String fecha, int completada) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.completada = completada;
    }
    
    // Getters y Setters...
}
```

**ListaTareasActivity.java:**
```java
public class ListaTareasActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Tarea> tareasList = new ArrayList<>();
    String url = "http://192.168.0.101/tareas/mostrar.php";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tareas);
        listView = findViewById(R.id.listView);
        cargarTareas();
    }
    
    private void cargarTareas() {
        StringRequest request = new StringRequest(Request.Method.POST, url,
            response -> {
                tareasList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("datos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Tarea tarea = new Tarea(
                            obj.getString("id"),
                            obj.getString("titulo"),
                            obj.getString("descripcion"),
                            obj.getString("fecha"),
                            obj.getInt("completada")
                        );
                        tareasList.add(tarea);
                    }
                    // Configurar adapter
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
```

---

## Recursos Adicionales

### Documentación Oficial
- **Volley**: https://developer.android.com/training/volley
- **PHP MySQLi**: https://www.php.net/manual/es/book.mysqli.php
- **JSON en Android**: https://developer.android.com/reference/org/json/package-summary

### Herramientas Útiles
- **Postman**: Para probar endpoints PHP
- **phpMyAdmin**: Para gestionar base de datos
- **JSON Viewer**: Extensiones de navegador para ver JSON formateado

### Comandos Útiles

**Obtener IP en Windows:**
```cmd
ipconfig
```

**Obtener IP en Linux/Mac:**
```bash
ifconfig
# o
ip addr
```

**Probar conexión desde Android a servidor:**
```java
// Agregar en código para debug
Log.d("URL", "Conectando a: " + url);
```

---

## Resumen Ejecutivo y Checklist Completo

### 📋 Checklist General del Proyecto

#### FASE 1: Preparación del Backend

**1.1. Configuración Inicial:**
- [ ] Instalar XAMPP/WAMP/MAMP
- [ ] Iniciar Apache y MySQL
- [ ] Crear base de datos en phpMyAdmin
- [ ] Crear todas las tablas necesarias
- [ ] Anotar nombres de tablas y columnas

**1.2. Estructura de Carpetas Backend:**
- [ ] Crear carpeta `proyecto_backend/`
- [ ] Crear carpeta `proyecto_backend/config/`
- [ ] Crear carpeta por cada tabla: `proyecto_backend/nombre_tabla/`
  - Ejemplo: `proyecto_backend/alumnos/`
  - Ejemplo: `proyecto_backend/usuarios/`
  - Ejemplo: `proyecto_backend/productos/`

**1.3. Archivo de Conexión:**
- [ ] Crear `config/conexion.php`
- [ ] Configurar credenciales de BD
- [ ] Probar conexión con `test_conexion.php`
- [ ] Eliminar `test_conexion.php` después de verificar

---

#### FASE 2: Crear Endpoints PHP por Cada Tabla

**Para CADA tabla, crear los siguientes archivos:**

**Tabla: [nombre_tabla]**

**2.1. Endpoint SELECT (mostrar.php):**
- [ ] Crear `nombre_tabla/mostrar.php`
- [ ] Incluir `../config/conexion.php`
- [ ] Ajustar nombres de columnas en SQL
- [ ] Ajustar nombres de campos en respuesta JSON
- [ ] Probar en navegador: `http://localhost/proyecto_backend/nombre_tabla/mostrar.php`
- [ ] Verificar que el JSON se muestre correctamente

**2.2. Endpoint INSERT (registrar.php):**
- [ ] Crear `nombre_tabla/registrar.php`
- [ ] Incluir `../config/conexion.php`
- [ ] Agregar validación de campos vacíos
- [ ] Agregar `mysqli_real_escape_string()` para cada campo
- [ ] Ajustar nombres de columnas en INSERT
- [ ] Probar con parámetros en URL o Postman

**2.3. Endpoint UPDATE (editar.php):**
- [ ] Crear `nombre_tabla/editar.php`
- [ ] Incluir `../config/conexion.php`
- [ ] Validar que el ID no esté vacío
- [ ] Sanitizar todos los campos
- [ ] Ajustar nombres de columnas en UPDATE
- [ ] Verificar campo WHERE (generalmente el ID)
- [ ] Probar actualización

**2.4. Endpoint DELETE (eliminar.php):**
- [ ] Crear `nombre_tabla/eliminar.php`
- [ ] Incluir `../config/conexion.php`
- [ ] Validar que el ID no esté vacío
- [ ] Sanitizar el ID
- [ ] Ajustar campo WHERE en DELETE
- [ ] Probar eliminación

**Repetir pasos 2.1 a 2.4 para CADA tabla**

---

#### FASE 3: Configuración del Servidor

**3.1. Ubicación de Archivos:**
- [ ] Copiar `proyecto_backend/` a `htdocs/` o `www/`
- [ ] Verificar que la estructura de carpetas se mantenga

**3.2. Obtener IP:**
- [ ] Ejecutar `ipconfig` (Windows) o `ifconfig` (Linux/Mac)
- [ ] Anotar la dirección IP (ejemplo: `192.168.0.101`)

**3.3. Pruebas desde Navegador:**
- [ ] Probar `http://localhost/proyecto_backend/nombre_tabla/mostrar.php`
- [ ] Verificar que muestre JSON correcto
- [ ] Probar desde dispositivo Android en la misma red

**3.4. Configuración de Firewall:**
- [ ] Si no funciona desde Android, configurar excepción en firewall
- [ ] Permitir puerto 80 (HTTP)

---

#### FASE 4: Configuración de Android

**4.1. Permisos:**
- [ ] Agregar `INTERNET` en AndroidManifest.xml
- [ ] Agregar `ACCESS_NETWORK_STATE` en AndroidManifest.xml
- [ ] Agregar `usesCleartextTraffic="true"` en `<application>`

**4.2. Dependencias:**
- [ ] Agregar Volley en `build.gradle.kts`: `implementation("com.android.volley:volley:1.2.1")`
- [ ] Sincronizar proyecto (Sync Now)

**4.3. Clase de Configuración:**
- [ ] Crear `Config.java`
- [ ] Configurar `SERVER_IP` con tu IP real
- [ ] Agregar todas las URLs para todas las tablas
- [ ] Verificar que las URLs coincidan con la estructura de carpetas

**4.4. Modelos de Datos:**
- [ ] Crear clase modelo para cada tabla
  - Ejemplo: `Alumnos.java`, `Usuarios.java`, `Productos.java`
- [ ] Agregar constructor, getters y setters

---

#### FASE 5: Implementación en Android

**Para CADA tabla, implementar:**

**5.1. Activity de Lista (Mostrar):**
- [ ] Crear Activity para mostrar lista
- [ ] Implementar método `cargarDatos()` usando Volley
- [ ] Parsear JSON respuesta
- [ ] Crear ArrayList con objetos modelo
- [ ] Configurar Adapter para ListView/RecyclerView
- [ ] Probar que se muestren los datos

**5.2. Activity de Registro:**
- [ ] Crear Activity con formulario
- [ ] Implementar método `registrarDatos()` usando Volley
- [ ] Agregar validación de campos
- [ ] Enviar parámetros POST
- [ ] Manejar respuesta del servidor
- [ ] Probar registro desde la app

**5.3. Activity de Edición:**
- [ ] Crear Activity de edición
- [ ] Cargar datos existentes en los campos
- [ ] Implementar método `actualizarDatos()` usando Volley
- [ ] Enviar ID y datos actualizados
- [ ] Verificar que se actualice correctamente

**5.4. Funcionalidad de Eliminación:**
- [ ] Implementar método `eliminarDatos()` usando Volley
- [ ] Agregar confirmación antes de eliminar
- [ ] Recargar lista después de eliminar
- [ ] Probar eliminación

**Repetir pasos 5.1 a 5.4 para CADA tabla**

---

### 📊 Matriz de Verificación por Tabla

**Tabla: _________________**

| Operación | PHP | Probar PHP | Android | Probar Android |
|-----------|-----|------------|---------|----------------|
| SELECT (mostrar) | [ ] | [ ] | [ ] | [ ] |
| INSERT (registrar) | [ ] | [ ] | [ ] | [ ] |
| UPDATE (editar) | [ ] | [ ] | [ ] | [ ] |
| DELETE (eliminar) | [ ] | [ ] | [ ] | [ ] |

*Repetir esta tabla para cada tabla de tu base de datos*

---

### 🎯 Puntos Críticos a Verificar

**Backend:**
- [ ] Todas las rutas usan `../config/conexion.php` correctamente
- [ ] Todos los nombres de columnas coinciden con la BD
- [ ] Todos los campos están sanitizados con `mysqli_real_escape_string()`
- [ ] Todas las respuestas JSON tienen estructura consistente
- [ ] Headers de JSON están configurados: `header('Content-Type: application/json')`

**Android:**
- [ ] Todas las URLs en `Config.java` coinciden con la estructura de carpetas
- [ ] La IP del servidor está correcta
- [ ] Todos los nombres de campos en JSON coinciden con los del modelo
- [ ] Todas las peticiones usan `Config.URL_*` en lugar de URLs hardcodeadas
- [ ] Manejo de errores implementado en todas las peticiones

**Conectividad:**
- [ ] Servidor y dispositivo en la misma red WiFi
- [ ] Firewall configurado correctamente
- [ ] Apache y MySQL están corriendo
- [ ] Se puede acceder desde navegador del dispositivo

---

## Conclusión

Esta arquitectura Cliente-Servidor es la forma estándar y segura de conectar aplicaciones Android con bases de datos. Los puntos clave son:

1. ✅ **Separación de responsabilidades**: Android solo se comunica con PHP, PHP maneja la BD
2. ✅ **Seguridad**: La BD no está expuesta directamente
3. ✅ **Escalabilidad**: Múltiples apps pueden usar el mismo backend
4. ✅ **Mantenibilidad**: Cambios en BD no afectan directamente la app
5. ✅ **Organización**: Cada tabla tiene su propia carpeta con sus endpoints

### ⚠️ Recordatorio Importante

**Cada tabla necesita:**
- 1 carpeta propia
- 4 archivos PHP (mostrar, registrar, editar, eliminar)
- 1 clase modelo en Android
- 1 Activity para mostrar lista (opcional: registrar, editar)
- URLs configuradas en `Config.java`

**Ejemplo:**
- 3 tablas = 3 carpetas PHP + 12 archivos PHP + 3 clases modelo + Activities correspondientes

**Siguiente paso:** Implementar en tu proyecto siguiendo esta guía paso a paso, tabla por tabla.

---

**¿Dudas o problemas?** Revisa la sección de "Resolución de Problemas Comunes" o verifica:
- Logs de Android Studio (Logcat)
- Logs del servidor PHP
- Respuesta del servidor en navegador
- Estructura de carpetas del backend
- URLs en `Config.java` vs estructura real de carpetas

