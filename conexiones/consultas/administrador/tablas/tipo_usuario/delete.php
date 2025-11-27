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
    
    if (isset($data['id_tipo_usuario'])) {
        $id_tipo_usuario = mysqli_real_escape_string($link, $data['id_tipo_usuario']);
        
        // Primero verificamos si existe y obtenemos la descripción
        $query_check = "SELECT COUNT(*) as count, descripcion FROM tipo_usuario WHERE id_tipo_usuario = '$id_tipo_usuario'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El tipo de usuario no existe";
            } else {
                // Protección: No permitir eliminar tipos base del sistema
                $descripcion = $row['descripcion'];
                $tipos_protegidos = array('Administrador', 'Conductor', 'Cliente');
                
                if (in_array($descripcion, $tipos_protegidos)) {
                    $response['success'] = "0";
                    $response['mensaje'] = "No se puede eliminar el tipo de usuario '$descripcion' porque es un tipo base del sistema";
                } else {
                    // Eliminar
                    $query_delete = "DELETE FROM tipo_usuario WHERE id_tipo_usuario = '$id_tipo_usuario'";
                    $result_delete = mysqli_query($link, $query_delete);
                    
                    if ($result_delete) {
                        if (mysqli_affected_rows($link) > 0) {
                            $response['success'] = "1";
                            $response['mensaje'] = "Tipo de usuario eliminado correctamente";
                        } else {
                            $response['success'] = "0";
                            $response['mensaje'] = "No se pudo eliminar el tipo de usuario";
                        }
                    } else {
                        $response['success'] = "0";
                        $response['mensaje'] = "Error al eliminar: " . mysqli_error($link);
                    }
                }
            }
        } else {
            $response['success'] = "0";
            $response['mensaje'] = "Error en la consulta: " . mysqli_error($link);
        }
        
    } else {
        $response['success'] = "0";
        $response['mensaje'] = "ID de tipo de usuario no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

