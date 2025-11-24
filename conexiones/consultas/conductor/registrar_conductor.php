<?php
/**
 * Script para registrar un nuevo conductor.
 * 
 * Este proceso es complejo y transaccional, involucrando múltiples tablas:
 * 1. Vehículo: Se registra o valida el vehículo.
 * 2. Conductor: Se crea el perfil del conductor.
 * 3. Teléfonos: Se asocian los números de contacto.
 * 4. Seguridad: Se registran las respuestas a las preguntas de seguridad.
 * 
 * Recibe un JSON con toda la estructura de datos anidada.
 */

include('../../config/conexion.php');
$link = Conectar();

// Leer datos JSON del cuerpo de la solicitud
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validar que existan todos los datos requeridos en la estructura JSON
if (!isset($data['id_tipo_identificacion']) || !isset($data['identificacion']) || 
    !isset($data['nombre']) || !isset($data['direccion']) || !isset($data['correo']) ||
    !isset($data['id_genero']) || !isset($data['id_pais_nacionalidad']) || 
    !isset($data['codigo_postal']) || !isset($data['telefonos']) || 
    !isset($data['vehiculo']) || !isset($data['preguntas_seguridad']) || 
    !isset($data['url_foto'])) {
    
    echo json_encode(array("success" => "0", "mensaje" => "Faltan datos requeridos"));
    mysqli_close($link);
    exit;
}

// Extraer y escapar datos personales
$id_tipo_identificacion = mysqli_real_escape_string($link, $data['id_tipo_identificacion']);
$identificacion = mysqli_real_escape_string($link, $data['identificacion']);
$nombre = mysqli_real_escape_string($link, $data['nombre']);
$direccion = mysqli_real_escape_string($link, $data['direccion']);
$correo = mysqli_real_escape_string($link, $data['correo']);
$id_genero = mysqli_real_escape_string($link, $data['id_genero']);
$id_pais_nacionalidad = mysqli_real_escape_string($link, $data['id_pais_nacionalidad']);
$codigo_postal = mysqli_real_escape_string($link, $data['codigo_postal']);
$url_foto = mysqli_real_escape_string($link, $data['url_foto']);

// Datos vehículo
$vehiculo = $data['vehiculo'];
$placa = mysqli_real_escape_string($link, $vehiculo['placa']);
$marca = mysqli_real_escape_string($link, $vehiculo['marca']);
$linea = mysqli_real_escape_string($link, $vehiculo['linea']);
$modelo = mysqli_real_escape_string($link, $vehiculo['modelo']);
$color = mysqli_real_escape_string($link, $vehiculo['color']);
$tipo_servicio = mysqli_real_escape_string($link, $vehiculo['tipo_servicio']);

// Teléfonos (Array)
$telefonos = $data['telefonos'];

// Preguntas seguridad (Array)
$preguntas_seguridad = $data['preguntas_seguridad'];

// Iniciar transacción global
mysqli_begin_transaction($link);

