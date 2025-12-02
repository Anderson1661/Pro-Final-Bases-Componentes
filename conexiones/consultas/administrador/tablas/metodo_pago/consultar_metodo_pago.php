<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_metodo_pago = isset($input['id_metodo_pago']) ? trim($input['id_metodo_pago']) : '';

if (empty($id_metodo_pago)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID del método de pago es requerido"));
} else {
    $id_metodo_pago = mysqli_real_escape_string($link, $id_metodo_pago);

    // Consulta para obtener el método de pago
    $sql = "SELECT id_metodo_pago, descripcion
            FROM metodo_pago
            WHERE id_metodo_pago = '$id_metodo_pago'";
    $res = mysqli_query($link, $sql);

    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        echo json_encode(array(
            "success" => "1",
            "datos" => $row,
            "mensaje" => "Método de pago encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el método de pago"));
    }
}

mysqli_close($link);
?>