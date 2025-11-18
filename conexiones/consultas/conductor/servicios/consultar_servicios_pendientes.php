<?php
// En /config/conexion.php se encuentra la función Conectar()
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

// CAMBIO CRÍTICO 1: Ahora se esperan 'codigo_postal' Y 'id_conductor'
if (isset($_POST['codigo_postal']) && isset($_POST['id_conductor'])) {
    $codigo_postal = trim($_POST['codigo_postal']);
    $id_conductor = (int)trim($_POST['id_conductor']); // ID del conductor
    
    // IDs de estado de servicio
    $id_estado_pendiente = 4; // Pendiente
    $id_estado_en_proceso = 2; // En proceso

    $sql = "SELECT 
                r.id_ruta,
                r.id_cliente,                           
                c.nombre AS nombre_cliente,             
                r.fecha_hora_reserva AS fecha_inicio,
                r.direccion_origen,
                cp_ori.ciudad AS ciudad_origen,
                r.direccion_destino,
                cp_dest.ciudad AS ciudad_destino,
                ts.descripcion AS tipo_servicio,
                es.descripcion AS estado,
                mp.descripcion AS metodo_pago,
                r.pago_conductor AS pago_conductor,     
                r.id_estado_servicio AS id_estado
            FROM ruta r
            LEFT JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
            LEFT JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
            LEFT JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
            LEFT JOIN codigo_postal cp_ori ON r.id_codigo_postal_origen = cp_ori.id_codigo_postal
            LEFT JOIN codigo_postal cp_dest ON r.id_codigo_postal_destino = cp_dest.id_codigo_postal
            LEFT JOIN cliente c ON r.id_cliente = c.id_cliente
            WHERE (
                (r.id_estado_servicio = ? AND r.id_codigo_postal_origen = ?) -- Condición 1: Pendiente (4) en mi CP
                OR
                (r.id_estado_servicio = ? AND r.id_conductor = ?)           -- Condición 2: En Proceso (2) asignado a mi ID
            )
            AND DATE(r.fecha_hora_reserva) = CURDATE()
            ORDER BY r.fecha_hora_reserva ASC";

    $stmt = mysqli_prepare($link, $sql);
    
    if ($stmt) {
        // La consulta se preparó correctamente, ahora ejecuta y obtiene resultados
        
        // La cadena de tipos "isii" es correcta: (int, string, int, int)
        mysqli_stmt_bind_param($stmt, "isii", $id_estado_pendiente, $codigo_postal, $id_estado_en_proceso, $id_conductor);
        
        // Verifica la ejecución antes de obtener resultados
        if (mysqli_stmt_execute($stmt)) {
            $result = mysqli_stmt_get_result($stmt);

            $lista_servicios = array();
            while ($row = mysqli_fetch_assoc($result)) {
                $lista_servicios[] = array(
                    'id_ruta' => (int)$row['id_ruta'],
                    'fecha_inicio' => $row['fecha_inicio'],
                    'direccion_origen' => $row['direccion_origen'],
                    'ciudad_origen' => $row['ciudad_origen'],
                    'direccion_destino' => $row['direccion_destino'],
                    'ciudad_destino' => $row['ciudad_destino'],
                    'tipo_servicio' => $row['tipo_servicio'],
                    'estado' => $row['estado'],
                    'metodo_pago' => $row['metodo_pago'],
                    'pago_conductor' => (float)$row['pago_conductor'],
                    'id_estado' => (int)$row['id_estado'],
                    'id_cliente' => (int)$row['id_cliente'],    
                    'nombre_cliente' => $row['nombre_cliente']
                );
            }

            if (count($lista_servicios) > 0) {
                $res["success"] = "1";
                $res["mensaje"] = "Servicios encontrados";
                $res["datos"] = $lista_servicios;
            } else {
                $res["mensaje"] = "No se encontraron servicios disponibles o en curso para hoy.";
            }

        } else {
             // Error de ejecución: (ej. si falla la conexión después de la preparación)
             $res["mensaje"] = "Error al ejecutar la consulta: " . mysqli_stmt_error($stmt);
        }
        
        mysqli_stmt_close($stmt); // Cerrar solo si la preparación fue exitosa
    } 
    else {
        // Error de preparación: (ej. si la sintaxis SQL es realmente mala)
        $res["mensaje"] = "Error al preparar la consulta: " . mysqli_error($link);
    }
} 
else {
    $res = array("success" => "0", "mensaje" => "Parámetros incompletos");
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
// FIN DEL ARCHIVO
?>