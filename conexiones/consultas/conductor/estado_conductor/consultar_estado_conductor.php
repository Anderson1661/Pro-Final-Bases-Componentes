<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_conductor'])) {
    $id_conductor = (int)trim($_POST['id_conductor']);

    $sql = "SELECT 
                c.id_estado_conductor,
                ec.descripcion 
            FROM conductor c
            JOIN estado_conductor ec ON c.id_estado_conductor = ec.id_estado_conductor
            WHERE c.id_conductor = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "i", $id_conductor);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $estado_data = mysqli_fetch_assoc($result);
        $res["success"] = "1";
        $res["mensaje"] = "Estado encontrado";
        $res["datos"] = $estado_data;
    } else {
        $res["mensaje"] = "Conductor no encontrado.";
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>