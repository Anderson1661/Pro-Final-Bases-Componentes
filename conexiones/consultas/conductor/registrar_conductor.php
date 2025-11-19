<?php
include('../../config/conexion.php');
$link = Conectar();

// --- Datos principales del conductor ---
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

// --- Vehículo ---
$placa = isset($_REQUEST['placa']) ? $_REQUEST['placa'] : '';
$marca = isset($_REQUEST['marca']) ? $_REQUEST['marca'] : '';
$linea = isset($_REQUEST['linea']) ? $_REQUEST['linea'] : '';
$modelo = isset($_REQUEST['modelo']) ? $_REQUEST['modelo'] : '';
$color = isset($_REQUEST['color']) ? $_REQUEST['color'] : '';
$tipo_servicio = isset($_REQUEST['tipo_servicio']) ? $_REQUEST['tipo_servicio'] : '';

// --- Validación de campos requeridos ---
if (empty($identificacion) || empty($id_tipo_identificacion) || empty($nombre) || 
    empty($direccion) || empty($correo) || empty($id_genero) || 
    empty($id_pais_nacionalidad) || empty($codigo_postal) || empty($tel1)) {
    
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos (excepto Teléfono 2) son requeridos"));
    mysqli_close($link);
    exit;
}

// Validar vehículo: todos los campos de vehículo deben estar presentes
if (empty($placa) || empty($marca) || empty($linea) || empty($modelo) || empty($color) || empty($tipo_servicio)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos de vehículo son requeridos"));
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
$placa = mysqli_real_escape_string($link, $placa);
$marca = mysqli_real_escape_string($link, $marca);
$linea = mysqli_real_escape_string($link, $linea);
$modelo = mysqli_real_escape_string($link, $modelo);
$color = mysqli_real_escape_string($link, $color);
$tipo_servicio = mysqli_real_escape_string($link, $tipo_servicio);

// --- Iniciar Transacción ---
mysqli_begin_transaction($link);

try {
        // --- PASO A: Registrar / asegurar la marca, color, tipo_servicio y linea_vehiculo ---
        // Marca: obtener id_marca o crear
        $id_marca = null;
        $sql_marca = "SELECT id_marca FROM marca_vehiculo WHERE nombre_marca = ? LIMIT 1";
        $stmt = mysqli_prepare($link, $sql_marca);
        if ($stmt) {
            mysqli_stmt_bind_param($stmt, "s", $marca);
            mysqli_stmt_execute($stmt);
            $res = mysqli_stmt_get_result($stmt);
            if ($row = mysqli_fetch_assoc($res)) {
                $id_marca = $row['id_marca'];
            }
            mysqli_stmt_close($stmt);
        }

        if ($id_marca === null) {
            $sql_ins_marca = "INSERT INTO marca_vehiculo (nombre_marca) VALUES (?)";
            $stmt_ins_marca = mysqli_prepare($link, $sql_ins_marca);
            if ($stmt_ins_marca) {
                mysqli_stmt_bind_param($stmt_ins_marca, "s", $marca);
                if (!mysqli_stmt_execute($stmt_ins_marca)) {
                    throw new Exception("Error al insertar marca: " . mysqli_stmt_error($stmt_ins_marca));
                }
                $id_marca = mysqli_insert_id($link);
                mysqli_stmt_close($stmt_ins_marca);
            } else {
                throw new Exception("Error al preparar inserción de marca: " . mysqli_error($link));
            }
        }

        // Color: obtener id_color (si no existe, insertar)
        $id_color = null;
        $sql_color = "SELECT id_color FROM color_vehiculo WHERE descripcion = ? LIMIT 1";
        $stmt = mysqli_prepare($link, $sql_color);
        if ($stmt) {
            mysqli_stmt_bind_param($stmt, "s", $color);
            mysqli_stmt_execute($stmt);
            $res = mysqli_stmt_get_result($stmt);
            if ($row = mysqli_fetch_assoc($res)) {
                $id_color = $row['id_color'];
            }
            mysqli_stmt_close($stmt);
        }
        if ($id_color === null) {
            $sql_ins_color = "INSERT INTO color_vehiculo (descripcion) VALUES (?)";
            $stmt_ins_color = mysqli_prepare($link, $sql_ins_color);
            if ($stmt_ins_color) {
                mysqli_stmt_bind_param($stmt_ins_color, "s", $color);
                if (!mysqli_stmt_execute($stmt_ins_color)) {
                    throw new Exception("Error al insertar color: " . mysqli_stmt_error($stmt_ins_color));
                }
                $id_color = mysqli_insert_id($link);
                mysqli_stmt_close($stmt_ins_color);
            } else {
                throw new Exception("Error al preparar inserción de color: " . mysqli_error($link));
            }
        }

        // Tipo de servicio: obtener id_tipo_servicio
        $id_tipo_serv = null;
        $sql_tipo = "SELECT id_tipo_servicio FROM tipo_servicio WHERE descripcion = ? LIMIT 1";
        $stmt = mysqli_prepare($link, $sql_tipo);
        if ($stmt) {
            mysqli_stmt_bind_param($stmt, "s", $tipo_servicio);
            mysqli_stmt_execute($stmt);
            $res = mysqli_stmt_get_result($stmt);
            if ($row = mysqli_fetch_assoc($res)) {
                $id_tipo_serv = $row['id_tipo_servicio'];
            }
            mysqli_stmt_close($stmt);
        }
        if ($id_tipo_serv === null) {
            throw new Exception("Tipo de servicio no encontrado: $tipo_servicio");
        }

        // Linea: insertar si no existe (linea_vehiculo tiene PK (id_linea, id_marca))
        $sql_linea_check = "SELECT id_linea FROM linea_vehiculo WHERE id_linea = ? AND id_marca = ? LIMIT 1";
        $stmt_linea = mysqli_prepare($link, $sql_linea_check);
        if ($stmt_linea) {
            mysqli_stmt_bind_param($stmt_linea, "si", $linea, $id_marca);
            mysqli_stmt_execute($stmt_linea);
            $res_linea = mysqli_stmt_get_result($stmt_linea);
            $exists_linea = (mysqli_num_rows($res_linea) > 0);
            mysqli_stmt_close($stmt_linea);
        } else {
            throw new Exception("Error al preparar consulta linea: " . mysqli_error($link));
        }

        if (!$exists_linea) {
            $sql_ins_linea = "INSERT INTO linea_vehiculo (id_linea, id_marca) VALUES (?, ?)";
            $stmt_ins_linea = mysqli_prepare($link, $sql_ins_linea);
            if ($stmt_ins_linea) {
                mysqli_stmt_bind_param($stmt_ins_linea, "si", $linea, $id_marca);
                if (!mysqli_stmt_execute($stmt_ins_linea)) {
                    throw new Exception("Error al insertar linea de vehiculo: " . mysqli_stmt_error($stmt_ins_linea));
                }
                mysqli_stmt_close($stmt_ins_linea);
            } else {
                throw new Exception("Error al preparar inserción de linea: " . mysqli_error($link));
            }
        }

        // --- PASO B: Insertar el vehiculo ---
        $sql_veh = "INSERT INTO vehiculo (placa, linea_vehiculo, modelo, id_color, id_marca, id_tipo_servicio, id_estado_vehiculo) VALUES (?, ?, ?, ?, ?, ?, 1)";
        $stmt_veh = mysqli_prepare($link, $sql_veh);
        if ($stmt_veh === false) {
            throw new Exception("Error al preparar inserción de vehiculo: " . mysqli_error($link));
        }
        mysqli_stmt_bind_param($stmt_veh, "ssiiii", $placa, $linea, $modelo, $id_color, $id_marca, $id_tipo_serv);
        if (!mysqli_stmt_execute($stmt_veh)) {
            throw new Exception("Error al insertar vehiculo: " . mysqli_stmt_error($stmt_veh));
        }
        mysqli_stmt_close($stmt_veh);

    // Paso 1: Insertar en la tabla principal 'conductor'
    // Asignamos un estado inicial por defecto para el conductor: 'Desconectado' (id_estado_conductor = 2)
    // Insertamos referencia a la placa del vehiculo en 'placa_vehiculo'
    $sql_conductor = "INSERT INTO conductor (placa_vehiculo, identificacion, id_tipo_identificacion, nombre, direccion, correo, id_genero, id_pais_nacionalidad, codigo_postal, id_estado_conductor) 
                    VALUES ('$placa', '$identificacion', '$id_tipo_identificacion', '$nombre', '$direccion', '$correo', '$id_genero', '$id_pais_nacionalidad', '$codigo_postal', '2')";
    
    if (!mysqli_query($link, $sql_conductor)) {
        throw new Exception("Error al registrar datos principales: " . mysqli_error($link));
    }
    
    // Paso 2: Obtener el ID del conductor que acabamos de insertar
    $id_conductor_nuevo = mysqli_insert_id($link);
    
    // Paso 3: Insertar el Teléfono 1 (obligatorio)
    $sql_tel1 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ('$id_conductor_nuevo', '$tel1')";
    if (!mysqli_query($link, $sql_tel1)) {
        throw new Exception("Error al registrar teléfono 1: " . mysqli_error($link));
    }

    // Paso 4: Insertar el Teléfono 2 (solo si no está vacío)
    if (!empty($tel2)) {
        $sql_tel2 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ('$id_conductor_nuevo', '$tel2')";
        if (!mysqli_query($link, $sql_tel2)) {
            throw new Exception("Error al registrar teléfono 2: " . mysqli_error($link));
        }
    }
    
    // Si todo salió bien, confirmamos los cambios
    mysqli_commit($link);
    echo json_encode(array("success" => "1", "mensaje" => "Conductor registrado correctamente"));

} catch (Exception $e) {
    // Si algo falló, revertimos todos los cambios
    mysqli_rollback($link);
    echo json_encode(array("success" => "0", "mensaje" => $e->getMessage()));
}

mysqli_close($link);
?>
