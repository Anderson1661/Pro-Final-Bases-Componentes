<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../../../../config/conexion.php'); // Asegúrate de ajustar la ruta de 'conexion.php' si es necesario
    $link = Conectar();
    
    // Obtener datos del cuerpo JSON
    $input = json_decode(file_get_contents('php://input'), true);
    
    $id_ruta_original = isset($input['id_ruta_original']) ? $input['id_ruta_original'] : '';
    $nombre_pasajero_original = isset($input['nombre_pasajero_original']) ? $input['nombre_pasajero_original'] : '';
    
    $id_ruta_nuevo = isset($input['id_ruta_nuevo']) ? $input['id_ruta_nuevo'] : '';
    $nombre_pasajero_nuevo = isset($input['nombre_pasajero_nuevo']) ? $input['nombre_pasajero_nuevo'] : '';
    
    // Validación de campos requeridos
    if (empty($id_ruta_original) || empty($nombre_pasajero_original)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID de ruta y nombre de pasajero originales son requeridos"));
    } else if (empty($id_ruta_nuevo) || empty($nombre_pasajero_nuevo)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID de ruta y nombre de pasajero nuevos son requeridos"));
    } else {
        $id_ruta_original = mysqli_real_escape_string($link, $id_ruta_original);
        $nombre_pasajero_original = mysqli_real_escape_string($link, $nombre_pasajero_original);
        $id_ruta_nuevo = mysqli_real_escape_string($link, $id_ruta_nuevo);
        $nombre_pasajero_nuevo = mysqli_real_escape_string($link, $nombre_pasajero_nuevo);
        
        // 1. Eliminar el registro original
        $sql_delete = "DELETE FROM pasajero_ruta WHERE id_ruta='$id_ruta_original' AND nombre_pasajero='$nombre_pasajero_original'";
        $res1 = mysqli_query($link, $sql_delete);
        
        if ($res1) {
            // 2. Insertar el nuevo registro
            $sql_insert = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ('$id_ruta_nuevo', '$nombre_pasajero_nuevo')";
            $res2 = mysqli_query($link, $sql_insert);
            
            if ($res2) {
                echo json_encode(array("success" => "1", "mensaje" => "Pasajero en ruta actualizado correctamente"));
            } else {
                // Si falla la inserción, intentar restaurar el original para evitar pérdida de datos (opcional, pero buena práctica)
                // $sql_restore = "INSERT INTO pasajero_ruta (id_ruta, nombre_pasajero) VALUES ('$id_ruta_original', '$nombre_pasajero_original')";
                // mysqli_query($link, $sql_restore);

                echo json_encode(array("success" => "0", "mensaje" => "Error al insertar el nuevo registro: " . mysqli_error($link)));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al eliminar el registro original: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido. Use POST"));
}
?>