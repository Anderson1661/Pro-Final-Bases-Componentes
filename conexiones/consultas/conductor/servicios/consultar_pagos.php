<?php
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$response = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // Buscar el id del conductor a partir del correo
    $sqlConductor = "SELECT id_conductor FROM conductor WHERE correo = ? LIMIT 1";
    $stmtCond = mysqli_prepare($link, $sqlConductor);
    if ($stmtCond) {
        mysqli_stmt_bind_param($stmtCond, "s", $correo);
        mysqli_stmt_execute($stmtCond);
        $resultCond = mysqli_stmt_get_result($stmtCond);
        if ($resultCond && mysqli_num_rows($resultCond) > 0) {
            $rowCond = mysqli_fetch_assoc($resultCond);
            $id_conductor = $rowCond['id_conductor'];

            // Consultar los servicios finalizados y pagos asociados
            $sql = "SELECT 
                        r.id_ruta,
                        r.fecha_hora_destino AS fecha_finalizacion,
                        cte.nombre AS nombre_cliente,
                        ts.descripcion AS tipo_servicio,
                        cs.descripcion AS categoria_servicio,
                        mp.descripcion AS metodo_pago,
                        r.total AS total_servicio,
                        ROUND(r.total * 0.80, 2) AS pago_conductor  -- 80% para el conductor
                    FROM ruta r
                    INNER JOIN cliente cte ON r.id_cliente = cte.id_cliente
                    INNER JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
                    INNER JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
                    INNER JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
                    WHERE r.id_conductor = ?
                      AND r.id_estado_servicio = (SELECT id_estado_servicio FROM estado_servicio WHERE descripcion = 'Finalizado' LIMIT 1)
                    ORDER BY r.fecha_hora_destino DESC";

            $stmt = mysqli_prepare($link, $sql);
            if ($stmt) {
                mysqli_stmt_bind_param($stmt, "i", $id_conductor);
                mysqli_stmt_execute($stmt);
                $result = mysqli_stmt_get_result($stmt);

                $datos = array();
                while ($row = mysqli_fetch_assoc($result)) {
                    $datos[] = array(
                        "id_ruta" => (int)$row['id_ruta'],
                        "fecha_finalizacion" => $row['fecha_finalizacion'],
                        "nombre_cliente" => $row['nombre_cliente'],
                        "tipo_servicio" => $row['tipo_servicio'],
                        "categoria_servicio" => $row['categoria_servicio'],
                        "metodo_pago" => $row['metodo_pago'],
                        "total" => (float)$row['total_servicio'],
                        "pago_conductor" => (float)$row['pago_conductor']
                    );
                }

                if (count($datos) > 0) {
                    $response["success"] = "1";
                    $response["mensaje"] = "Pagos encontrados";
                    $response["datos"] = $datos;
                } else {
                    $response["mensaje"] = "No se encontraron pagos finalizados para este conductor.";
                }

                mysqli_stmt_close($stmt);
            } else {
                $response["mensaje"] = "Error al preparar la consulta de pagos.";
            }
        } else {
            $response["mensaje"] = "No se encontró conductor con ese correo.";
        }
        mysqli_stmt_close($stmtCond);
    } else {
        $response["mensaje"] = "Error al preparar consulta de conductor.";
    }
}

echo json_encode($response, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
