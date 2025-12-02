<?php
include('../../../../config/conexion.php'); // Asegúrate de ajustar la ruta de 'conexion.php' si es necesario
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_ruta = isset($input['id_ruta']) ? trim($input['id_ruta']) : '';
$nombre_pasajero = isset($input['nombre_pasajero']) ? trim($input['nombre_pasajero']) : '';

if (empty($id_ruta) || empty($nombre_pasajero)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de ruta y el nombre del pasajero son requeridos"));
} else {
    $id_ruta = mysqli_real_escape_string($link, $id_ruta);
    $nombre_pasajero = mysqli_real_escape_string($link, $nombre_pasajero);
    
    // Consulta por clave primaria compuesta
    $sql = "SELECT id_ruta, nombre_pasajero
            FROM pasajero_ruta
            WHERE id_ruta = '$id_ruta' AND nombre_pasajero = '$nombre_pasajero'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Pasajero en ruta encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el pasajero en esa ruta"));
    }
}

mysqli_close($link);
?>