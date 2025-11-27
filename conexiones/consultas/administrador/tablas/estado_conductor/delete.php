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
    
    if (isset($data['id_estado_conductor'])) {
        $id_estado_conductor = mysqli_real_escape_string($link, $data['id_estado_conductor']);
        
        // Primero verificamos si existe el estado de conductor
        $query_check = "SELECT COUNT(*) as count FROM estado_conductor WHERE id_estado_conductor = '$id_estado_conductor'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El estado de conductor no existe";
            } else {
                // Verificar si es uno de los estados por defecto (Conectado/Desconectado)
                $query_default = "SELECT descripcion FROM estado_conductor WHERE id_estado_conductor = '$id_estado_conductor'";
                $result_default = mysqli_query($link, $query_default);
                $row_default = mysqli_fetch_assoc($result_default);
                
                $descripcion = $row_default['descripcion'];
                
                // Prevenir eliminación de estados por defecto del sistema
                if (in_array($descripcion, ['Conectado', 'Desconectado'])) {
                    $response['success'] = "0";
                    $response['mensaje'] = "No se puede eliminar el estado '$descripcion' porque es un estado del sistema";
                } else {
                    // Eliminar el estado de conductor
                    $query_delete = "DELETE FROM estado_conductor WHERE id_estado_conductor = '$id_estado_conductor'";
                    $result_delete = mysqli_query($link, $query_delete);
                    
                    if ($result_delete) {
                        if (mysqli_affected_rows($link) > 0) {
                            $response['success'] = "1";
                            $response['mensaje'] = "Estado de conductor eliminado correctamente";
                        } else {
                            $response['success'] = "0";
                            $response['mensaje'] = "No se pudo eliminar el estado de conductor";
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
        $response['mensaje'] = "ID de estado de conductor no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>