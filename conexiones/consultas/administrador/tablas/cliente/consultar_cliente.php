<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_cliente = isset($input['id_cliente']) ? trim($input['id_cliente']) : '';

if (empty($id_cliente)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del cliente es requerido"));
} else {
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    
    $sql = "SELECT id_cliente, identificacion, id_tipo_identificacion, nombre, 
                   direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal 
            FROM cliente 
            WHERE id_cliente = '$id_cliente'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Cliente encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el cliente"));
    }
}

mysqli_close($link);
?>