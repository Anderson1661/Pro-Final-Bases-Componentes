<?php
/**
 * Script para consultar el reporte 6: Resumen mensual de ingresos
 * 
 * Utiliza la tabla materializada: mv_resumen_mensual_ingresos
 * Muestra: anio_mes, total_ingresos, total_servicios, promedio_por_servicio,
 *          ingresos_conductor, ingresos_empresa, ultima_actualizacion
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudieron obtener los datos");

try {
    // Consulta usando la tabla materializada mv_resumen_mensual_ingresos
    $sql = "SELECT 
                anio_mes,
                total_ingresos,
                total_servicios,
                promedio_por_servicio,
                ingresos_conductor,
                ingresos_empresa,
                ultima_actualizacion
            FROM mv_resumen_mensual_ingresos
            ORDER BY anio_mes DESC";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $datos_reporte = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $datos_reporte[] = array(
                'anio_mes' => $row['anio_mes'],
                'total_ingresos' => number_format($row['total_ingresos'], 0, ',', '.'),
                'total_servicios' => $row['total_servicios'],
                'promedio_por_servicio' => number_format($row['promedio_por_servicio'], 0, ',', '.'),
                'ingresos_conductor' => number_format($row['ingresos_conductor'], 0, ',', '.'),
                'ingresos_empresa' => number_format($row['ingresos_empresa'], 0, ',', '.'),
                'ultima_actualizacion' => $row['ultima_actualizacion']
            );
        }

        $res["datos"] = $datos_reporte;
        $res["success"] = "1";
        $res["mensaje"] = "Datos del reporte obtenidos correctamente";
        $res["total_registros"] = count($datos_reporte);

    } else {
        $res["mensaje"] = "No se encontraron datos para el reporte.";
    }
    
    mysqli_stmt_close($stmt);

} catch (Exception $e) {
    $res["mensaje"] = "Error: " . $e->getMessage();
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>