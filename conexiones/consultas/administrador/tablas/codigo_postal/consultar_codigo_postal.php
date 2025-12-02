<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_codigo_postal = isset($input['id_codigo_postal']) ? trim($input['id_codigo_postal']) : '';

if (empty($id_codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del código postal es requerido"));
} else {
    $id_codigo_postal = mysqli_real_escape_string($link, $id_codigo_postal);
    
    $sql = "SELECT id_codigo_postal, id_pais, departamento, ciudad 
            FROM codigo_postal 
            WHERE id_codigo_postal = '$id_codigo_postal'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Código postal encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el código postal"));
    }
}

mysqli_close($link);
?>