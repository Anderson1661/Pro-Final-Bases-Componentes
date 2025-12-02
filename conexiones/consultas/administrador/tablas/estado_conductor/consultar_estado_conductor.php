<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_estado_conductor = isset($input['id_estado_conductor']) ? trim($input['id_estado_conductor']) : '';

if (empty($id_estado_conductor)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del estado de conductor es requerido"));
} else {
    $id_estado_conductor = mysqli_real_escape_string($link, $id_estado_conductor);
    
    // Consulta para obtener el estado de conductor
    $sql = "SELECT id_estado_conductor, descripcion 
            FROM estado_conductor 
            WHERE id_estado_conductor = '$id_estado_conductor'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Estado de conductor encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el estado de conductor"));
    }
}

mysqli_close($link);
?>