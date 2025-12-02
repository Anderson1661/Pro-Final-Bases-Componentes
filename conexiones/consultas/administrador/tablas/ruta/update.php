<?php
// consultas/administrador/tablas/ruta/update.php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Asegúrate de ajustar la ruta a tu archivo de conexión si es necesario
    include('../../../../config/conexion.php'); 
    $link = Conectar();
    
    // Obtener datos del cuerpo JSON
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Campos inmutables/no editables (solo para el WHERE)
    $id_ruta = isset($input['id_ruta']) ? $input['id_ruta'] : '';
    
    // Campos editables (14 en total)
    $direccion_origen = isset($input['direccion_origen']) ? $input['direccion_origen'] : '';
    $direccion_destino = isset($input['direccion_destino']) ? $input['direccion_destino'] : '';
    $id_codigo_postal_origen = isset($input['id_codigo_postal_origen']) ? $input['id_codigo_postal_origen'] : '';
    $id_codigo_postal_destino = isset($input['id_codigo_postal_destino']) ? $input['id_codigo_postal_destino'] : '';
    $distancia_km = isset($input['distancia_km']) ? $input['distancia_km'] : '';
    $fecha_hora_reserva = isset($input['fecha_hora_reserva']) ? $input['fecha_hora_reserva'] : '';
    $fecha_hora_origen = isset($input['fecha_hora_origen']) ? $input['fecha_hora_origen'] : ''; // Puede ser NULL
    $fecha_hora_destino = isset($input['fecha_hora_destino']) ? $input['fecha_hora_destino'] : ''; // Puede ser NULL
    $id_conductor = isset($input['id_conductor']) ? $input['id_conductor'] : ''; // Puede ser NULL
    $id_tipo_servicio = isset($input['id_tipo_servicio']) ? $input['id_tipo_servicio'] : '';
    $id_cliente = isset($input['id_cliente']) ? $input['id_cliente'] : '';
    $id_estado_servicio = isset($input['id_estado_servicio']) ? $input['id_estado_servicio'] : '';
    $id_categoria_servicio = isset($input['id_categoria_servicio']) ? $input['id_categoria_servicio'] : '';
    $id_metodo_pago = isset($input['id_metodo_pago']) ? $input['id_metodo_pago'] : '';

    
    // Validar campos requeridos
    if (empty($id_ruta) || empty($direccion_origen) || empty($direccion_destino) || empty($id_codigo_postal_origen) || 
        empty($id_codigo_postal_destino) || empty($distancia_km) || empty($fecha_hora_reserva) || 
        empty($id_tipo_servicio) || empty($id_cliente) || empty($id_estado_servicio) || 
        empty($id_categoria_servicio) || empty($id_metodo_pago)) {
        
        echo json_encode(array("success" => "0", "mensaje" => "Faltan campos obligatorios."));
        mysqli_close($link);
        exit;
    }

    // Escapar y sanitizar
    $id_ruta = mysqli_real_escape_string($link, $id_ruta);
    $direccion_origen = mysqli_real_escape_string($link, $direccion_origen);
    $direccion_destino = mysqli_real_escape_string($link, $direccion_destino);
    $id_codigo_postal_origen = mysqli_real_escape_string($link, $id_codigo_postal_origen);
    $id_codigo_postal_destino = mysqli_real_escape_string($link, $id_codigo_postal_destino);
    $distancia_km = mysqli_real_escape_string($link, $distancia_km);
    $fecha_hora_reserva = mysqli_real_escape_string($link, $fecha_hora_reserva);
    $fecha_hora_origen = mysqli_real_escape_string($link, $fecha_hora_origen);
    $fecha_hora_destino = mysqli_real_escape_string($link, $fecha_hora_destino);
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
    $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
    $id_cliente = mysqli_real_escape_string($link, $id_cliente);
    $id_estado_servicio = mysqli_real_escape_string($link, $id_estado_servicio);
    $id_categoria_servicio = mysqli_real_escape_string($link, $id_categoria_servicio);
    $id_metodo_pago = mysqli_real_escape_string($link, $id_metodo_pago);
    
    // 1. Validaciones de existencia de FKs (omitiendo id_conductor si es vacío, que es manejado por NULLIF)
    $fks_to_check = [
        "codigo_postal" => [$id_codigo_postal_origen, $id_codigo_postal_destino],
        "tipo_servicio" => [$id_tipo_servicio],
        "cliente" => [$id_cliente],
        "estado_servicio" => [$id_estado_servicio],
        "categoria_servicio" => [$id_categoria_servicio],
        "metodo_pago" => [$id_metodo_pago],
    ];

    foreach ($fks_to_check as $table => $ids) {
        foreach ($ids as $id) {
            if (!empty($id)) {
                $pk_column = ($table == "codigo_postal") ? "id_codigo_postal" : "id_" . $table;
                $sql_check = "SELECT 1 FROM $table WHERE $pk_column = '$id'";
                $res_check = mysqli_query($link, $sql_check);
                
                if (mysqli_num_rows($res_check) == 0) {
                    echo json_encode(array("success" => "0", "mensaje" => "El ID de $table ('$id') no existe."));
                    mysqli_close($link);
                    exit;
                }
            }
        }
    }
    
    // Validar id_conductor si no está vacío
    if (!empty($id_conductor)) {
        $sql_check_cond = "SELECT 1 FROM conductor WHERE id_conductor = '$id_conductor'";
        $res_check_cond = mysqli_query($link, $sql_check_cond);
        if (mysqli_num_rows($res_check_cond) == 0) {
            echo json_encode(array("success" => "0", "mensaje" => "El ID de conductor ('$id_conductor') no existe."));
            mysqli_close($link);
            exit;
        }
    }

    // 2. Realizar el UPDATE
    // Usamos NULLIF(TRIM('$value'), '') para los campos que pueden ser NULL.
    $sql = "UPDATE ruta SET 
            direccion_origen='$direccion_origen', 
            direccion_destino='$direccion_destino', 
            id_codigo_postal_origen='$id_codigo_postal_origen', 
            id_codigo_postal_destino='$id_codigo_postal_destino', 
            distancia_km='$distancia_km', 
            fecha_hora_reserva='$fecha_hora_reserva', 
            fecha_hora_origen=NULLIF(TRIM('$fecha_hora_origen'), ''), 
            fecha_hora_destino=NULLIF(TRIM('$fecha_hora_destino'), ''), 
            id_conductor=NULLIF(TRIM('$id_conductor'), ''), 
            id_tipo_servicio='$id_tipo_servicio', 
            id_cliente='$id_cliente', 
            id_estado_servicio='$id_estado_servicio', 
            id_categoria_servicio='$id_categoria_servicio', 
            id_metodo_pago='$id_metodo_pago' 
            WHERE id_ruta='$id_ruta'";
    
    $res = mysqli_query($link, $sql);
    
    if ($res) {
        if (mysqli_affected_rows($link) > 0) {
            echo json_encode(array("success" => "1", "mensaje" => "Ruta/Servicio actualizada correctamente"));
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "No se realizó ningún cambio o no se encontró el ID."));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
    }
    
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido. Use POST"));
}
?>