<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $placa = isset($_REQUEST['placa']) ? $_REQUEST['placa'] : '';
    $linea_vehiculo = isset($_REQUEST['linea_vehiculo']) ? $_REQUEST['linea_vehiculo'] : '';
    $modelo = isset($_REQUEST['modelo']) ? $_REQUEST['modelo'] : '';
    $id_color = isset($_REQUEST['id_color']) ? $_REQUEST['id_color'] : '';
    $id_marca = isset($_REQUEST['id_marca']) ? $_REQUEST['id_marca'] : '';
    $id_tipo_servicio = isset($_REQUEST['id_tipo_servicio']) ? $_REQUEST['id_tipo_servicio'] : '';
    $id_estado_vehiculo = isset($_REQUEST['id_estado_vehiculo']) ? $_REQUEST['id_estado_vehiculo'] : '';
    
    if (empty($placa)) {
        echo json_encode(array("success" => "0", "mensaje" => "La placa es requerida"));
    } else if (empty($linea_vehiculo) || empty($modelo) || empty($id_color) || 
               empty($id_marca) || empty($id_tipo_servicio) || empty($id_estado_vehiculo)) {
        echo json_encode(array("success" => "0", "mensaje" => "Todos los campos son requeridos"));
    } else {
        $placa = mysqli_real_escape_string($link, $placa);
        $linea_vehiculo = mysqli_real_escape_string($link, $linea_vehiculo);
        $modelo = mysqli_real_escape_string($link, $modelo);
        $id_color = mysqli_real_escape_string($link, $id_color);
        $id_marca = mysqli_real_escape_string($link, $id_marca);
        $id_tipo_servicio = mysqli_real_escape_string($link, $id_tipo_servicio);
        $id_estado_vehiculo = mysqli_real_escape_string($link, $id_estado_vehiculo);
        
        $sql = "UPDATE vehiculo SET linea_vehiculo='$linea_vehiculo', modelo='$modelo', id_color='$id_color', 
                id_marca='$id_marca', id_tipo_servicio='$id_tipo_servicio', id_estado_vehiculo='$id_estado_vehiculo' 
                WHERE placa='$placa'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Vehículo actualizado correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

