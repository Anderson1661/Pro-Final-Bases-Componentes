<?php
include('../../config/conexion.php');
$link = Conectar();

$id_cliente = $_POST['id_cliente'] ?? '';

header('Content-Type: application/json; charset=utf-8');

if (empty($id_cliente)) {
    echo json_encode(["success" => false, "message" => "Falta id_cliente"]);
    exit;
}

$sql = "SELECT direccion, ciudad FROM cliente WHERE id_cliente = '$id_cliente'";
$res = mysqli_query($link, $sql);

if ($res && mysqli_num_rows($res) > 0) {
    $row = mysqli_fetch_assoc($res);
    echo json_encode([
        "success" => true,
        "direccion" => $row['direccion'],
        "ciudad" => $row['ciudad']
    ]);
} else {
    echo json_encode(["success" => false, "message" => "Cliente no encontrado"]);
}

mysqli_close($link);
?>
