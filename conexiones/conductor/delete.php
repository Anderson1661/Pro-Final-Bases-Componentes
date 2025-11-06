<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_conductor = isset($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : '';
    
    if (empty($id_conductor)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_conductor = mysqli_real_escape_string($link, $id_conductor);
        
        $sql = "DELETE FROM conductor WHERE id_conductor='$id_conductor'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Conductor eliminado correctamente"));
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

