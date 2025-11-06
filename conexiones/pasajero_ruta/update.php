<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_ruta = isset($_REQUEST['id_ruta']) ? $_REQUEST['id_ruta'] : '';
    $nombre_pasajero = isset($_REQUEST['nombre_pasajero']) ? $_REQUEST['nombre_pasajero'] : '';
    $nombre_pasajero_nuevo = isset($_REQUEST['nombre_pasajero_nuevo']) ? $_REQUEST['nombre_pasajero_nuevo'] : '';
    
    if (empty($id_ruta) || empty($nombre_pasajero)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID ruta y nombre pasajero son requeridos"));
    } else if (empty($nombre_pasajero_nuevo)) {
        echo json_encode(array("success" => "0", "mensaje" => "El nuevo nombre es requerido"));
    } else {
        $id_ruta = mysqli_real_escape_string($link, $id_ruta);
        $nombre_pasajero = mysqli_real_escape_string($link, $nombre_pasajero);
        $nombre_pasajero_nuevo = mysqli_real_escape_string($link, $nombre_pasajero_nuevo);
        
        $sql = "DELETE FROM pasajero_ruta WHERE id_ruta='$id_ruta' AND nombre_pasajero='$nombre_pasajero'";
        $res1 = mysqli_query($link, $sql);
        
        if ($res1) {
            $sql2 = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ('$id_ruta', '$nombre_pasajero_nuevo')";
            $res2 = mysqli_query($link, $sql2);
            
            if ($res2) {
                echo json_encode(array("success" => "1", "mensaje" => "Pasajero actualizado correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
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

