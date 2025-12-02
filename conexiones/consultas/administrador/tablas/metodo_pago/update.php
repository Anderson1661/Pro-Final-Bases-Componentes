<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_metodo_pago = isset($input['id_metodo_pago']) ? trim($input['id_metodo_pago']) : '';
$descripcion = isset($input['descripcion']) ? trim($input['descripcion']) : '';

if (empty($id_metodo_pago)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de método de pago requerido"));
} else if (empty($descripcion)) {
    echo json_encode(array("success" => "0", "mensaje" => "La descripción es requerida"));
} else {
    $id_metodo_pago = mysqli_real_escape_string($link, $id_metodo_pago);
    $descripcion = mysqli_real_escape_string($link, $descripcion);

    // Verificar si ya existe otra descripción con el mismo nombre (excluyendo el actual)
    $check_descripcion = "SELECT id_metodo_pago FROM metodo_pago
                     WHERE descripcion = '$descripcion' AND id_metodo_pago != '$id_metodo_pago'";
    $check_res_descripcion = mysqli_query($link, $check_descripcion);

    if (mysqli_num_rows($check_res_descripcion) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro método de pago con esa descripción"));
        exit;
    }

    $sql = "UPDATE metodo_pago SET
            descripcion='$descripcion'
            WHERE id_metodo_pago='$id_metodo_pago'";

    $res = mysqli_query($link, $sql);

    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Método de pago actualizado correctamente"));
        } else {
            // Si la descripción es la misma, no hay filas afectadas.
            echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
}

mysqli_close($link);
?>