<?php
include('../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_usuario'], $_POST['contrasenia'])) {
    
    $id_usuario = intval($_POST['id_usuario']);
    $contrasenia = $_POST['contrasenia']; // SIN HASHING, como se solicitó
    
    // Consulta de actualización
    $sql = "UPDATE usuario SET contrasenia = ? WHERE id_usuario = ?";

    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "si", $contrasenia, $id_usuario);
    
    if (mysqli_stmt_execute($stmt)) {
        // Se considera éxito si la consulta se ejecutó sin errores, 
        // incluso si affected_rows es 0 (contraseña ya era la misma).
        $res["success"] = "1";
        $res["mensaje"] = "Contraseña actualizada con éxito. Por favor inicie sesión.";
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error al ejecutar la consulta de actualización.";
    }

    mysqli_stmt_close($stmt);

} 

echo json_encode($res);
mysqli_close($link);
?>