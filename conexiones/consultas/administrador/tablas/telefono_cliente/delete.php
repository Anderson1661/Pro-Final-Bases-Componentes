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
    
    if (isset($data['id_cliente']) && isset($data['telefono'])) {
        $id_cliente = mysqli_real_escape_string($link, $data['id_cliente']);
        $telefono = mysqli_real_escape_string($link, $data['telefono']);
        
        // Primero verificamos si existe
        $query_check = "SELECT COUNT(*) as count FROM telefono_cliente WHERE id_cliente = '$id_cliente' AND telefono = '$telefono'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El teléfono no existe";
            } else {
                // Eliminar
                $query_delete = "DELETE FROM telefono_cliente WHERE id_cliente = '$id_cliente' AND telefono = '$telefono'";
                $result_delete = mysqli_query($link, $query_delete);
                
                if ($result_delete) {
                    if (mysqli_affected_rows($link) > 0) {
                        $response['success'] = "1";
                        $response['mensaje'] = "Teléfono eliminado correctamente";
                    } else {
                        $response['success'] = "0";
                        $response['mensaje'] = "No se pudo eliminar el teléfono";
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
        $response['mensaje'] = "ID de cliente y teléfono no proporcionados";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

