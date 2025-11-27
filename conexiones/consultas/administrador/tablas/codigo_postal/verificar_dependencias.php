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
    
    if (isset($data['id_codigo_postal'])) {
        $id_codigo_postal = mysqli_real_escape_string($link, $data['id_codigo_postal']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla cliente
        $query_clientes = "SELECT COUNT(*) as count FROM cliente WHERE codigo_postal = '$id_codigo_postal'";
        $result_clientes = mysqli_query($link, $query_clientes);
        if ($result_clientes) {
            $row = mysqli_fetch_assoc($result_clientes);
            $dependencies['clientes'] = (int)$row['count'];
        } else {
            $dependencies['clientes'] = 0;
        }
        
        // 2. Verificar en tabla administrador
        $query_administradores = "SELECT COUNT(*) as count FROM administrador WHERE codigo_postal = '$id_codigo_postal'";
        $result_administradores = mysqli_query($link, $query_administradores);
        if ($result_administradores) {
            $row = mysqli_fetch_assoc($result_administradores);
            $dependencies['administradores'] = (int)$row['count'];
        } else {
            $dependencies['administradores'] = 0;
        }
        
        // 3. Verificar en tabla conductor
        $query_conductores = "SELECT COUNT(*) as count FROM conductor WHERE codigo_postal = '$id_codigo_postal'";
        $result_conductores = mysqli_query($link, $query_conductores);
        if ($result_conductores) {
            $row = mysqli_fetch_assoc($result_conductores);
            $dependencies['conductores'] = (int)$row['count'];
        } else {
            $dependencies['conductores'] = 0;
        }
        
        // 4. Verificar en tabla ruta (como origen)
        $query_rutas_origen = "SELECT COUNT(*) as count FROM ruta WHERE id_codigo_postal_origen = '$id_codigo_postal'";
        $result_rutas_origen = mysqli_query($link, $query_rutas_origen);
        if ($result_rutas_origen) {
            $row = mysqli_fetch_assoc($result_rutas_origen);
            $dependencies['rutas_origen'] = (int)$row['count'];
        } else {
            $dependencies['rutas_origen'] = 0;
        }
        
        // 5. Verificar en tabla ruta (como destino)
        $query_rutas_destino = "SELECT COUNT(*) as count FROM ruta WHERE id_codigo_postal_destino = '$id_codigo_postal'";
        $result_rutas_destino = mysqli_query($link, $query_rutas_destino);
        if ($result_rutas_destino) {
            $row = mysqli_fetch_assoc($result_rutas_destino);
            $dependencies['rutas_destino'] = (int)$row['count'];
        } else {
            $dependencies['rutas_destino'] = 0;
        }
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el código postal porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de código postal no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>