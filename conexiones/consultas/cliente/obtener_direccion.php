<?php
/**
 * Script para obtener la dirección de un cliente.
 * 
 * Recibe el ID del cliente y devuelve su dirección y ciudad registradas.
 * Útil para autocompletar formularios o mostrar información en el perfil.
 */

include('../../config/conexion.php');
$link = Conectar();

// Obtener ID del cliente
$id_cliente = $_POST['id_cliente'] ?? '';

header('Content-Type: application/json; charset=utf-8');

// Validar parámetro
if (empty($id_cliente)) {
    echo json_encode(["success" => false, "message" => "Falta id_cliente"]);
    exit;
}

// Consultar dirección y ciudad
$sql = "SELECT direccion, ciudad FROM cliente WHERE id_cliente = '$id_cliente'";
$res = mysqli_query($link, $sql);

// Verificar si existe el cliente
if ($res && mysqli_num_rows($res) > 0) {
    $row = mysqli_fetch_assoc($res);
    // Devolver datos
    echo json_encode([
        "success" => true,
        "direccion" => $row['direccion'],
        "ciudad" => $row['ciudad']
    ]);
} else {
    // Cliente no encontrado
    echo json_encode(["success" => false, "message" => "Cliente no encontrado"]);
}

mysqli_close($link);
?>
