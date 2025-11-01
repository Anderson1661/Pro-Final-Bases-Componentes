# Proyecto de Gestión de Transportes "Aventureros S.A."

Este repositorio contiene el código fuente y la documentación para la aplicación de gestión de la empresa de transportes "Aventureros S.A.". El sistema se compone de una aplicación móvil Android para la interacción de usuarios y un backend en PHP que se comunica con una base de datos PostgreSQL.

---

## 1. Descripción General

La aplicación permite a los clientes solicitar servicios de transporte de pasajeros y/o alimentos. Los conductores pueden gestionar los servicios que les son asignados, y los administradores tienen una visión completa de la operación, pudiendo gestionar todos los aspectos del sistema, desde usuarios y vehículos hasta la generación de reportes financieros y operativos.

---

## 2. Estructura de Directorios

El proyecto está organizado de la siguiente manera:

```
/
├── Base/                   # Contiene el script SQL para la creación de la BD.
│   ├── base.sql            # Script de estructura, tablas, vistas y triggers.
│   └── Reglas.txt          # Reglas de negocio (borrador).
│
├── conexiones_bd/          # Backend de la aplicación (API en PHP).
│   ├── conexion.php        # Archivo principal de conexión a la BD.
│   ├── tabla_x/            # Una carpeta por cada tabla de la BD.
│   │   ├── create.php      # Endpoint para crear registros (INSERT).
│   │   ├── read.php        # Endpoint para leer registros (SELECT).
│   │   ├── update.php      # Endpoint para actualizar registros (UPDATE).
│   │   └── delete.php      # Endpoint para eliminar registros (DELETE).
│   └── vista_y/            # Una carpeta por cada vista de la BD.
│       └── read.php        # Endpoint para consultar los datos de la vista.
│
├── Transportadora/         # Proyecto de la aplicación Android en Kotlin.
│   ├── app/                # Módulo principal de la app.
│   └── ...                 # Otros archivos de configuración de Gradle.
│
├── .gitattributes
├── Conexiones.txt          # Borrador inicial de la arquitectura de conexión.
├── Enunciados.txt          # Requisitos y enunciados del proyecto.
├── Funcionalidades.txt     # Descripción detallada de las funcionalidades.
└── README.md               # Este archivo.
```

---

## 3. Backend y API (PHP)

El backend actúa como una API RESTful que responde a las peticiones HTTP desde la aplicación Android. La comunicación se realiza exclusivamente a través de JSON.

### 3.1. Configuración

Antes de usar la API, es crucial configurar la conexión a la base de datos en el archivo `conexiones_bd/conexion.php`:

```php
// ...
$host = "localhost";
$port = "5432";
$dbname = "transportadora";
$user = "postgres";
$password = "tu_password"; // <-- ¡IMPORTANTE: Cambia esto por tu contraseña!
// ...
```

### 3.2. Estructura de Endpoints y Uso

Los endpoints siguen un patrón predecible basado en la estructura de carpetas. El formato general es:

`https://<tu-servidor>/conexiones_bd/<nombre_tabla_o_vista>/<accion>.php`

**Ejemplos de Operaciones:**

- **Leer (GET):**
  - Para leer todos los clientes: `GET /conexiones_bd/cliente/read.php`
  - Para leer un cliente específico: `GET /conexiones_bd/cliente/read.php?id_cliente=123`

- **Crear (POST):**
  - URL: `POST /conexiones_bd/cliente/create.php`
  - Body (JSON): `{"nombre": "Juan", "correo": "juan@example.com", ...}`

- **Actualizar (PUT/POST):**
  - URL: `POST /conexiones_bd/cliente/update.php` (aunque se usa POST, la acción es de actualización).
  - Body (JSON): `{"id_cliente": 123, "direccion": "Nueva Calle 456", ...}`

- **Eliminar (POST/DELETE):**
  - URL: `POST /conexiones_bd/cliente/delete.php`
  - Body (JSON): `{"id_cliente": 123}`

> **Nota:** Por simplicidad, los métodos `PUT` y `DELETE` se manejan a través de `POST` en los scripts PHP, leyendo un cuerpo JSON para identificar el recurso a modificar o eliminar.

---

## 4. Funcionalidades de la Aplicación

La lista completa y detallada de funcionalidades se encuentra en el archivo `Funcionalidades.txt`. A continuación, un resumen de los módulos principales:

- **Módulo de Autenticación:** Login, registro, y recuperación de contraseña por preguntas de seguridad.
- **Módulo de Cliente:** Solicitar servicios, ver historial y seguimiento, y gestionar su perfil.
- **Módulo de Conductor:** Gestionar servicios asignados, consultar historial y ver resumen de pagos.
- **Módulo de Administrador:** Control total sobre usuarios, vehículos, sucursales y servicios. Acceso a un dashboard con reportes y estadísticas clave de la operación.

---

## 5. Base de Datos

Se utiliza **PostgreSQL** como motor de base de datos.

- **Esquema:** La estructura completa, incluyendo tablas, tipos de datos y relaciones, está definida en `Base/base.sql`.
- **Triggers:** La base de datos incluye lógica automatizada:
  - `insertar_usuario()`: Crea una entrada en la tabla `usuario` automáticamente cuando se inserta un nuevo `cliente`, `conductor` o `administrador`.
  - `calcular_total_ruta()`: Calcula el costo total de un servicio (`ruta.total`) automáticamente antes de insertar o actualizar una ruta, basándose en la distancia y la categoría del servicio.
- **Vistas:** Se han creado vistas para simplificar la generación de reportes complejos:
  - `vw_total_cantidad_por_tipo_servicio`: Agrupa el total de servicios y su valor por tipo.
  - `vw_cantidad_servicios_por_mes`: Cuenta los servicios y clientes únicos por mes.
  - `vw_clientes_con_servicios`: Ranking de clientes por cantidad de servicios y valor total.
  - `vw_total_por_metodo_pago`: Agrupa los ingresos por método de pago.
