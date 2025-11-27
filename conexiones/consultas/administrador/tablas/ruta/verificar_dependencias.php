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
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla pasajero_ruta
        $query_pasajeros = "SELECT COUNT(*) as count FROM pasajero_ruta WHERE id_ruta = '$id_ruta'";
        $result_pasajeros = mysqli_query($link, $query_pasajeros);
        if ($result_pasajeros) {
            $row = mysqli_fetch_assoc($result_pasajeros);
            $dependencies['pasajeros_ruta'] = (int)$row['count'];
        } else {
            $dependencies['pasajeros_ruta'] = 0;
        }
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar la ruta porque tiene dependencias en otras tablas";
            $response['dependencies'] = $dependencies;
            $response['total_dependencies'] = $total_dependencies;
        } else {
            $response['success'] = "1";
            $response['mensaje'] = "No hay dependencias, se puede eliminar";
            $response['dependencies'] = $dependencies;
            $response['total_dependencies'] = 0;
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

