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
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // Esta tabla no tiene dependencias directas, es una tabla de relación
        // Se puede eliminar directamente sin verificar dependencias
        $dependencies['ninguna'] = 0;
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el teléfono porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de cliente y teléfono no proporcionados";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

