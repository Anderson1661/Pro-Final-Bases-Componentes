<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_conductor = isset($input['id_conductor']) ? trim($input['id_conductor']) : '';

if (empty($id_conductor)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del conductor es requerido"));
} else {
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
    
    $sql = "SELECT id_conductor, id_estado_conductor, placa_vehiculo, identificacion, 
                   id_tipo_identificacion, nombre, direccion, correo, id_genero, 
                   codigo_postal, id_pais_nacionalidad, url_foto 
            FROM conductor 
            WHERE id_conductor = '$id_conductor'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Conductor encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el conductor"));
    }
}

mysqli_close($link);
?>