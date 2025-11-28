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
    
    if (isset($data['id_cliente'])) {
        $id_cliente = mysqli_real_escape_string($link, $data['id_cliente']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla telefono_cliente (BLOQUEANTE)
        $query_telefonos = "SELECT COUNT(*) as count FROM telefono_cliente WHERE id_cliente = '$id_cliente'";
        $result_telefonos = mysqli_query($link, $query_telefonos);
        if ($result_telefonos) {
            $row = mysqli_fetch_assoc($result_telefonos);
            $dependencies['telefonos'] = (int)$row['count'];
        } else {
            $dependencies['telefonos'] = 0;
        }
        
        // 2. Verificar en tabla ruta (BLOQUEANTE)
        $query_rutas = "SELECT COUNT(*) as count FROM ruta WHERE id_cliente = '$id_cliente'";
        $result_rutas = mysqli_query($link, $query_rutas);
        if ($result_rutas) {
            $row = mysqli_fetch_assoc($result_rutas);
            $dependencies['rutas'] = (int)$row['count'];
        } else {
            $dependencies['rutas'] = 0;
        }
        
        // 3. Verificar en tabla usuario (NO BLOQUEANTE, solo informativo)
        $query_correo = "SELECT correo FROM cliente WHERE id_cliente = '$id_cliente'";
        $result_correo = mysqli_query($link, $query_correo);
        if ($result_correo && mysqli_num_rows($result_correo) > 0) {
            $row_correo = mysqli_fetch_assoc($result_correo);
            $correo = $row_correo['correo'];
            
            $query_usuario = "SELECT COUNT(*) as count FROM usuario WHERE correo = '$correo'";
            $result_usuario = mysqli_query($link, $query_usuario);
            if ($result_usuario) {
                $row = mysqli_fetch_assoc($result_usuario);
                $dependencies['usuarios'] = (int)$row['count'];
            } else {
                $dependencies['usuarios'] = 0;
            }
        } else {
            $dependencies['usuarios'] = 0;
        }
        
        // Calcular total de dependencias BLOQUEANTES (teléfonos y rutas)
        $dependencias_bloqueantes = $dependencies['telefonos'] + $dependencies['rutas'];
        
        if ($dependencias_bloqueantes > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el cliente porque tiene dependencias en otras tablas";
            $response['dependencies'] = $dependencies;
            $response['dependencias_bloqueantes'] = $dependencias_bloqueantes;
        } else {
            $response['success'] = "1";
            $response['mensaje'] = "No hay dependencias bloqueantes, se puede eliminar";
            $response['dependencies'] = $dependencies;
            $response['dependencias_bloqueantes'] = 0;
        }
        
    } else {
        $response['success'] = "0";
        $response['mensaje'] = "ID de cliente no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>