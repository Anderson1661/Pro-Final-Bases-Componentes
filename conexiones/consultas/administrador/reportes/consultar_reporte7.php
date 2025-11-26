<?php
/**
 * Script para consultar el reporte 7: Estadísticas de conductores por ciudad
 * 
 * Utiliza la tabla materializada: mv_estadisticas_conductores_ciudad
 * Muestra: id_ciudad, ciudad, departamento, total_conductores, conductores_activos,
 *          conductores_conectados, vehiculos_activos, promedio_servicios_por_conductor, ultima_actualizacion
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudieron obtener los datos");

try {
    // Consulta usando la tabla materializada mv_estadisticas_conductores_ciudad
    $sql = "SELECT 
                id_ciudad,
                ciudad,
                departamento,
                total_conductores,
                conductores_activos,
                conductores_conectados,
                vehiculos_activos,
                promedio_servicios_por_conductor,
                ultima_actualizacion
            FROM mv_estadisticas_conductores_ciudad
            ORDER BY total_conductores DESC, ciudad";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $datos_reporte = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $datos_reporte[] = array(
                'id_ciudad' => $row['id_ciudad'],
                'ciudad' => $row['ciudad'],
                'departamento' => $row['departamento'],
                'total_conductores' => $row['total_conductores'],
                'conductores_activos' => $row['conductores_activos'],
                'conductores_conectados' => $row['conductores_conectados'],
                'vehiculos_activos' => $row['vehiculos_activos'],
                'promedio_servicios_por_conductor' => number_format($row['promedio_servicios_por_conductor'], 2, ',', '.'),
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