<?php
// consultas/administrador/tablas/ruta/consultar_ruta.php

include('../../../../config/conexion.php'); // Ajusta la ruta
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_ruta = isset($input['id_ruta']) ? trim($input['id_ruta']) : '';

if (empty($id_ruta)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de la ruta es requerido"));
} else {
    $id_ruta = mysqli_real_escape_string($link, $id_ruta);
    
    $sql = "SELECT id_ruta, direccion_origen, direccion_destino, id_codigo_postal_origen, 
                   id_codigo_postal_destino, distancia_km, fecha_hora_reserva, fecha_hora_origen, 
                   fecha_hora_destino, id_conductor, id_tipo_servicio, id_cliente, 
                   id_estado_servicio, id_categoria_servicio, id_metodo_pago, total, pago_conductor
            FROM ruta
            WHERE id_ruta = '$id_ruta'";
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        
        // Asegurar que los campos nulos se envíen como strings vacíos si no existen
        $row['fecha_hora_origen'] = $row['fecha_hora_origen'] ?: "";
        $row['fecha_hora_destino'] = $row['fecha_hora_destino'] ?: "";
        $row['id_conductor'] = $row['id_conductor'] ?: "";

        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Ruta encontrada"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró la ruta"));
    }
}

mysqli_close($link);
?>