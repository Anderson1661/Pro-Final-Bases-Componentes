<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_categoria_servicio = isset($input['id_categoria_servicio']) ? trim($input['id_categoria_servicio']) : '';

if (empty($id_categoria_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de la categoría de servicio es requerido"));
} else {
    $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
    
    $sql = "SELECT id_categoria_servicio, descripcion, valor_km FROM categoria_servicio WHERE id_categoria_servicio = '$id_categoria_servicio'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Categoría de servicio encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la categoría de servicio"));
    }
}

mysqli_close($link);
?>