<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

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
                -- Nuevo campo: URL de foto del conductor
                cnd.url_foto AS url_foto_conductor
            FROM ruta r
            JOIN cliente c ON r.id_cliente = c.id_cliente
            JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
            JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
            JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
            JOIN codigo_postal co ON r.id_codigo_postal_origen = co.id_codigo_postal
            JOIN codigo_postal cd ON r.id_codigo_postal_destino = cd.id_codigo_postal
            LEFT JOIN conductor cnd ON r.id_conductor = cnd.id_conductor  -- LEFT JOIN para obtener datos del conductor
            WHERE c.correo = ?
            ORDER BY r.fecha_hora_reserva DESC";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        if (mysqli_num_rows($result) > 0) {
            $historial = array();
            while ($row = mysqli_fetch_assoc($result)) {
                $historial[] = $row;
            }
            $res["datos"] = $historial;
            $res["success"] = "1";
            $res["mensaje"] = "Historial encontrado";
        } else {
            $res["success"] = "1"; // Éxito, pero sin datos
            $res["mensaje"] = "No tienes servicios en tu historial.";
        }
    } else {
        $res["mensaje"] = "Error en la consulta: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>