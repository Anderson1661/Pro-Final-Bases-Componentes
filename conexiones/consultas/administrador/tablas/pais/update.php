<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_pais = isset($input['id_pais']) ? trim($input['id_pais']) : '';
$nombre = isset($input['nombre']) ? trim($input['nombre']) : '';

if (empty($id_pais)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de país requerido"));
} else if (empty($nombre)) {
    echo json_encode(array("success" => "0", "mensaje" => "El nombre es requerido"));
} else {
    $id_pais = mysqli_real_escape_string($link, $id_pais);
    $nombre = mysqli_real_escape_string($link, $nombre);

    // Verificar si ya existe otro país con el mismo nombre (excluyendo el actual)
    $check_nombre = "SELECT id_pais FROM pais
                     WHERE nombre = '$nombre' AND id_pais != '$id_pais'";
    $check_res_nombre = mysqli_query($link, $check_nombre);

    if (mysqli_num_rows($check_res_nombre) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro país con ese nombre"));
        exit;
    }

    $sql = "UPDATE pais SET
            nombre='$nombre'
            WHERE id_pais='$id_pais'";

    $res = mysqli_query($link, $sql);

    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "País actualizado correctamente"));
        } else {
            // Si el nombre es el mismo, no hay filas afectadas.
            echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>