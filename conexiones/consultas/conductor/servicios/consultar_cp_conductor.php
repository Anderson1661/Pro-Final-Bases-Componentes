<?php
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // 1. Obtener el id_conductor y el codigo_postal
    // CAMBIO CRÍTICO: Añadir 'id_conductor' al SELECT
    $sql = "SELECT id_conductor, codigo_postal FROM conductor WHERE correo = ? LIMIT 1";
    $stmt = mysqli_prepare($link, $sql);
    
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "s", $correo);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        if ($result && mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);
            
            $res["success"] = "1";
            $res["mensaje"] = "Datos del conductor encontrados";
            
            // CAMBIO CRÍTICO: Retornar ambos campos
            $res["id_conductor"] = (int)$row['id_conductor']; 
            $res["codigo_postal"] = $row['codigo_postal']; 
        } else {
            $res["mensaje"] = "Conductor no encontrado.";
        }
        mysqli_stmt_close($stmt);
    } else {
        $res["mensaje"] = "Error al preparar la consulta.";
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>