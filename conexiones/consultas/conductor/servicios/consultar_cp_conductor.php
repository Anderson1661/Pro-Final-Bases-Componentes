<?php
/**
 * Script para consultar el código postal y tipo de servicio de un conductor.
 * 
 * Recibe el correo del conductor.
 * Devuelve el ID del conductor, su código postal y el tipo de servicio de su vehículo.
 * Se utiliza para filtrar los servicios disponibles que coincidan con su ubicación y tipo.
 */

include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // MODIFICACIÓN: Obtener id_conductor, codigo_postal Y id_tipo_servicio
    $sql = "SELECT 
                c.id_conductor, 
                c.codigo_postal, 
                v.id_tipo_servicio 
            FROM conductor c
            JOIN vehiculo v ON c.placa_vehiculo = v.placa
            WHERE c.correo = ? LIMIT 1";
    $stmt = mysqli_prepare($link, $sql);
    
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "s", $correo);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        if ($result && mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);
            
            $res["success"] = "1";
            $res["mensaje"] = "Datos del conductor encontrados";
            
            // Retornar todos los campos necesarios
            $res["id_conductor"] = (int)$row['id_conductor']; 
            $res["codigo_postal"] = $row['codigo_postal'];
            $res["id_tipo_servicio"] = (int)$row['id_tipo_servicio']; // NUEVO CAMPO
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