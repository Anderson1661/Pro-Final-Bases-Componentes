<?php
include('../../../config/conexion.php');

header('Content-Type: application/json; charset=utf-8');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

$response = array("success" => "0", "message" => "Error desconocido");

try {
    $link = Conectar();
    
    // Obtener los datos JSON del request
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        $response["message"] = "Datos JSON inválidos";
        echo json_encode($response);
        exit;
    }
    
    $correo = isset($input['correo']) ? trim($input['correo']) : '';
    $url_foto = isset($input['url_foto']) ? trim($input['url_foto']) : '';
    
    // Validar que los campos requeridos no estén vacíos
    if (empty($correo) || empty($url_foto)) {
        $response["message"] = "Correo y URL de foto son requeridos";
        echo json_encode($response);
        exit;
    }
    
    // Verificar si el conductor existe
    $check_sql = "SELECT id_conductor FROM conductor WHERE correo = ?";
    $check_stmt = mysqli_prepare($link, $check_sql);
    mysqli_stmt_bind_param($check_stmt, "s", $correo);
    mysqli_stmt_execute($check_stmt);
    $check_result = mysqli_stmt_get_result($check_stmt);
    
    if (mysqli_num_rows($check_result) === 0) {
        $response["message"] = "Conductor no encontrado";
        mysqli_stmt_close($check_stmt);
        mysqli_close($link);
        echo json_encode($response);
        exit;
    }
    mysqli_stmt_close($check_stmt);
    
    // Actualizar la URL de la foto
    $update_sql = "UPDATE conductor SET url_foto = ? WHERE correo = ?";
    $update_stmt = mysqli_prepare($link, $update_sql);
    mysqli_stmt_bind_param($update_stmt, "ss", $url_foto, $correo);
    
    if (mysqli_stmt_execute($update_stmt)) {
        if (mysqli_stmt_affected_rows($update_stmt) > 0) {
            $response["success"] = "1";
            $response["message"] = "Foto actualizada exitosamente";
            $response["correo"] = $correo;
            $response["url_foto"] = $url_foto;
        } else {
            $response["message"] = "No se realizaron cambios (posiblemente los datos son iguales)";
        }
    } else {
        $response["message"] = "Error al actualizar: " . mysqli_error($link);
    }
    
    mysqli_stmt_close($update_stmt);
    mysqli_close($link);
    
} catch (Exception $e) {
    $response["message"] = "Error de conexión: " . $e->getMessage();
}

echo json_encode($response);
?>