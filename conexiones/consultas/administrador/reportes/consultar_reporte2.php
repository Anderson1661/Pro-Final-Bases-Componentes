<?php
/**
 * Script para consultar el reporte 2: Cantidad de servicios por mes y tipo
 * 
 * Utiliza la vista: vw_cantidad_servicios_por_mes_y_tipo
 * Muestra: periodo, tipo_servicio, cantidad_servicios
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudieron obtener los datos");

try {
    // Consulta usando la vista vw_cantidad_servicios_por_mes_y_tipo
    $sql = "SELECT 
                periodo,
                tipo_servicio,
                cantidad_servicios
            FROM vw_cantidad_servicios_por_mes_y_tipo
            ORDER BY periodo, tipo_servicio";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $datos_reporte = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $datos_reporte[] = array(
                'periodo' => $row['periodo'],
                'tipo_servicio' => $row['tipo_servicio'],
                'cantidad_servicios' => $row['cantidad_servicios']
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