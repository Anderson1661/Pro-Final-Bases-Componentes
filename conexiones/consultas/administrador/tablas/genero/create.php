<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $descripcion = mysqli_real_escape_string($link, $descripcion);
    
    // Verificar si ya existe
    $check_sql = "SELECT id_genero FROM genero WHERE descripcion = '$descripcion'";
    $check_res = mysqli_query($link, $check_sql);
    
    if (mysqli_num_rows($check_res) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El género '$descripcion' ya existe"));
    } else {
        $sql = "INSERT INTO genero (descripcion) VALUES ('$descripcion')";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            echo json_encode(array("success" => "1", "mensaje" => "Género registrado correctamente"));
        } else {
            $error = mysqli_error($link);
            echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . $error));
        }
    }
}

mysqli_close($link);
?>