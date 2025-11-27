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
    
    if (isset($data['id_linea']) && isset($data['id_marca'])) {
        $id_linea = mysqli_real_escape_string($link, $data['id_linea']);
        $id_marca = mysqli_real_escape_string($link, $data['id_marca']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla vehiculo (clave compuesta)
        $query_vehiculos = "SELECT COUNT(*) as count FROM vehiculo WHERE linea_vehiculo = '$id_linea' AND id_marca = '$id_marca'";
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
            $response['mensaje'] = "No se puede eliminar la línea de vehículo porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de línea y marca no proporcionados";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

