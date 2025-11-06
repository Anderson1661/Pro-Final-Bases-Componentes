<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_categoria_servicio = isset($_REQUEST['id_categoria_servicio']) ? $_REQUEST['id_categoria_servicio'] : '';
    
    if (empty($id_categoria_servicio)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
        
        $sql = "DELETE FROM categoria_servicio WHERE id_categoria_servicio='$id_categoria_servicio'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Categoría de servicio eliminada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al eliminar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

