<?php
header('Content-Type: application/json; charset=utf-8');
include('../config/conexion.php');

$link = Conectar();

$id_conductor = $_POST['id_conductor'] ?? '';

if (empty($id_conductor)) {
    echo json_encode(["success" => false, "message" => "Falta el ID del conductor."]);
    exit;
}

// Using prepared statements to prevent SQL injection
$sql = "SELECT id_servicio, origen, destino, fecha_solicitud, estado_servicio FROM servicio WHERE id_conductor_asignado = ?";
$stmt = mysqli_prepare($link, $sql);

if ($stmt) {
    mysqli_stmt_bind_param($stmt, "i", $id_conductor);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    $servicios = [];
    while ($row = mysqli_fetch_assoc($result)) {
        // Remap field names for the app
        $servicios[] = [
            "id_servicio" => $row['id_servicio'],
            "origen" => $row['origen'],
            "destino" => $row['destino'],
            "fecha" => $row['fecha_solicitud'],
            "estado" => $row['estado_servicio']
        ];
    }

    echo json_encode(["success" => true, "servicios" => $servicios]);
    mysqli_stmt_close($stmt);
} else {
    echo json_encode(["success" => false, "message" => "Error en la preparación de la consulta: " . mysqli_error($link)]);
}

mysqli_close($link);
?>
