<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_marca = isset($input['id_marca']) ? trim($input['id_marca']) : '';
$nombre_marca = isset($input['nombre_marca']) ? trim($input['nombre_marca']) : '';

if (empty($id_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de marca de vehículo requerido"));
} else if (empty($nombre_marca)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre de la marca es requerido"));
} else {
    $id_marca = mysqli_real_escape_string($link, $id_marca);
    $nombre_marca = mysqli_real_escape_string($link, $nombre_marca);
    
    // Verificar si ya existe otra marca con el mismo nombre (excluyendo la actual)
    $check_nombre = "SELECT id_marca FROM marca_vehiculo 
                     WHERE nombre_marca = '$nombre_marca' AND id_marca != '$id_marca'";
    $check_res_nombre = mysqli_query($link, $check_nombre);
    
    if (mysqli_num_rows($check_res_nombre) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otra marca con ese nombre"));
        exit;
    }
    
    $sql = "UPDATE marca_vehiculo SET 
            nombre_marca='$nombre_marca'
            WHERE id_marca='$id_marca'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Marca de vehículo actualizada correctamente"));
        } else {
            // Si el nombre es el mismo, no hay filas afectadas.
            echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>