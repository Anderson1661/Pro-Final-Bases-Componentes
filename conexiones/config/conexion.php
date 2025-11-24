<?php
/**
 * Función para establecer la conexión a la base de datos MySQL.
 * 
 * Esta función se encarga de:
 * 1. Buscar y cargar las variables de entorno desde un archivo .env.
 * 2. Configurar los parámetros de conexión (host, usuario, contraseña, base de datos).
 * 3. Inicializar la conexión utilizando la extensión MySQLi.
 * 4. Configurar el juego de caracteres a UTF-8.
 * 
 * @return mysqli Objeto de conexión a la base de datos.
 * @throws Exception Si no se puede cargar la configuración o fallar la conexión.
 */
function Conectar()
{
    // --- 1. Carga de configuración ---
    
    // Lista de posibles ubicaciones del archivo .env para flexibilidad en el despliegue
    $candidates = [
        __DIR__ . '/.env',        // Opción 1: Mismo directorio que este archivo (config/.env)
        __DIR__ . '/../.env',     // Opción 2: Directorio padre (conexiones/.env)
    ];

    $envFile = null;
    // Buscar el primer archivo .env que exista
    foreach ($candidates as $cand) {
        if (file_exists($cand)) {
            $envFile = $cand;
            break;
        }
    }

    // Si se encontró un archivo .env, parsearlo manualmente
    if ($envFile !== null) {
        $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
        foreach ($lines as $line) {
            $line = trim($line);
            // Ignorar líneas vacías o comentarios (#)
            if ($line === '' || strpos($line, '#') === 0) {
                continue;
            }
            // Ignorar líneas que no tengan formato CLAVE=VALOR
            if (strpos($line, '=') === false) {
                continue;
            }
            
            // Separar clave y valor
            list($name, $value) = explode('=', $line, 2);
            $name = trim($name);
            $value = trim($value);
            
            // Limpiar comillas alrededor del valor si existen
            if ((substr($value, 0, 1) === '"' && substr($value, -1) === '"') ||
                (substr($value, 0, 1) === "'" && substr($value, -1) === "'")) {
                $value = substr($value, 1, -1);
            }
            
            // Cargar en $_ENV y putenv si no existe ya
            if ($name !== '') {
                if (!array_key_exists($name, $_ENV)) {
                    $_ENV[$name] = $value;
                    putenv("$name=$value");
                }
            }
        }
    }

    // --- 2. Obtención de credenciales ---
    
    // Se intenta obtener de $_ENV, luego getenv(), y finalmente un valor por defecto
    $host = $_ENV['DB_HOST'] ?? getenv('DB_HOST') ?? '127.0.0.1';
    $port = (int) ($_ENV['DB_PORT'] ?? getenv('DB_PORT') ?? 3306);
    $user = $_ENV['DB_USER'] ?? getenv('DB_USER') ?? 'root';
    $pass = $_ENV['DB_PASS'] ?? getenv('DB_PASS') ?? '';
    $dbname = $_ENV['DB_NAME'] ?? getenv('DB_NAME') ?? '';

    // Validación crítica: El nombre de la BD es obligatorio
    if ($dbname === '') {
        throw new Exception('DB_NAME no está definido en .env');
    }

    // --- 3. Inicialización de conexión ---
    
    // Inicializar objeto mysqli
    $link = mysqli_init();
    if ($link === false) {
        throw new Exception('No se pudo inicializar mysqli');
    }

    // Configurar timeout corto (5s) para no bloquear el script si la BD está caída
    mysqli_options($link, MYSQLI_OPT_CONNECT_TIMEOUT, 5);

    // Intentar conectar (el @ suprime warnings de PHP, manejamos el error manualmente)
    if (!@mysqli_real_connect($link, $host, $user, $pass, $dbname, $port)) {
        $err = mysqli_connect_error();
        throw new Exception("Error de conexión a MySQL: $err");
    }

    // --- 4. Configuración final ---
    
    // Establecer charset a utf8mb4 para soporte completo de Unicode (emojis, acentos, etc.)
    mysqli_set_charset($link, 'utf8mb4');

    return $link;
}

