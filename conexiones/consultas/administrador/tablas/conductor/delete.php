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
    
    if (isset($data['id_conductor'])) {
        $id_conductor = mysqli_real_escape_string($link, $data['id_conductor']);
        
        // Primero verificamos si existe el conductor
        $query_check = "SELECT COUNT(*) as count, correo FROM conductor WHERE id_conductor = '$id_conductor'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El conductor no existe";
            } else {
                $correo = $row['correo'];
                
                // Eliminar teléfonos del conductor primero (por restricciones de clave foránea)
                $query_delete_telefonos = "DELETE FROM telefono_conductor WHERE id_conductor = '$id_conductor'";
                $result_delete_telefonos = mysqli_query($link, $query_delete_telefonos);
                
                // Eliminar el conductor
                $query_delete = "DELETE FROM conductor WHERE id_conductor = '$id_conductor'";
                $result_delete = mysqli_query($link, $query_delete);
                
                if ($result_delete) {
                    if (mysqli_affected_rows($link) > 0) {
                        // Si se eliminó el conductor, también eliminamos el usuario asociado
                        $query_delete_usuario = "DELETE FROM usuario WHERE correo = '$correo'";
                        $result_delete_usuario = mysqli_query($link, $query_delete_usuario);
                        
                        $response['success'] = "1";
                        $response['mensaje'] = "Conductor eliminado correctamente";
                    } else {
                        $response['success'] = "0";
                        $response['mensaje'] = "No se pudo eliminar el conductor";
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
        $response['mensaje'] = "ID de conductor no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>