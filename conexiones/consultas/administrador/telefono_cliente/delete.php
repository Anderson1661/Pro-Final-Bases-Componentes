<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_cliente = isset($_REQUEST['id_cliente']) ? $_REQUEST['id_cliente'] : '';
    $telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';
    
    if (empty($id_cliente) || empty($telefono)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID cliente y teléfono son requeridos"));
    } else {
        $id_cliente = mysqli_real_escape_string($link, $id_cliente);
        $telefono = mysqli_real_escape_string($link, $telefono);
        
        $sql = "DELETE FROM telefono_cliente WHERE id_cliente='$id_cliente' AND telefono='$telefono'";
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

