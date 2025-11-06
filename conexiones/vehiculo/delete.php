<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $placa = isset($_REQUEST['placa']) ? $_REQUEST['placa'] : '';
    
    if (empty($placa)) {
        echo json_encode(array("success" => "0", "mensaje" => "La placa es requerida"));
    } else {
        $placa = mysqli_real_escape_string($link, $placa);
        
        $sql = "DELETE FROM vehiculo WHERE placa='$placa'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Vehículo eliminado correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "No se encontró el registro"));
            }
        } else {
            echo json_encode(array("success" => "0", "mensaje" => "Error al eliminar: " . mysqli_error($link)));
        }
    }
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido"));
}
?>

