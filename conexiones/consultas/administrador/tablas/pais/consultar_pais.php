<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_pais = isset($input['id_pais']) ? trim($input['id_pais']) : '';

if (empty($id_pais)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del país es requerido"));
} else {
    $id_pais = mysqli_real_escape_string($link, $id_pais);

    // Consulta para obtener el país
    $sql = "SELECT id_pais, nombre
            FROM pais
            WHERE id_pais = '$id_pais'";
    $res = mysqli_query($link, $sql);

    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1",
            "datos" => $row,
            "mensaje" => "País encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el país"));
    }
}

mysqli_close($link);
?>