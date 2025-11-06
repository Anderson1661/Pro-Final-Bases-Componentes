<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_codigo_postal = isset($_REQUEST['id_codigo_postal']) ? $_REQUEST['id_codigo_postal'] : '';
    $id_pais = isset($_REQUEST['id_pais']) ? $_REQUEST['id_pais'] : '';
    $departamento = isset($_REQUEST['departamento']) ? $_REQUEST['departamento'] : '';
    $ciudad = isset($_REQUEST['ciudad']) ? $_REQUEST['ciudad'] : '';
    
    if (empty($id_codigo_postal)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($id_pais) || empty($departamento) || empty($ciudad)) {
        echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
    } else {
        $id_codigo_postal = mysqli_real_escape_string($link, $id_codigo_postal);
        $id_pais = mysqli_real_escape_string($link, $id_pais);
        $departamento = mysqli_real_escape_string($link, $departamento);
        $ciudad = mysqli_real_escape_string($link, $ciudad);
        
        $sql = "UPDATE codigo_postal SET id_pais='$id_pais', departamento='$departamento', ciudad='$ciudad' WHERE id_codigo_postal='$id_codigo_postal'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Código postal actualizado correctamente"));
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

