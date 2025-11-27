<?php
include('../../../../config/conexion.php');
$link = Conectar();

// FUNCIÓN PARA LEER TANTO JSON COMO FORM-DATA
function getRequestData($key) {
    // Primero intenta leer de JSON
    $input = json_decode(file_get_contents('php://input'), true);
    if (isset($input[$key])) {
        return $input[$key];
    }
    
    // Si no viene en JSON, busca en $_REQUEST (form-data)
    return isset($_REQUEST[$key]) ? $_REQUEST[$key] : '';
}

// USAR LA FUNCIÓN EN LUGAR DE $_REQUEST
$identificacion = getRequestData('identificacion');
$id_tipo_identificacion = getRequestData('id_tipo_identificacion');
$nombre = getRequestData('nombre');
$direccion = getRequestData('direccion');
$correo = getRequestData('correo');
$id_genero = getRequestData('id_genero');
$id_pais_nacionalidad = getRequestData('id_pais_nacionalidad');
$codigo_postal = getRequestData('codigo_postal');
$telefonos = getRequestData('telefonos');

if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
    empty($direccion) || empty($correo) || empty($id_genero) || 
    empty($id_pais_nacionalidad) || empty($codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    // Escapar datos
    $identificacion = mysqli_real_escape_string($link, $identificacion);
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $nombre = mysqli_real_escape_string($link, $nombre);
    $direccion = mysqli_real_escape_string($link, $direccion);
    $correo = mysqli_real_escape_string($link, $correo);
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $id_pais_nacionalidad = mysqli_real_escape_string($link, $id_pais_nacionalidad);
    $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
    
    // Iniciar transacción
    mysqli_begin_transaction($link);
    
    try {
        // Insertar cliente
        $sql_cliente = "INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) 
                       VALUES ('$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$id_pais_nacionalidad', '$codigo_postal')";
        $res_cliente = mysqli_query($link, $sql_cliente);
        
        if (!$res_cliente) {
            throw new Exception(mysqli_error($link));
        }
        
        $id_cliente = mysqli_insert_id($link);
        
        // Confirmar transacción
        mysqli_commit($link);
        echo json_encode(array("success" => "1", "mensaje" => "Cliente registrado correctamente"));
        
    } catch (Exception $e) {
        // Revertir transacción en caso de error
        mysqli_rollback($link);
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . $e->getMessage()));
    }
}

mysqli_close($link);
?>