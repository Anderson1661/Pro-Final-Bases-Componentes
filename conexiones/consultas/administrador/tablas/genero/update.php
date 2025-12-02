<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_genero = isset($input['id_genero']) ? trim($input['id_genero']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_genero)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe otro género con la misma descripción (excepto este)
    $check_sql = "SELECT id_genero FROM genero WHERE descripcion = '$descripcion' AND id_genero != '$id_genero'";
    $check_res = mysqli_query($link, $check_sql);
    
    if (mysqli_num_rows($check_res) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro género con esa descripción"));
    } else {
        $sql = "UPDATE genero SET descripcion='$descripcion' WHERE id_genero='$id_genero'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Género actualizado correctamente"));
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