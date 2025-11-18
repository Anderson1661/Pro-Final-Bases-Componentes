<?php

try {
    include('../../../config/conexion.php');
    $link = Conectar();
} catch (Exception $e) {
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(["success" => "0", "mensaje" => "Error de conexión a la base de datos."]);
    exit;
}

header('Content-Type: application/json; charset=utf-8');

$correo = $_POST['correo'] ?? '';

if (empty($correo)) {
    echo json_encode(["success" => "0", "mensaje" => "Falta el parámetro 'correo'."]);
    exit;
}

$sql = "SELECT id_conductor FROM conductor WHERE correo = ?";
$stmt = mysqli_prepare($link, $sql);
if ($stmt === false) {
    echo json_encode(["success" => "0", "mensaje" => "Error al preparar la consulta SQL."]);
    exit;
}

mysqli_stmt_bind_param($stmt, "s", $correo);

if (mysqli_stmt_execute($stmt)) {
    $result = mysqli_stmt_get_result($stmt);
    if ($row = mysqli_fetch_assoc($result)) {
        echo json_encode(["success" => "1", "id_conductor" => $row['id_conductor']]);
    } else {
        echo json_encode(["success" => "0", "mensaje" => "Conductor no encontrado."]);
    }
} else {
    echo json_encode(["success" => "0", "mensaje" => "Error al ejecutar la consulta: " . mysqli_stmt_error($stmt)]);
}

mysqli_stmt_close($stmt);
mysqli_close($link);

?>