try {
    // PASO 1: Insertar o verificar datos del vehículo
    
    // 1.1 Obtener ID de marca (debe existir en catálogo)
    $sql_marca = "SELECT id_marca FROM marca_vehiculo WHERE nombre_marca = '$marca'";
    $result_marca = mysqli_query($link, $sql_marca);
    if (!$result_marca || mysqli_num_rows($result_marca) == 0) {
        throw new Exception("Marca de vehículo no encontrada: $marca");
    }
    $row_marca = mysqli_fetch_assoc($result_marca);
    $id_marca = $row_marca['id_marca'];
    
    // 1.2 Obtener ID de color (debe existir en catálogo)
    $sql_color = "SELECT id_color FROM color_vehiculo WHERE descripcion = '$color'";
    $result_color = mysqli_query($link, $sql_color);
    if (!$result_color || mysqli_num_rows($result_color) == 0) {
        throw new Exception("Color de vehículo no encontrado: $color");
    }
    $row_color = mysqli_fetch_assoc($result_color);
    $id_color = $row_color['id_color'];
    
    // 1.3 Obtener ID de tipo servicio (debe existir en catálogo)
    $sql_servicio = "SELECT id_tipo_servicio FROM tipo_servicio WHERE descripcion = '$tipo_servicio'";
    $result_servicio = mysqli_query($link, $sql_servicio);
    if (!$result_servicio || mysqli_num_rows($result_servicio) == 0) {
        throw new Exception("Tipo de servicio no encontrado: $tipo_servicio");
    }
    $row_servicio = mysqli_fetch_assoc($result_servicio);
    $id_tipo_servicio = $row_servicio['id_tipo_servicio'];
    
    // 1.4 Verificar/insertar línea de vehículo si no existe para esa marca
    $sql_linea = "SELECT 1 FROM linea_vehiculo WHERE id_linea = '$linea' AND id_marca = $id_marca";
    $result_linea = mysqli_query($link, $sql_linea);
    if (!$result_linea || mysqli_num_rows($result_linea) == 0) {
        $sql_insert_linea = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES ('$linea', $id_marca)";
        if (!mysqli_query($link, $sql_insert_linea)) {
            throw new Exception("Error al insertar línea de vehículo: " . mysqli_error($link));
        }
    }
    
    // 1.5 Insertar vehículo
    // Estado del vehículo por defecto (Activo = 1)
    $id_estado_vehiculo = 1;
    
    $sql_vehiculo = "INSERT INTO vehiculo (placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo) 
                     VALUES ('$placa', '$linea', $modelo, $id_color, $id_marca, $id_tipo_servicio, $id_estado_vehiculo)";
    if (!mysqli_query($link, $sql_vehiculo)) {
        throw new Exception("Error al insertar vehículo: " . mysqli_error($link));
    }
    
    // PASO 2: Insertar conductor
    
    // Estado del conductor por defecto (Desconectado = 2)
    $id_estado_conductor = 2;
    
    $sql_conductor = "INSERT INTO conductor (id_estado_conductor, placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, codigo_postal, id_pais_nacionalidad, url_foto) 
                      VALUES ($id_estado_conductor, '$placa', '$identificacion', $id_tipo_identificacion, '$nombre', '$direccion', '$correo', $id_genero, '$codigo_postal', $id_pais_nacionalidad, '$url_foto')";
    if (!mysqli_query($link, $sql_conductor)) {
        throw new Exception("Error al insertar conductor: " . mysqli_error($link));
    }
    
    $id_conductor = mysqli_insert_id($link);
    
    // PASO 3: Insertar teléfonos
    foreach ($telefonos as $telefono) {
        $tel = mysqli_real_escape_string($link, $telefono);
        $sql_telefono = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ($id_conductor, '$tel')";
        if (!mysqli_query($link, $sql_telefono)) {
            throw new Exception("Error al insertar teléfono: " . mysqli_error($link));
        }
    }
    
    // PASO 4: Insertar respuestas de seguridad
    // Nota: El trigger en la BD crea automáticamente el usuario al insertar el conductor.
    // Debemos buscar ese usuario recién creado usando el correo.
    
    $sql_usuario = "SELECT id_usuario FROM usuario WHERE correo = '$correo'";
    $result_usuario = mysqli_query($link, $sql_usuario);
    if (!$result_usuario || mysqli_num_rows($result_usuario) == 0) {
        throw new Exception("No se encontró usuario para el conductor (Fallo en Trigger)");
    }
    $row_usuario = mysqli_fetch_assoc($result_usuario);
    $id_usuario = $row_usuario['id_usuario'];
    
    foreach ($preguntas_seguridad as $pregunta) {
        $id_pregunta = mysqli_real_escape_string($link, $pregunta['id_pregunta']);
        $respuesta = mysqli_real_escape_string($link, $pregunta['respuesta']);
        
        $sql_respuesta = "INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) 
                          VALUES ($id_pregunta, $id_usuario, '$respuesta')";
        if (!mysqli_query($link, $sql_respuesta)) {
            throw new Exception("Error al insertar respuesta de seguridad: " . mysqli_error($link));
        }
    }
    
    // Confirmar transacción si todo es correcto
    mysqli_commit($link);
    echo json_encode(array("success" => "1", "mensaje" => "Conductor registrado correctamente"));
    
} catch (Exception $e) {
    // Revertir transacción en caso de cualquier error
    mysqli_rollback($link);
    echo json_encode(array("success" => "0", "mensaje" => $e->getMessage()));
}

mysqli_close($link);
?>