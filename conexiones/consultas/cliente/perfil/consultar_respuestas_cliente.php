<?php
/**
 * Script para consultar las respuestas de seguridad de un cliente.
 * 
 * Recibe el correo del cliente.
 * Devuelve las preguntas seleccionadas y sus respuestas (para visualización o edición).
 * Se utiliza en la sección de perfil para gestionar la seguridad de la cuenta.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    $sql = "SELECT 
                u.id_usuario,
                ps.id_pregunta,
                ps.descripcion,
                rs.respuesta_pregunta
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
            
            while ($row = mysqli_fetch_assoc($result)) {
                $id_usuario = $row['id_usuario'];
                $preguntas[] = array(
                    'id_pregunta' => $row['id_pregunta'],
                    'descripcion' => $row['descripcion'],
                    'respuesta_pregunta' => $row['respuesta_pregunta']
                );
            }
            
            $res["success"] = "1";
            $res["mensaje"] = "Datos cargados con éxito";
            $res["id_usuario"] = $id_usuario;
            $res["preguntas"] = $preguntas;
        } else {
            $res["success"] = "0";
            $res["mensaje"] = "Correo no encontrado o sin preguntas de seguridad registradas.";
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