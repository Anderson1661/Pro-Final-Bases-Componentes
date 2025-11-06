<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_ruta = isset($_REQUEST['id_ruta']) ? $_REQUEST['id_ruta'] : '';
    $direccion_origen = isset($_REQUEST['direccion_origen']) ? $_REQUEST['direccion_origen'] : '';
    $direccion_destino = isset($_REQUEST['direccion_destino']) ? $_REQUEST['direccion_destino'] : '';
    $id_codigo_postal_origen = isset($_REQUEST['id_codigo_postal_origen']) ? $_REQUEST['id_codigo_postal_origen'] : '';
    $id_codigo_postal_destino = isset($_REQUEST['id_codigo_postal_destino']) ? $_REQUEST['id_codigo_postal_destino'] : '';
    $distancia_km = isset($_REQUEST['distancia_km']) ? $_REQUEST['distancia_km'] : '';
    $fecha_hora_reserva = isset($_REQUEST['fecha_hora_reserva']) ? $_REQUEST['fecha_hora_reserva'] : '';
    $fecha_hora_origen = isset($_REQUEST['fecha_hora_origen']) ? $_REQUEST['fecha_hora_origen'] : '';
    $fecha_hora_destino = isset($_REQUEST['fecha_hora_destino']) ? $_REQUEST['fecha_hora_destino'] : '';
    $id_conductor = isset($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : '';
    $id_tipo_servicio = isset($_REQUEST['id_tipo_servicio']) ? $_REQUEST['id_tipo_servicio'] : '';
    $id_cliente = isset($_REQUEST['id_cliente']) ? $_REQUEST['id_cliente'] : '';
    $id_estado_servicio = isset($_REQUEST['id_estado_servicio']) ? $_REQUEST['id_estado_servicio'] : '';
    $id_categoria_servicio = isset($_REQUEST['id_categoria_servicio']) ? $_REQUEST['id_categoria_servicio'] : '';
    $id_metodo_pago = isset($_REQUEST['id_metodo_pago']) ? $_REQUEST['id_metodo_pago'] : '';
    
    if (empty($id_ruta)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else if (empty($direccion_origen) || empty($direccion_destino) || empty($id_codigo_postal_origen) || 
               empty($id_codigo_postal_destino) || empty($distancia_km) || empty($fecha_hora_reserva) || 
               empty($fecha_hora_origen) || empty($fecha_hora_destino) || empty($id_conductor) || 
               empty($id_tipo_servicio) || empty($id_cliente) || empty($id_estado_servicio) || 
               empty($id_categoria_servicio) || empty($id_metodo_pago)) {
        echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
    } else {
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
        
        // total y pago_conductor son recalculados por triggers
        $sql = "UPDATE ruta SET direccion_origen='$direccion_origen', direccion_destino='$direccion_destino', 
                id_codigo_postal_origen='$id_codigo_postal_origen', id_codigo_postal_destino='$id_codigo_postal_destino', 
                distancia_km='$distancia_km', fecha_hora_reserva='$fecha_hora_reserva', 
                fecha_hora_origen='$fecha_hora_origen', fecha_hora_destino='$fecha_hora_destino', 
                id_conductor='$id_conductor', id_tipo_servicio='$id_tipo_servicio', id_cliente='$id_cliente', 
                id_estado_servicio='$id_estado_servicio', id_categoria_servicio='$id_categoria_servicio', 
                id_metodo_pago='$id_metodo_pago' WHERE id_ruta='$id_ruta'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Ruta actualizada correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

