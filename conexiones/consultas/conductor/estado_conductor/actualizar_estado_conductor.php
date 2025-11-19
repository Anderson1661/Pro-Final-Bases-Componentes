<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_conductor']) && isset($_POST['nuevo_estado_id'])) {
    $id_conductor = (int)trim($_POST['id_conductor']);
    $nuevo_estado_id = (int)trim($_POST['nuevo_estado_id']);

    $sql = "UPDATE conductor SET id_estado_conductor = ? WHERE id_conductor = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "ii", $nuevo_estado_id, $id_conductor);
    
    if (mysqli_stmt_execute($stmt)) {
        $res["success"] = "1";
        $res["mensaje"] = "Estado actualizado correctamente";
    } else {
        $res["mensaje"] = "Error al actualizar estado: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>