<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_genero = isset($input['id_genero']) ? trim($input['id_genero']) : '';

if (empty($id_genero)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del género es requerido"));
} else {
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    
    $sql = "SELECT id_genero, descripcion FROM genero WHERE id_genero = '$id_genero'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Género encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el género"));
    }
}

mysqli_close($link);
?>