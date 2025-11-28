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
        
        // Primero verificamos si existe el cliente
        $query_check = "SELECT COUNT(*) as count, correo FROM cliente WHERE id_cliente = '$id_cliente'";
        $result_check = mysqli_query($link, $query_check);
        
        if ($result_check) {
            $row = mysqli_fetch_assoc($result_check);
            
            if ($row['count'] == 0) {
                $response['success'] = "0";
                $response['mensaje'] = "El cliente no existe";
            } else {
                $correo = $row['correo'];
                
                // Iniciar transacción para asegurar consistencia
                mysqli_begin_transaction($link);
                
                try {
                    // PRIMERO: Eliminar el usuario asociado (si existe)
                    $query_delete_usuario = "DELETE FROM usuario WHERE correo = '$correo'";
                    $result_delete_usuario = mysqli_query($link, $query_delete_usuario);
                    
                    // No nos importa si no existía el usuario, continuamos
                    
                    // SEGUNDO: Eliminar teléfonos del cliente
                    $query_delete_telefonos = "DELETE FROM telefono_cliente WHERE id_cliente = '$id_cliente'";
                    $result_delete_telefonos = mysqli_query($link, $query_delete_telefonos);
                    
                    if (!$result_delete_telefonos) {
                        throw new Exception("Error al eliminar teléfonos: " . mysqli_error($link));
                    }
                    
                    // TERCERO: Eliminar el cliente
                    $query_delete = "DELETE FROM cliente WHERE id_cliente = '$id_cliente'";
                    $result_delete = mysqli_query($link, $query_delete);
                    
                    if (!$result_delete) {
                        throw new Exception("Error al eliminar cliente: " . mysqli_error($link));
                    }
                    
                    if (mysqli_affected_rows($link) > 0) {
                        // Confirmar transacción
                        mysqli_commit($link);
                        $response['success'] = "1";
                        $response['mensaje'] = "Cliente eliminado correctamente";
                    } else {
                        throw new Exception("No se pudo eliminar el cliente");
                    }
                    
                } catch (Exception $e) {
                    // Revertir transacción en caso de error
                    mysqli_rollback($link);
                    $response['success'] = "0";
                    $response['mensaje'] = $e->getMessage();
                }
            }
        } else {
            $response['success'] = "0";
            $response['mensaje'] = "Error en la consulta: " . mysqli_error($link);
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