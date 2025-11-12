<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_metodo_pago = isset($_REQUEST['id_metodo_pago']) ? $_REQUEST['id_metodo_pago'] : '';
    $descripcion = isset($_REQUEST['descripcion']) ? $_REQUEST['descripcion'] : '';
    
    if (empty($id_metodo_pago)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($descripcion)) {
        echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
    } else {
        $id_metodo_pago = mysqli_real_escape_string($link, $id_metodo_pago);
        $descripcion = mysqli_real_escape_string($link, $descripcion);
        
        $sql = "UPDATE metodo_pago SET descripcion='$descripcion' WHERE id_metodo_pago='$id_metodo_pago'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Método de pago actualizado correctamente"));
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

