<?php
// En /config/conexion.php se encuentra la función Conectar()
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['codigo_postal'])) {
    $codigo_postal = trim($_POST['codigo_postal']);

    // Asumimos que el ID de estado_servicio 'Pendiente' es 4 según los datos de prueba
    $id_estado_pendiente = 4;

    $sql = "SELECT 
                r.id_ruta,
                r.fecha_hora_reserva AS fecha_inicio, -- Usamos fecha_hora_reserva para servicios pendientes
                r.direccion_origen,
                cp_ori.ciudad AS ciudad_origen,
                r.direccion_destino,
                cp_dest.ciudad AS ciudad_destino,
                ts.descripcion AS tipo_servicio,
                es.descripcion AS estado,
                mp.descripcion AS metodo_pago
            FROM ruta r
            LEFT JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
            LEFT JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
            LEFT JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
            LEFT JOIN codigo_postal cp_ori ON r.id_codigo_postal_origen = cp_ori.id_codigo_postal
            LEFT JOIN codigo_postal cp_dest ON r.id_codigo_postal_destino = cp_dest.id_codigo_postal
            WHERE r.id_estado_servicio = ?  -- Solo servicios Pendientes (4)
              AND r.id_codigo_postal_origen = ? -- En el codigo postal del conductor
              AND DATE(r.fecha_hora_reserva) = CURDATE() -- Limitado a servicios de HOY
            ORDER BY r.fecha_hora_reserva ASC";

    $stmt = mysqli_prepare($link, $sql);
    
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "is", $id_estado_pendiente, $codigo_postal);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);

        $lista_servicios = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $lista_servicios[] = $row;
        }

        if (count($lista_servicios) > 0) {
            $res["success"] = "1";
            $res["mensaje"] = "Servicios encontrados";
            $res["datos"] = $lista_servicios;
        } else {
            $res["mensaje"] = "No se encontraron servicios pendientes para hoy en tu zona.";
        }
        mysqli_stmt_close($stmt);
    } else {
        $res["mensaje"] = "Error al preparar la consulta.";
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>