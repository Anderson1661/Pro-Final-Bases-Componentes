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
    
    if (isset($data['id_tipo_usuario'])) {
        $id_tipo_usuario = mysqli_real_escape_string($link, $data['id_tipo_usuario']);
        
        // Array para almacenar los resultados de las verificaciones
        $dependencies = array();
        
        // 1. Verificar en tabla usuario
        $query_usuarios = "SELECT COUNT(*) as count FROM usuario WHERE id_tipo_usuario = '$id_tipo_usuario'";
        $result_usuarios = mysqli_query($link, $query_usuarios);
        if ($result_usuarios) {
            $row = mysqli_fetch_assoc($result_usuarios);
            $dependencies['usuarios'] = (int)$row['count'];
        } else {
            $dependencies['usuarios'] = 0;
        }
        
        // Calcular total de dependencias
        $total_dependencies = array_sum($dependencies);
        
        // Protección especial: No permitir eliminar tipos de usuario base del sistema
        $query_descripcion = "SELECT descripcion FROM tipo_usuario WHERE id_tipo_usuario = '$id_tipo_usuario'";
        $result_descripcion = mysqli_query($link, $query_descripcion);
        $descripcion = "";
        if ($result_descripcion && mysqli_num_rows($result_descripcion) > 0) {
            $row_desc = mysqli_fetch_assoc($result_descripcion);
            $descripcion = $row_desc['descripcion'];
        }
        
        $tipos_protegidos = array('Administrador', 'Conductor', 'Cliente');
        if (in_array($descripcion, $tipos_protegidos)) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el tipo de usuario '$descripcion' porque es un tipo base del sistema";
            $response['dependencies'] = $dependencies;
            $response['total_dependencies'] = $total_dependencies;
        } else if ($total_dependencies > 0) {
            $response['success'] = "0";
            $response['mensaje'] = "No se puede eliminar el tipo de usuario porque tiene dependencias en otras tablas";
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
        $response['mensaje'] = "ID de tipo de usuario no proporcionado";
    }
} else {
    $response['success'] = "0";
    $response['mensaje'] = "Método no permitido";
}

echo json_encode($response);
mysqli_close($link);
?>

