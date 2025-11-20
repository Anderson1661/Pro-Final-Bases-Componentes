<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

$id_marca = isset($_POST['id_marca']) ? (int)$_POST['id_marca'] : 0;

if ($id_marca > 0) {
    $sql = "SELECT id_linea 
            FROM linea_vehiculo 
            WHERE id_marca = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "i", $id_marca);
    
    if (mysqli_stmt_execute($stmt)) {
        $res1 = mysqli_stmt_get_result($stmt);
        if (mysqli_num_rows($res1) > 0) {
            while ($row = mysqli_fetch_assoc($res1)) {
                $item = array(
                    "linea" => $row['id_linea']
                );
                array_push($res['datos'], $item);
            }
            $res["success"] = "1";
        } else {
            $res["success"] = "1";
            $res["mensaje"] = "No hay líneas para la marca seleccionada.";
        }
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error en la consulta: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
} else {
    $res["success"] = "0";
    $res["mensaje"] = "ID de marca no válido o no proporcionado.";
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>