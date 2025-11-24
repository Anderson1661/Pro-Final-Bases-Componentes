<?php
/**
 * Script para recuperar las preguntas de seguridad de un usuario.
 * 
 * Se utiliza en el flujo de "Olvidé mi contraseña".
 * Recibe un correo electrónico y devuelve las 3 preguntas de seguridad asociadas.
 */

include('../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // Consulta SQL para obtener ID de usuario y sus preguntas de seguridad
    // Realiza JOINs entre usuario, respuestas_seguridad y preguntas_seguridad
    $sql = "SELECT 
                u.id_usuario,
                ps.id_pregunta,
                ps.descripcion
            FROM usuario u
            JOIN respuestas_seguridad rs ON u.id_usuario = rs.id_usuario
            JOIN preguntas_seguridad ps ON rs.id_pregunta = ps.id_pregunta
            WHERE u.correo = ?";

    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        
        if (mysqli_num_rows($result) > 0) {
            $preguntas = array();
            $id_usuario = -1;
            
            // Recorrer resultados para construir el array de preguntas
            while ($row = mysqli_fetch_assoc($result)) {
                $id_usuario = $row['id_usuario'];
                $preguntas[] = array(
                    'id_pregunta' => $row['id_pregunta'],
                    'descripcion' => $row['descripcion']
                );
            }
            
            // Validación: El usuario debe tener exactamente 3 preguntas configuradas
            if (count($preguntas) == 3) {
                $res["success"] = "1";
                $res["mensaje"] = "Usuario y preguntas encontradas";
                $res["id_usuario"] = $id_usuario;
                $res["preguntas"] = $preguntas;
            } else {
                $res["success"] = "0";
                $res["mensaje"] = "El usuario no tiene 3 preguntas de seguridad registradas.";
            }

        } else {
            // No se encontraron registros para ese correo
            $res["success"] = "0";
            $res["mensaje"] = "El correo no está asociado a una cuenta o no tiene respuestas de seguridad registradas.";
        }
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error en la consulta SQL.";
    }

    mysqli_stmt_close($stmt);

} 

echo json_encode($res);
mysqli_close($link);
?>