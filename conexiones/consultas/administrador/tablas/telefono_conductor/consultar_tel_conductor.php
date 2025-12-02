<?php
// Asegúrate de que el path de tu archivo de conexión sea correcto.
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_conductor = isset($input['id_conductor']) ? trim($input['id_conductor']) : '';
$telefono = isset($input['telefono']) ? trim($input['telefono']) : ''; // Teléfono a consultar

if (empty($id_conductor) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID conductor y teléfono son requeridos"));
} else {
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
    $telefono = mysqli_real_escape_string($link, $telefono);

    $sql = "SELECT id_conductor, telefono
            FROM telefono_conductor
            WHERE id_conductor = '$id_conductor' AND telefono = '$telefono'";
    $res = mysqli_query($link, $sql);

    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Teléfono de conductor encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el teléfono del conductor"));
    }
}

mysqli_close($link);
?>