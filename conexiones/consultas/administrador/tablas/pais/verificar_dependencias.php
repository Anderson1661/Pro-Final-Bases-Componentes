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
    
    if (isset($data['id_pais'])) {
        $id_pais = mysqli_real_escape_string($link, $data['id_pais']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla codigo_postal
        $query_codigos = "SELECT COUNT(*) as count FROM codigo_postal WHERE id_pais = '$id_pais'";
        $result_codigos = mysqli_query($link, $query_codigos);
        if ($result_codigos) {
            $row = mysqli_fetch_assoc($result_codigos);
            $dependencies['codigos_postales'] = (int)$row['count'];
        } else {
            $dependencies['codigos_postales'] = 0;
        }
        
        // 2. Verificar en tabla cliente (nacionalidad)
        $query_clientes = "SELECT COUNT(*) as count FROM cliente WHERE id_pais_nacionalidad = '$id_pais'";
        $result_clientes = mysqli_query($link, $query_clientes);
        if ($result_clientes) {
            $row = mysqli_fetch_assoc($result_clientes);
            $dependencies['clientes'] = (int)$row['count'];
        } else {
            $dependencies['clientes'] = 0;
        }
        
        // 3. Verificar en tabla conductor (nacionalidad)
        $query_conductores = "SELECT COUNT(*) as count FROM conductor WHERE id_pais_nacionalidad = '$id_pais'";
        $result_conductores = mysqli_query($link, $query_conductores);
        if ($result_conductores) {
            $row = mysqli_fetch_assoc($result_conductores);
            $dependencies['conductores'] = (int)$row['count'];
        } else {
            $dependencies['conductores'] = 0;
        }
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el país porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de país no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

