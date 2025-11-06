<?php
include('../config/conexion.php');
$link = Conectar();

$correo = $_POST['correo'] ?? '';
$contrasenia = $_POST['contrasenia'] ?? '';

header('Content-Type: application/json; charset=utf-8');

if (empty($correo) || empty($contrasenia)) {
    echo json_encode(["success" => false, "message" => "Faltan datos"]);
    exit;
}

$sql = "SELECT id_usuario, id_tipo_usuario, correo FROM usuario WHERE correo = '$correo' AND contrasenia = '$contrasenia'";
$res = mysqli_query($link, $sql);

if ($res && mysqli_num_rows($res) > 0) {
    $row = mysqli_fetch_assoc($res);
    echo json_encode([
        "success" => true,
        "id_usuario" => $row['id_usuario'],
        "id_tipo_usuario" => $row['id_tipo_usuario'],
        "correo" => $row['correo']
    ]);
} else {
    echo json_encode(["success" => false, "message" => "Correo o contraseña incorrectos"]);
}

mysqli_close($link);
?>
