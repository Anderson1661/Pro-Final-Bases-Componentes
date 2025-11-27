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
    
    if (isset($data['id_marca'])) {
        $id_marca = mysqli_real_escape_string($link, $data['id_marca']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla linea_vehiculo
        $query_lineas = "SELECT COUNT(*) as count FROM linea_vehiculo WHERE id_marca = '$id_marca'";
        $result_lineas = mysqli_query($link, $query_lineas);
        if ($result_lineas) {
            $row = mysqli_fetch_assoc($result_lineas);
            $dependencies['lineas_vehiculo'] = (int)$row['count'];
        } else {
            $dependencies['lineas_vehiculo'] = 0;
        }
        
        // 2. Verificar en tabla vehiculo
        $query_vehiculos = "SELECT COUNT(*) as count FROM vehiculo WHERE id_marca = '$id_marca'";
        $result_vehiculos = mysqli_query($link, $query_vehiculos);
        if ($result_vehiculos) {
            $row = mysqli_fetch_assoc($result_vehiculos);
            $dependencies['vehiculos'] = (int)$row['count'];
        } else {
            $dependencies['vehiculos'] = 0;
        }
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar la marca de vehículo porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de marca no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

