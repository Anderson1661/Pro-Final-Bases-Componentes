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
$codigo_postal = getRequestData('codigo_postal');
$telefonos = getRequestData('telefonos');

if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
    empty($direccion) || empty($correo) || empty($id_genero) || empty($codigo_postal)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
} else {
    // Escapar datos
    $identificacion = mysqli_real_escape_string($link, $identificacion);
    $id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
    $nombre = mysqli_real_escape_string($link, $nombre);
    $direccion = mysqli_real_escape_string($link, $direccion);
    $correo = mysqli_real_escape_string($link, $correo);
    $id_genero = mysqli_real_escape_string($link, $id_genero);
    $codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
    
    // Iniciar transacción
    mysqli_begin_transaction($link);
    
    try {
        // Insertar administrador
        $sql_admin = "INSERT INTO administrador (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal) 
                     VALUES ('$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$codigo_postal')";
        $res_admin = mysqli_query($link, $sql_admin);
        
        if (!$res_admin) {
            throw new Exception(mysqli_error($link));
        }
        
        $id_administrador = mysqli_insert_id($link);
        
        // Insertar teléfonos
        if (!empty($telefonos)) {
            $telefonos_array = json_decode($telefonos, true);
            
            if (isset($telefonos_array['telefono1'])) {
                $tel1 = mysqli_real_escape_string($link, $telefonos_array['telefono1']);
                $sql_tel1 = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES ('$id_administrador', '$tel1')";
                if (!mysqli_query($link, $sql_tel1)) {
                    throw new Exception(mysqli_error($link));
                }
            }
            
            if (isset($telefonos_array['telefono2']) && !empty($telefonos_array['telefono2'])) {
                $tel2 = mysqli_real_escape_string($link, $telefonos_array['telefono2']);
                $sql_tel2 = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES ('$id_administrador', '$tel2')";
                if (!mysqli_query($link, $sql_tel2)) {
                    throw new Exception(mysqli_error($link));
                }
            }
        }
        
        // Confirmar transacción
        mysqli_commit($link);
        echo json_encode(array("success" => "1", "mensaje" => "Administrador registrado correctamente"));
        
    } catch (Exception $e) {
        // Revertir transacción en caso de error
        mysqli_rollback($link);
        echo json_encode(array("success" => "0", "mensaje" => "Error al registrar: " . $e->getMessage()));
    }
}

mysqli_close($link);
?>