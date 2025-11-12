<?php
include('../config/conexion.php');
$link = Conectar();

$id_codigo_postal = isset($_REQUEST['id_codigo_postal']) ? $_REQUEST['id_codigo_postal'] : '';
$id_pais = isset($_REQUEST['id_pais']) ? $_REQUEST['id_pais'] : '';
$departamento = isset($_REQUEST['departamento']) ? $_REQUEST['departamento'] : '';
$ciudad = isset($_REQUEST['ciudad']) ? $_REQUEST['ciudad'] : '';

if (empty($id_codigo_postal) || empty($id_pais) || empty($departamento) || empty($ciudad)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    $id_codigo_postal = mysqli_real_escape_string($link, $id_codigo_postal);
    $id_pais = mysqli_real_escape_string($link, $id_pais);
    $departamento = mysqli_real_escape_string($link, $departamento);
    $ciudad = mysqli_real_escape_string($link, $ciudad);
    
    $sql = "INSERT INTO codigo_postal (id_codigo_postal, id_pais, departamento, ciudad) VALUES ('$id_codigo_postal', '$id_pais', '$departamento', '$ciudad')";
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        echo json_encode(array("success" => "1", "mensaje" => "Código postal registrado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>

