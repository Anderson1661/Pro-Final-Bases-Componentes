<?php
function Conectar()
{
    // Cargar variables de entorno desde alguno de los posibles .env
    $candidates = [
        __DIR__ . '/.env',        // en config/.env
        __DIR__ . '/../.env',     // en conexiones/.env
        __DIR__ . '/config/.env', // por si acaso
    ];

    $envFile = null;
    foreach ($candidates as $cand) {
        if (file_exists($cand)) {
            $envFile = $cand;
            break;
        }
    }

    if ($envFile !== null) {
        $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
        foreach ($lines as $line) {
            $line = trim($line);
            if ($line === '' || strpos($line, '#') === 0) {
                continue;
            }
            if (strpos($line, '=') === false) {
                // Línea inválida, ignorar
                continue;
            }
            list($name, $value) = explode('=', $line, 2);
            $name = trim($name);
            $value = trim($value);
            // Quitar comillas simples/dobles alrededor del valor
            if ((substr($value, 0, 1) === '"' && substr($value, -1) === '"') ||
                (substr($value, 0, 1) === "'" && substr($value, -1) === "'")) {
                $value = substr($value, 1, -1);
            }
            if ($name !== '') {
                if (!array_key_exists($name, $_ENV)) {
                    $_ENV[$name] = $value;
                    putenv("$name=$value");
                }
            }
        }
    }

    // Obtener configuración de variables de entorno con fallback seguro
    $host = $_ENV['DB_HOST'] ?? getenv('DB_HOST') ?? '127.0.0.1';
    $port = (int) ($_ENV['DB_PORT'] ?? getenv('DB_PORT') ?? 3306);
    $user = $_ENV['DB_USER'] ?? getenv('DB_USER') ?? 'root';
    $pass = $_ENV['DB_PASS'] ?? getenv('DB_PASS') ?? '';
    $dbname = $_ENV['DB_NAME'] ?? getenv('DB_NAME') ?? '';

    if ($dbname === '') {
        throw new Exception('DB_NAME no está definido en .env');
    }

    // Inicializar mysqli y conectar (lanza excepción en caso de fallo)
    $link = mysqli_init();
    if ($link === false) {
        throw new Exception('No se pudo inicializar mysqli');
    }

    // Opciones (tiempo de conexión corto para fallos rápidos)
    mysqli_options($link, MYSQLI_OPT_CONNECT_TIMEOUT, 5);

    if (!@mysqli_real_connect($link, $host, $user, $pass, $dbname, $port)) {
        $err = mysqli_connect_error();
        throw new Exception("Error de conexión a MySQL: $err");
    }

    // Usar utf8mb4 para mejor soporte unicode
    mysqli_set_charset($link, 'utf8mb4');

    return $link;
}

