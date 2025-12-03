<?php
/**
 * Script para obtener el ID de un conductor por su correo.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    $sql = "SELECT id_conductor FROM conductor WHERE correo = ?";
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $res["success"] = "1";
        $res["id_conductor"] = $row['id_conductor'];
        $res["mensaje"] = "ID encontrado";
    } else {
        $res["mensaje"] = "No se encontró el conductor con ese correo.";
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>