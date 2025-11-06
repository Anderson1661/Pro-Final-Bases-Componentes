<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_pais = isset($_REQUEST['id_pais']) ? $_REQUEST['id_pais'] : '';
    $nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';
    
    if (empty($id_pais)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($nombre)) {
        echo json_encode(array("success" => "0", "mensaje" => "El nombre es requerido"));
    } else {
        $id_pais = mysqli_real_escape_string($link, $id_pais);
        $nombre = mysqli_real_escape_string($link, $nombre);
        
        $sql = "UPDATE pais SET nombre='$nombre' WHERE id_pais='$id_pais'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "País actualizado correctamente"));
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

