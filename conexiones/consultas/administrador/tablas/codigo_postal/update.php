<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_codigo_postal = isset($input['id_codigo_postal']) ? trim($input['id_codigo_postal']) : '';
$id_pais = isset($input['id_pais']) ? trim($input['id_pais']) : '';
$departamento = isset($input['departamento']) ? trim($input['departamento']) : '';
$ciudad = isset($input['ciudad']) ? trim($input['ciudad']) : '';

if (empty($id_codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
} else if (empty($id_pais) || empty($departamento) || empty($ciudad)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_codigo_postal = mysqli_real_escape_string($link, $id_codigo_postal);
    $id_pais = mysqli_real_escape_string($link, $id_pais);
    $departamento = mysqli_real_escape_string($link, $departamento);
    $ciudad = mysqli_real_escape_string($link, $ciudad);
    
    // Verificar si ya existe otro código postal con la misma ciudad y departamento en el mismo país
    $check_existente = "SELECT id_codigo_postal FROM codigo_postal 
                       WHERE id_pais = '$id_pais' 
                       AND departamento = '$departamento' 
                       AND ciudad = '$ciudad' 
                       AND id_codigo_postal != '$id_codigo_postal'";
    $check_res = mysqli_query($link, $check_existente);
    
    if (mysqli_num_rows($check_res) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe un código postal para esta ciudad y departamento en este país"));
        exit;
    }
    
    // Verificar que el país exista
    $check_pais = "SELECT id_pais FROM pais WHERE id_pais = '$id_pais'";
    
    if (mysqli_num_rows(mysqli_query($link, $check_pais)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El país no existe"));
        exit;
    }
    
    $sql = "UPDATE codigo_postal SET 
            id_pais='$id_pais', 
            departamento='$departamento', 
            ciudad='$ciudad' 
            WHERE id_codigo_postal='$id_codigo_postal'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Código postal actualizado correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}
mysqli_close($link);
?>