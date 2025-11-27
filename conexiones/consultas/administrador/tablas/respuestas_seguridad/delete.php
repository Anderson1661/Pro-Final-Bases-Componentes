<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

include('../../../../config/conexion.php');
$link = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    
    if (isset($data['id_pregunta']) && isset($data['id_usuario'])) {
        $id_pregunta = mysqli_real_escape_string($link, $data['id_pregunta']);
        $id_usuario = mysqli_real_escape_string($link, $data['id_usuario']);
        
        // Primero verificamos si existe
        $query_check = "SELECT COUNT(*) as count FROM respuestas_seguridad WHERE id_pregunta = '$id_pregunta' AND id_usuario = '$id_usuario'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "La respuesta de seguridad no existe";
            } else {
                // Eliminar
                $query_delete = "DELETE FROM respuestas_seguridad WHERE id_pregunta = '$id_pregunta' AND id_usuario = '$id_usuario'";
                $result_delete = mysqli_query($link, $query_delete);
                
                if ($result_delete) {
                    if (mysqli_affected_rows($link) > 0) {
                        $response['success'] = "1";
                        $response['mensaje'] = "Respuesta de seguridad eliminada correctamente";
                    } else {
                        $response['success'] = "0";
                        $response['mensaje'] = "No se pudo eliminar la respuesta de seguridad";
                    }
                } else {
                    $response['success'] = "0";
                    $response['mensaje'] = "Error al eliminar: " . mysqli_error($link);
                }
            }
        } else {
            $response['success'] = "0";
            $response['mensaje'] = "Error en la consulta: " . mysqli_error($link);
        }
        
    } else {
        $response['success'] = "0";
        $response['mensaje'] = "ID de pregunta e ID de usuario no proporcionados";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

