<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

if (isset($_POST['pais']) && isset($_POST['departamento']) && isset($_POST['ciudad'])) {
    $nombre_pais = trim($_POST['pais']);
    $departamento = trim($_POST['departamento']);
    $ciudad = trim($_POST['ciudad']);

    $sql = "
        SELECT 
            cp.id_codigo_postal AS codigo_postal
        FROM codigo_postal cp
        INNER JOIN pais p ON cp.id_pais = p.id_pais
        WHERE 
            p.nombre = ? AND 
            cp.departamento = ? AND 
            cp.ciudad = ?
        LIMIT 1
    ";

    $stmt = mysqli_prepare($link, $sql);

    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "sss", $nombre_pais, $departamento, $ciudad);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        if ($result && mysqli_num_rows($result) > 0) {
            $codigo_postal_data = mysqli_fetch_assoc($result);
            $res["success"] = "1";
            $res["mensaje"] = "Código postal encontrado correctamente.";
            $res["datos"] = $codigo_postal_data;
        } else {
            $res["mensaje"] = "No se encontró un código postal para la ubicación especificada.";
        }

        mysqli_stmt_close($stmt);
    } else {
        $res["mensaje"] = "Error al preparar la consulta SQL.";
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>

