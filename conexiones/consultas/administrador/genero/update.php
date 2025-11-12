<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
    $descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
    
    if (empty($id_genero)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($descripcion)) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
    } else {
        $id_genero = mysqli_real_escape_string($link, $id_genero);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        
        $sql = "UPDATE genero SET descripcion='$descripcion' WHERE id_genero='$id_genero'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Género actualizado correctamente"));
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

