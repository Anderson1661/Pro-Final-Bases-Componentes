<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_categoria_servicio = isset($_REQUEST['id_categoria_servicio']) ? $_REQUEST['id_categoria_servicio'] : '';
    $descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
    $valor_km = isset($_REQUEST['valor_km']) ? $_REQUEST['valor_km'] : '';
    
    if (empty($id_categoria_servicio)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($descripcion) || empty($valor_km)) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción y el valor por km son requeridos"));
    } else {
        $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        $valor_km = mysqli_real_escape_string($link, $valor_km);
        
        $sql = "UPDATE categoria_servicio SET descripcion='$descripcion', valor_km='$valor_km' WHERE id_categoria_servicio='$id_categoria_servicio'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Categoría de servicio actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

