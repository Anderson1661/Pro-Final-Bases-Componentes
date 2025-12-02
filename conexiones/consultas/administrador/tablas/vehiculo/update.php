<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$placa_original = isset($input['placa_original']) ? trim($input['placa_original']) : '';
$placa_nueva = isset($input['placa_nueva']) ? trim($input['placa_nueva']) : '';
$linea_vehiculo = isset($input['linea_vehiculo']) ? trim($input['linea_vehiculo']) : '';
$modelo = isset($input['modelo']) ? trim($input['modelo']) : '';
$id_color = isset($input['id_color']) ? trim($input['id_color']) : '';
$id_marca = isset($input['id_marca']) ? trim($input['id_marca']) : '';
$id_tipo_servicio = isset($input['id_tipo_servicio']) ? trim($input['id_tipo_servicio']) : '';
$id_estado_vehiculo = isset($input['id_estado_vehiculo']) ? trim($input['id_estado_vehiculo']) : '';

// 1. Validaciones de campos requeridos
if (empty($placa_original)) {
    echo json_encode(array("success" => "0", "mensaje" => "Placa original requerida para la actualización"));
    mysqli_close($link); exit;
} else if (empty($placa_nueva) || empty($linea_vehiculo) || empty($modelo) || empty($id_color) || 
           empty($id_marca) || empty($id_tipo_servicio) || empty($id_estado_vehiculo)) {
    echo json_encode(array("success" => "0", "mensaje" => "Todos los campos del vehículo son requeridos"));
    mysqli_close($link); exit;
}

// Escapar variables
$placa_original = mysqli_real_escape_string($link, $placa_original);
$placa_nueva = mysqli_real_escape_string($link, $placa_nueva);
$linea_vehiculo = mysqli_real_escape_string($link, $linea_vehiculo);
$modelo = mysqli_real_escape_string($link, $modelo);
$id_color = mysqli_real_escape_string($link, $id_color);
$id_marca = mysqli_real_escape_string($link, $id_marca);
$id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
$id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);

// 2. Verificar unicidad de la nueva placa si cambió
if (strtoupper($placa_original) !== strtoupper($placa_nueva)) {
    $check_placa = "SELECT placa FROM vehiculo WHERE placa = '$placa_nueva'";
    if (mysqli_num_rows(mysqli_query($link, $check_placa)) > 0) {
        echo json_encode(array("success" => "0", "mensaje" => "La nueva placa ya está registrada en otro vehículo"));
        mysqli_close($link); exit;
    }
}

// 3. Verificar todas las claves foráneas (FKs)
$fks_ok = true;
$mensaje_fk = "";

$checks = array(
    "color_vehiculo" => "SELECT id_color FROM color_vehiculo WHERE id_color = '$id_color'",
    "linea_vehiculo_compuesta" => "SELECT id_linea FROM linea_vehiculo WHERE id_linea = '$linea_vehiculo' AND id_marca = '$id_marca'",
    "tipo_servicio" => "SELECT id_tipo_servicio FROM tipo_servicio WHERE id_tipo_servicio = '$id_tipo_servicio'",
    "estado_vehiculo" => "SELECT id_estado_vehiculo FROM estado_vehiculo WHERE id_estado_vehiculo = '$id_estado_vehiculo'"
);

foreach ($checks as $tabla => $sql_check) {
    $res_check = mysqli_query($link, $sql_check);
    if (!$res_check || mysqli_num_rows($res_check) == 0) {
        $fks_ok = false;
        $mensaje_fk = "Error de Clave Foránea: el ID de $tabla no es válido o no existe.";
        break;
    }
}

if (!$fks_ok) {
    echo json_encode(array("success" => "0", "mensaje" => $mensaje_fk));
    mysqli_close($link); exit;
}

// 4. Verificar restricción CHECK (modelo >= 2010)
if ((int)$modelo < 2010) {
    echo json_encode(array("success" => "0", "mensaje" => "El año del modelo debe ser 2010 o posterior."));
    mysqli_close($link); exit;
}

// 5. Realizar la actualización
// El Trigger 'update_placa_conductor' se encargará de actualizar la placa en la tabla 'conductor' si cambia.
$sql = "UPDATE vehiculo SET 
        placa='$placa_nueva', 
        linea_vehiculo='$linea_vehiculo', 
        modelo='$modelo', 
        id_color='$id_color', 
        id_marca='$id_marca', 
        id_tipo_servicio='$id_tipo_servicio', 
        id_estado_vehiculo='$id_estado_vehiculo'
        WHERE placa='$placa_original'";

$res = mysqli_query($link, $sql);

if ($res) {
    if (mysqli_affected_rows($link) > 0) {
        echo json_encode(array("success" => "1", "mensaje" => "Vehículo actualizado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios o la placa original no existe"));
    }
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
}

mysqli_close($link);
?>