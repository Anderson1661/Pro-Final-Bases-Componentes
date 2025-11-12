<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_ruta = isset($_REQUEST['id_ruta']) ? $_REQUEST['id_ruta'] : '';
    $nombre_pasajero = isset($_REQUEST['nombre_pasajero']) ? $_REQUEST['nombre_pasajero'] : '';
    
    if (empty($id_ruta) || empty($nombre_pasajero)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID ruta y nombre pasajero son requeridos"));
    } else {
        $id_ruta = mysqli_real_escape_string($link, $id_ruta);
        $nombre_pasajero = mysqli_real_escape_string($link, $nombre_pasajero);
        
        $sql = "DELETE FROM pasajero_ruta WHERE id_ruta='$id_ruta' AND nombre_pasajero='$nombre_pasajero'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Pasajero eliminado correctamente"));
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

