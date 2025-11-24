<?php
/**
 * Script para cambiar la contraseña de un usuario.
 * 
 * Recibe el ID del usuario y la nueva contraseña para actualizarla en la base de datos.
 * Nota: Según la solicitud, la contraseña se almacena SIN hashing (texto plano).
 */

include('../config/conexion.php');
$link = Conectar();

// Configurar cabecera para respuesta JSON
header('Content-Type: application/json; charset=utf-8');

// Respuesta por defecto
$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

// Validar que se reciban los parámetros necesarios
if (isset($_POST['id_usuario'], $_POST['contrasenia'])) {
    
    $id_usuario = intval($_POST['id_usuario']);
    $contrasenia = $_POST['contrasenia']; // ADVERTENCIA: Almacenamiento en texto plano
    
    // Preparar la consulta de actualización
    $sql = "UPDATE usuario SET contrasenia = ? WHERE id_usuario = ?";

    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "si", $contrasenia, $id_usuario);
    
    // Ejecutar la consulta
    if (mysqli_stmt_execute($stmt)) {
        // Éxito: La contraseña se actualizó (o era la misma)
        $res["success"] = "1";
        $res["mensaje"] = "Contraseña actualizada con éxito. Por favor inicie sesión.";
    } else {
        // Error en la ejecución SQL
        $res["success"] = "0";
        $res["mensaje"] = "Error al ejecutar la consulta de actualización.";
    }

    mysqli_stmt_close($stmt);

} 

// Devolver respuesta en formato JSON
echo json_encode($res);
mysqli_close($link);
?>