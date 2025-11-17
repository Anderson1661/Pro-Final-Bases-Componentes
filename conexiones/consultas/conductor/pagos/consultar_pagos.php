<?php
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = ["success" => "0", "mensaje" => "Parámetros incompletos"];

// Aceptar correo por POST o GET para facilitar pruebas desde navegador/curl
$correo = null;
if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);
} elseif (isset($_GET['correo'])) {
    $correo = trim($_GET['correo']);
}

if ($correo !== null && $correo !== '') {
    $sql = "SELECT 
                r.id_ruta,
                r.fecha_hora_destino AS fecha_finalizacion,
                cte.nombre AS nombre_cliente,
                ts.descripcion AS tipo_servicio,
                cs.descripcion AS categoria_servicio,
                mp.descripcion AS metodo_pago,
                r.total,
                r.pago_conductor
            FROM ruta r
            INNER JOIN conductor cond ON r.id_conductor = cond.id_conductor
            INNER JOIN cliente cte ON r.id_cliente = cte.id_cliente
            INNER JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
            INNER JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
            INNER JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
            INNER JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
            WHERE cond.correo = ? AND es.descripcion = 'Finalizado'
            ORDER BY r.fecha_hora_destino DESC";

    $stmt = mysqli_prepare($link, $sql);
    if ($stmt === false) {
        $res["mensaje"] = "Error al preparar la consulta: " . mysqli_error($link);
        echo json_encode($res, JSON_UNESCAPED_UNICODE);
        exit;
    }

    if (!mysqli_stmt_bind_param($stmt, "s", $correo)) {
        $res["mensaje"] = "Error al enlazar parámetros: " . mysqli_stmt_error($stmt);
        echo json_encode($res, JSON_UNESCAPED_UNICODE);
        mysqli_stmt_close($stmt);
        exit;
    }

    if (!mysqli_stmt_execute($stmt)) {
        $res["mensaje"] = "Error al ejecutar la consulta: " . mysqli_stmt_error($stmt);
        echo json_encode($res, JSON_UNESCAPED_UNICODE);
        mysqli_stmt_close($stmt);
        exit;
    }

    $result = mysqli_stmt_get_result($stmt);
    if ($result === false) {
        $res["mensaje"] = "Error obteniendo resultados: " . mysqli_stmt_error($stmt);
        echo json_encode($res, JSON_UNESCAPED_UNICODE);
        mysqli_stmt_close($stmt);
        exit;
    }

    if (mysqli_num_rows($result) > 0) {
        $datos = [];
        while ($row = mysqli_fetch_assoc($result)) {
            $datos[] = $row;
        }
        $res["success"] = "1";
        $res["mensaje"] = "Pagos encontrados";
        $res["datos"] = $datos;
    } else {
        $res["mensaje"] = "No hay pagos registrados.";
    }

    mysqli_free_result($result);
    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
?>
