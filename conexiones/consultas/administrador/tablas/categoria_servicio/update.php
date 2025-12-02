<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_categoria_servicio = isset($input['id_categoria_servicio']) ? trim($input['id_categoria_servicio']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';
$valor_km = isset($input['valor_km']) ? trim($input['valor_km']) : '';

if (empty($id_categoria_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($descripcion) || empty($valor_km)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción y el valor por km son requeridos"));
} else {
    // Validar que valor_km sea numérico
    if (!is_numeric($valor_km)) {
        echo json_encode(array("success" => "0", "mensaje" => "El valor por km debe ser un número"));
        exit;
    }
    
    $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    $valor_km = mysqli_real_escape_string($link, $valor_km);
    
    // Verificar si ya existe otra categoría con la misma descripción
    $check_sql = "SELECT id_categoria_servicio FROM categoria_servicio 
                  WHERE descripcion = '$descripcion' AND id_categoria_servicio != '$id_categoria_servicio'";
    $check_res = mysqli_query($link, $check_sql);
    
    if (mysqli_num_rows($check_res) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otra categoría con esa descripción"));
    } else {
        $sql = "UPDATE categoria_servicio SET descripcion='$descripcion', valor_km='$valor_km' 
                WHERE id_categoria_servicio='$id_categoria_servicio'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Categoría de servicio actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
}
mysqli_close($link);
?>