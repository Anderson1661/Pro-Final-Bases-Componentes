<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_administrador = isset($input['id_administrador']) ? trim($input['id_administrador']) : '';

if (empty($id_administrador)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del administrador es requerido"));
} else {
    $id_administrador = mysqli_real_escape_string($link, $id_administrador);
    
    $sql = "SELECT id_administrador, identificacion, id_tipo_identificacion, nombre, 
                   direccion, correo, id_genero, codigo_postal 
            FROM administrador 
            WHERE id_administrador = '$id_administrador'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Administrador encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el administrador"));
    }
}

mysqli_close($link);
?>