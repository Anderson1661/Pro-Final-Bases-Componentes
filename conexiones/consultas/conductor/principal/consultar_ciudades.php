<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

// Se esperan id_pais y departamento desde la app
$id_pais = isset($_POST['id_pais']) ? (int)$_POST['id_pais'] : 0;
$departamento = isset($_POST['departamento']) ? trim($_POST['departamento']) : '';

if ($id_pais > 0 && !empty($departamento)) {
    $sql = "SELECT ciudad 
            FROM codigo_postal 
            WHERE id_pais = ? AND departamento = ?";
            
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "is", $id_pais, $departamento);

    if (mysqli_stmt_execute($stmt)) {
        $res1 = mysqli_stmt_get_result($stmt);
        if (mysqli_num_rows($res1) > 0) {
            while ($row = mysqli_fetch_assoc($res1)) {
                $item = array(
                    "nombre"  => $row['ciudad']
                );
                array_push($res['datos'], $item);
            }
            $res["success"] = "1";
        } else {
            $res["success"] = "1";
            $res["mensaje"] = "No hay ciudades para la selección.";
        }
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error en la consulta: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Parámetros incompletos (se requiere id_pais y departamento).";
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
