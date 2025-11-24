<?php
/**
 * Script para consultar información básica del perfil de un cliente.
 * 
 * Recibe el correo del cliente y devuelve su dirección y ubicación (departamento, ciudad).
 * Se utiliza para mostrar información rápida en la cabecera o resumen del perfil.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();

$correo = isset($_POST['correo']) ? trim($_POST['correo']) : '';

if (!empty($correo)) {
    // Consulta con JOIN a codigo_postal para obtener nombres legibles de ubicación
    $sql = "SELECT 
                c.direccion, 
                cp.departamento, 
                cp.ciudad 
            FROM cliente c
            JOIN codigo_postal cp ON c.codigo_postal = cp.id_codigo_postal
            WHERE c.correo = ?";
            
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);

    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        if (mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);
            $res['datos'] = $row;
            $res["success"] = "1";
        } else {
            $res["success"] = "0";
            $res["mensaje"] = "No se encontró un cliente con ese correo.";
        }
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error en la consulta: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
} else {
    $res["success"] = "0";
    $res["mensaje"] = "Parámetro 'correo' no proporcionado.";
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
