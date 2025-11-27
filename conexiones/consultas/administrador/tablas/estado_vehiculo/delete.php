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
    
    if (isset($data['id_estado_vehiculo'])) {
        $id_estado_vehiculo = mysqli_real_escape_string($link, $data['id_estado_vehiculo']);
        
        // Primero verificamos si existe el estado de vehículo
        $query_check = "SELECT COUNT(*) as count FROM estado_vehiculo WHERE id_estado_vehiculo = '$id_estado_vehiculo'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El estado de vehículo no existe";
            } else {
                // Verificar si es uno de los estados por defecto del sistema
                $query_default = "SELECT descripcion FROM estado_vehiculo WHERE id_estado_vehiculo = '$id_estado_vehiculo'";
                $result_default = mysqli_query($link, $query_default);
                $row_default = mysqli_fetch_assoc($result_default);
                
                $descripcion = $row_default['descripcion'];
                
                // Prevenir eliminación de estados por defecto del sistema
                if (in_array($descripcion, ['Activo', 'En Mantenimiento', 'Inactivo'])) {
                    $response['success'] = "0";
                    $response['mensaje'] = "No se puede eliminar el estado '$descripcion' porque es un estado del sistema";
                } else {
                    // Eliminar el estado de vehículo
                    $query_delete = "DELETE FROM estado_vehiculo WHERE id_estado_vehiculo = '$id_estado_vehiculo'";
                    $result_delete = mysqli_query($link, $query_delete);
                    
                    if ($result_delete) {
                        if (mysqli_affected_rows($link) > 0) {
                            $response['success'] = "1";
                            $response['mensaje'] = "Estado de vehículo eliminado correctamente";
                        } else {
                            $response['success'] = "0";
                            $response['mensaje'] = "No se pudo eliminar el estado de vehículo";
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
        $response['mensaje'] = "ID de estado de vehículo no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>