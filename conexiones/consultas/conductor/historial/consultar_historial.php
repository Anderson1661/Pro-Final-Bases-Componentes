<?php
/**
 * Script para consultar el historial de servicios de un conductor.
 * 
 * Recibe el correo del conductor y devuelve una lista de servicios realizados.
 * Incluye detalles como origen, destino, costo, estado y método de pago.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = ["success" => 0, "mensaje" => "Parámetros incompletos"];

if (!isset($_POST['correo'])) {
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    mysqli_close($link);
    exit;
}

$correo = trim($_POST['correo']);
if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    $res['mensaje'] = "Correo inválido";
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    mysqli_close($link);
    exit;
}

// Consulta compleja con múltiples JOINs para obtener descripciones legibles
$sql = "SELECT 
            r.id_ruta,
            r.fecha_hora_reserva,
            r.direccion_origen,
            co.ciudad AS ciudad_origen,
            r.direccion_destino,
            cd.ciudad AS ciudad_destino,
            ts.descripcion AS tipo_servicio,
            es.descripcion AS estado_servicio,
            mp.descripcion AS metodo_pago,
            r.costo,
            r.distancia
        FROM ruta r
        JOIN conductor cond ON r.id_conductor = cond.id_conductor
        JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
        JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
        JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
        JOIN codigo_postal co ON r.id_codigo_postal_origen = co.id_codigo_postal
        JOIN codigo_postal cd ON r.id_codigo_postal_destino = cd.id_codigo_postal
        WHERE cond.correo = ?
        ORDER BY r.fecha_hora_reserva DESC";

if ($stmt = mysqli_prepare($link, $sql)) {
    mysqli_stmt_bind_param($stmt, "s", $correo);
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        $historial = [];

        if ($result !== false) {
            while ($row = mysqli_fetch_assoc($result)) {
                $historial[] = $row;
            }
        } else {
            // Fallback si mysqli_stmt_get_result no está disponible (versiones antiguas de PHP/MySQLnd)
            mysqli_stmt_bind_result($stmt,
                $id_ruta, $fecha_hora_reserva, $direccion_origen, $ciudad_origen,
                $direccion_destino, $ciudad_destino, $tipo_servicio, $estado_servicio,
                $metodo_pago, $costo, $distancia
            );
            while (mysqli_stmt_fetch($stmt)) {
                $historial[] = [
                    "id_ruta" => $id_ruta,
                    "fecha_hora_reserva" => $fecha_hora_reserva,
                    "direccion_origen" => $direccion_origen,
                    "ciudad_origen" => $ciudad_origen,
                    "direccion_destino" => $direccion_destino,
                    "ciudad_destino" => $ciudad_destino,
                    "tipo_servicio" => $tipo_servicio,
                    "estado_servicio" => $estado_servicio,
                    "metodo_pago" => $metodo_pago,
                    "costo" => $costo,
                    "distancia" => $distancia
                ];
            }
        }

        if (count($historial) > 0) {
            $res["success"] = 1;
            $res["mensaje"] = "Historial encontrado";
            $res["datos"] = $historial;
        } else {
            $res["success"] = 1;
            $res["mensaje"] = "No tienes servicios en tu historial.";
            $res["datos"] = [];
        }
    } else {
        $res["mensaje"] = "Error en la ejecución: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
} else {
    $res["mensaje"] = "Error al preparar la consulta: " . mysqli_error($link);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
