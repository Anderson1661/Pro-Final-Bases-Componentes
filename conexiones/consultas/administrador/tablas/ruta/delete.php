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
    
    if (isset($data['id_ruta'])) {
        $id_ruta = mysqli_real_escape_string($link, $data['id_ruta']);
        
        // Primero verificamos si existe
        $query_check = "SELECT COUNT(*) as count FROM ruta WHERE id_ruta = '$id_ruta'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "La ruta no existe";
            } else {
                // Eliminar primero los pasajeros asociados (cascada manual)
                $query_delete_pasajeros = "DELETE FROM pasajero_ruta WHERE id_ruta = '$id_ruta'";
                mysqli_query($link, $query_delete_pasajeros);
                
                // Eliminar la ruta
                $query_delete = "DELETE FROM ruta WHERE id_ruta = '$id_ruta'";
                $result_delete = mysqli_query($link, $query_delete);
                
                if ($result_delete) {
                    if (mysqli_affected_rows($link) > 0) {
                        $response['success'] = "1";
                        $response['mensaje'] = "Ruta eliminada correctamente";
                    } else {
                        $response['success'] = "0";
                        $response['mensaje'] = "No se pudo eliminar la ruta";
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
        $response['mensaje'] = "ID de ruta no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

