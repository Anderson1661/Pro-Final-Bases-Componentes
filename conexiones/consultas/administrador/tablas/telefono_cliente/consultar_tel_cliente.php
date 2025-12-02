<?php
// Asegúrate de que el path de tu archivo de conexión sea correcto.
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_cliente = isset($input['id_cliente']) ? trim($input['id_cliente']) : '';
$telefono = isset($input['telefono']) ? trim($input['telefono']) : ''; // Teléfono a consultar

if (empty($id_cliente) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID cliente y teléfono son requeridos"));
} else {
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $telefono = mysqli_real_escape_string($link, $telefono);

    $sql = "SELECT id_cliente, telefono
            FROM telefono_cliente
            WHERE id_cliente = '$id_cliente' AND telefono = '$telefono'";
    $res = mysqli_query($link, $sql);

    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Teléfono de cliente encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el teléfono del cliente"));
    }
}

mysqli_close($link);
?>