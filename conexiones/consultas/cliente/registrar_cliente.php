<?php
include('../../config/conexion.php'); // Asegúrate que la ruta a tu conexión es correcta
$link = Conectar();

// --- Datos principales del cliente ---
$identificacion = isset($_REQUEST['identificacion']) ? $_REQUEST['identificacion'] : '';
$id_tipo_identificacion = isset($_REQUEST['id_tipo_identificacion']) ? $_REQUEST['id_tipo_identificacion'] : '';
$nombre = isset($_REQUEST['nombre']) ? $_REQUEST['nombre'] : '';
$direccion = isset($_REQUEST['direccion']) ? $_REQUEST['direccion'] : '';
$correo = isset($_REQUEST['correo']) ? $_REQUEST['correo'] : '';
$id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
$id_pais_nacionalidad = isset($_REQUEST['id_pais_nacionalidad']) ? $_REQUEST['id_pais_nacionalidad'] : '';
$codigo_postal = isset($_REQUEST['codigo_postal']) ? $_REQUEST['codigo_postal'] : '';

// --- Teléfonos ---
$tel1 = isset($_REQUEST['tel1']) ? $_REQUEST['tel1'] : '';
$tel2 = isset($_REQUEST['tel2']) ? $_REQUEST['tel2'] : '';

// --- Validación de campos requeridos ---
if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
    empty($direccion) || empty($correo) || empty($id_genero) || 
    empty($id_pais_nacionalidad) || empty($codigo_postal) || empty($tel1)) {
    
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos (excepto Teléfono 2) son requeridos"));
    mysqli_close($link);
    exit;
}

// --- Escapar todas las variables ---
$identificacion = mysqli_real_escape_string($link, $identificacion);
$id_tipo_identificacion = mysqli_real_escape_string($link, $id_tipo_identificacion);
$nombre = mysqli_real_escape_string($link, $nombre);
$direccion = mysqli_real_escape_string($link, $direccion);
$correo = mysqli_real_escape_string($link, $correo);
$id_genero = mysqli_real_escape_string($link, $id_genero);
$id_pais_nacionalidad = mysqli_real_escape_string($link, $id_pais_nacionalidad);
$codigo_postal = mysqli_real_escape_string($link, $codigo_postal);
$tel1 = mysqli_real_escape_string($link, $tel1);
$tel2 = mysqli_real_escape_string($link, $tel2);

// --- Iniciar Transacción ---
mysqli_begin_transaction($link);

try {
    // Paso 1: Insertar en la tabla principal 'cliente'
    $sql_cliente = "INSERT INTO cliente (identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal) 
                    VALUES ('$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$id_pais_nacionalidad', '$codigo_postal')";
    
    if (!mysqli_query($link, $sql_cliente)) {
        throw new Exception("Error al registrar datos principales: " . mysqli_error($link));
    }
    
    // Paso 2: Obtener el ID del cliente que acabamos de insertar
    $id_cliente_nuevo = mysqli_insert_id($link);
    
    // Paso 3: Insertar el Teléfono 1 (obligatorio)
    // ########## CORRECCIÓN AQUÍ ##########
    $sql_tel1 = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES ('$id_cliente_nuevo', '$tel1')";
    if (!mysqli_query($link, $sql_tel1)) {
        throw new Exception("Error al registrar teléfono 1: " . mysqli_error($link));
    }

    // Paso 4: Insertar el Teléfono 2 (solo si no está vacío)
    if (!empty($tel2)) {
        // ########## CORRECCIÓN AQUÍ ##########
        $sql_tel2 = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES ('$id_cliente_nuevo', '$tel2')";
        if (!mysqli_query($link, $sql_tel2)) {
            throw new Exception("Error al registrar teléfono 2: " . mysqli_error($link));
        }
    }
    
    // Si todo salió bien, confirmamos los cambios
    mysqli_commit($link);
    echo json_encode(array("success" => "1", "mensaje" => "Cliente registrado correctamente"));

} catch (Exception $e) {
    // Si algo falló, revertimos todos los cambios
    mysqli_rollback($link);
    echo json_encode(array("success" => "0", "mensaje" => $e->getMessage()));
}

mysqli_close($link);
?>