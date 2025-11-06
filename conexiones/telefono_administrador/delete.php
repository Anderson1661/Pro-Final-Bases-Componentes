<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_administrador = isset($_REQUEST['id_administrador']) ? $_REQUEST['id_administrador'] : '';
    $telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';
    
    if (empty($id_administrador) || empty($telefono)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID administrador y teléfono son requeridos"));
    } else {
        $id_administrador = mysqli_real_escape_string($link, $id_administrador);
        $telefono = mysqli_real_escape_string($link, $telefono);
        
        $sql = "DELETE FROM telefono_administrador WHERE id_administrador='$id_administrador' AND telefono='$telefono'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Teléfono eliminado correctamente"));
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

