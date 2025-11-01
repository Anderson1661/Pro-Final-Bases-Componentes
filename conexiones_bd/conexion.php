<?php
// Encabezados para permitir el acceso desde cualquier origen (CORS)
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header('Content-Type: application/json');

// Si la solicitud es OPTIONS (pre-vuelo de CORS), terminar aquí.
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
  exit;
}

// --- Datos de Conexión a PostgreSQL ---
// Modifica estos valores según tu configuración local
$host = "localhost";
$port = "5432";
$dbname = "transportadora"; // Nombre de la BD que creaste
$user = "postgres";
$password = "tu_password"; // ¡Cambia esto por tu contraseña!


// --- Establecer Conexión ---
$conn_string = "host={$host} port={$port} dbname={$dbname} user={$user} password={$password}";
$conn = pg_connect($conn_string);


// --- Verificación de Conexión ---
if (!$conn) {
    // Si la conexión falla, se notifica con un error 500.
    // Es una buena práctica no exponer detalles del error de la BD en producción.
    http_response_code(500);
    echo json_encode(["error" => "Error: No se pudo conectar a la base de datos."]);
    // Detener la ejecución del script si no hay conexión.
    exit;
}

/**
 * Función para enviar una respuesta JSON estandarizada.
 *
 * @param int $statusCode Código de estado HTTP (e.g., 200, 404, 500).
 * @param array $data El array de datos a codificar en JSON.
 */
function send_response($statusCode, $data) {
    http_response_code($statusCode);
    echo json_encode($data);
}

?>