<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_usuario = isset($input['id_usuario']) ? trim($input['id_usuario']) : '';
$id_tipo_usuario = isset($input['id_tipo_usuario']) ? trim($input['id_tipo_usuario']) : '';
$correo = isset($input['correo']) ? trim($input['correo']) : '';
$contrasenia = isset($input['contrasenia']) ? $input['contrasenia'] : ''; // Opcional

// 1. Validaciones de campos requeridos
if (empty($id_usuario)) {
    echo json_encode(array("success" => "0", "mensaje" => "ID de Usuario requerido"));
    mysqli_close($link);
    exit;
} else if (empty($id_tipo_usuario) || empty($correo)) {
    echo json_encode(array("success" => "0", "mensaje" => "El Tipo de Usuario y el Correo son requeridos"));
    mysqli_close($link);
    exit;
}

// Escapar para prevenir inyección SQL
$id_usuario = mysqli_real_escape_string($link, $id_usuario);
$id_tipo_usuario = mysqli_real_escape_string($link, $id_tipo_usuario);
$correo = mysqli_real_escape_string($link, $correo);

// 2. Verificar existencia de la clave foránea (FK)
$check_tipo_usuario = "SELECT id_tipo_usuario FROM tipo_usuario WHERE id_tipo_usuario = '$id_tipo_usuario'";
if (mysqli_num_rows(mysqli_query($link, $check_tipo_usuario)) == 0) {
    echo json_encode(array("success" => "0", "mensaje" => "El Tipo de Usuario (ID) no existe"));
    mysqli_close($link);
    exit;
}

// 3. Verificar unicidad del correo (excluyendo el usuario actual)
$check_correo = "SELECT id_usuario FROM usuario 
                 WHERE correo = '$correo' AND id_usuario != '$id_usuario'";
$check_res_correo = mysqli_query($link, $check_correo);

if (mysqli_num_rows($check_res_correo) > 0) {
    echo json_encode(array("success" => "0", "mensaje" => "Ya existe otro usuario con ese correo electrónico"));
    mysqli_close($link);
    exit;
}

// 4. Construir la consulta de actualización
$sql_base = "UPDATE usuario SET 
             id_tipo_usuario='$id_tipo_usuario', 
             correo='$correo'
             WHERE id_usuario='$id_usuario'";

if (!empty($contrasenia)) {
    $contrasenia_safe = mysqli_real_escape_string($link, $contrasenia);
    // NOTA IMPORTANTE: Si la contraseña se almacena hasheada, debes hashearla aquí antes de insertarla.
    // Ejemplo: $contrasenia_hashed = password_hash($contrasenia_safe, PASSWORD_DEFAULT);
    // Para el propósito del ejercicio, se asume que se usa la variable $contrasenia_safe (plano o ya hasheada por el cliente).
    $sql = "UPDATE usuario SET 
            id_tipo_usuario='$id_tipo_usuario', 
            correo='$correo', 
            contrasenia='$contrasenia_safe' 
            WHERE id_usuario='$id_usuario'";
} else {
    $sql = $sql_base;
}

$res = mysqli_query($link, $sql);

if ($res) {
    if (mysqli_affected_rows($link) > 0) {
        echo json_encode(array("success" => "1", "mensaje" => "Usuario actualizado correctamente"));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se realizaron cambios o el ID no existe"));
    }
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
}

mysqli_close($link);
?>