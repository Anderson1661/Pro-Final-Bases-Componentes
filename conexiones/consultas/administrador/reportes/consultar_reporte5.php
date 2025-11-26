<?php
/**
 * Script para consultar el reporte 5: Análisis por categoría y método de pago
 * 
 * Utiliza la vista: vw_analisis_categoria_metodo_pago
 * Muestra: categoria_servicio, metodo_pago, cantidad_servicios, valor_total, 
 *          valor_promedio, pago_conductores, ganancia_empresa, clientes_unicos, conductores_unicos
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "No se pudieron obtener los datos");

try {
    // Consulta usando la vista vw_analisis_categoria_metodo_pago
    $sql = "SELECT 
                categoria_servicio,
                metodo_pago,
                cantidad_servicios,
                valor_total,
                valor_promedio,
                pago_conductores,
                ganancia_empresa,
                clientes_unicos,
                conductores_unicos
            FROM vw_analisis_categoria_metodo_pago
            ORDER BY valor_total DESC";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $datos_reporte = array();
        
        while ($row = mysqli_fetch_assoc($result)) {
            $datos_reporte[] = array(
                'categoria_servicio' => $row['categoria_servicio'],
                'metodo_pago' => $row['metodo_pago'],
                'cantidad_servicios' => $row['cantidad_servicios'],
                'valor_total' => number_format($row['valor_total'], 0, ',', '.'),
                'valor_promedio' => number_format($row['valor_promedio'], 0, ',', '.'),
                'pago_conductores' => number_format($row['pago_conductores'], 0, ',', '.'),
                'ganancia_empresa' => number_format($row['ganancia_empresa'], 0, ',', '.'),
                'clientes_unicos' => $row['clientes_unicos'],
                'conductores_unicos' => $row['conductores_unicos']
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