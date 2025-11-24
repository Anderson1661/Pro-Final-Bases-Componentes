<?php
/**
 * Script de inicio de sesión (Login).
 * 
 * Valida las credenciales del usuario (correo y contraseña) contra la base de datos.
 * Devuelve el ID y tipo de usuario si la autenticación es exitosa.
 */

include('../config/conexion.php');
$link = Conectar();

// Obtener parámetros POST
$correo = $_POST['correo'] ?? '';
$contrasenia = $_POST['contrasenia'] ?? '';

header('Content-Type: application/json; charset=utf-8');

// Validar campos vacíos
if (empty($correo) || empty($contrasenia)) {
    echo json_encode(["success" => false, "message" => "Faltan datos"]);
    exit;
}

// Consulta de validación
// Nota: Se usa comparación directa de contraseña (texto plano)
$sql = "SELECT id_usuario, id_tipo_usuario, correo FROM usuario WHERE correo = '$correo' AND contrasenia = '$contrasenia'";
$res = mysqli_query($link, $sql);

// Verificar si hubo coincidencia
if ($res && mysqli_num_rows($res) > 0) {
    $row = mysqli_fetch_assoc($res);
    // Login exitoso
    echo json_encode([
        "success" => true,
        "id_usuario" => $row['id_usuario'],
        "id_tipo_usuario" => $row['id_tipo_usuario'],
        "correo" => $row['correo']
    ]);
} else {
    // Credenciales inválidas
    echo json_encode(["success" => false, "message" => "Correo o contraseña incorrectos"]);
}

mysqli_close($link);
?>
