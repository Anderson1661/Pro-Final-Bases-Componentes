<?php
/**
 * Script para consultar el reporte 3: Clientes con servicios
 * 
 * Utiliza la vista: vw_clientes_con_servicios
 * Muestra: id_cliente, nombre, correo, cantidad_servicios, valor_total
 * Ahora con filtros de fecha
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudieron obtener los datos");

try {
    // Obtener parámetros de fecha
    $fecha_desde = isset($_POST['fecha_desde']) ? $_POST['fecha_desde'] : null;
    $fecha_hasta = isset($_POST['fecha_hasta']) ? $_POST['fecha_hasta'] : null;

    // Consulta base usando la vista vw_clientes_con_servicios
    $sql = "SELECT 
                id_cliente,
                nombre,
                correo,
                cantidad_servicios,
                valor_total
            FROM vw_clientes_con_servicios
            WHERE 1=1";
    
    // Agregar filtros de fecha si se proporcionan
    if ($fecha_desde && $fecha_hasta) {
        // Convertir formato de fecha si es necesario
        $fecha_desde_mysql = date('Y-m-d H:i:s', strtotime($fecha_desde));
        $fecha_hasta_mysql = date('Y-m-d H:i:s', strtotime($fecha_hasta));
        
        // Cambiar la consulta para usar filtros de fecha
        $sql = "SELECT 
                    c.id_cliente,
                    c.nombre,
                    c.correo,
                    COUNT(r.id_ruta) AS cantidad_servicios,
                    COALESCE(SUM(r.total), 0) AS valor_total
                FROM cliente c
                JOIN ruta r ON c.id_cliente = r.id_cliente
                WHERE r.fecha_hora_reserva BETWEEN ? AND ?
                GROUP BY c.id_cliente, c.nombre, c.correo
                ORDER BY cantidad_servicios DESC";
        
        $stmt = mysqli_prepare($link, $sql);
        mysqli_stmt_bind_param($stmt, "ss", $fecha_desde_mysql, $fecha_hasta_mysql);
    } else {
        // Sin filtros de fecha
        $stmt = mysqli_prepare($link, $sql);
    }
    
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $datos_reporte = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $datos_reporte[] = array(
                'id_cliente' => $row['id_cliente'],
                'nombre' => $row['nombre'],
                'correo' => $row['correo'],
                'cantidad_servicios' => $row['cantidad_servicios'],
                'valor_total' => number_format($row['valor_total'], 0, ',', '.')
            );
        }

        $res["datos"] = $datos_reporte;
        $res["success"] = "1";
        $res["mensaje"] = "Datos del reporte obtenidos correctamente";
        $res["total_registros"] = count($datos_reporte);
        
        // Agregar información de filtros aplicados
        if ($fecha_desde && $fecha_hasta) {
            $res["filtros"] = array(
                "fecha_desde" => $fecha_desde,
                "fecha_hasta" => $fecha_hasta
            );
        }

    } else {
        $res["mensaje"] = "No se encontraron datos para el reporte con los filtros aplicados.";
    }
    
    mysqli_stmt_close($stmt);

} catch (Exception $e) {
    $res["mensaje"] = "Error: " . $e->getMessage();
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>