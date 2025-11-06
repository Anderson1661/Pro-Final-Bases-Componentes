<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_genero = isset($_REQUEST['id_genero']) ? $_REQUEST['id_genero'] : '';
    
    if (empty($id_genero)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID requerido"));
    } else {
        $id_genero = mysqli_real_escape_string($link, $id_genero);
        
        $sql = "DELETE FROM genero WHERE id_genero='$id_genero'";
        $res = mysqli_query($link, $sql);
        
        if ($res) {
            if (mysqli_affected_rows($link) > 0) {
                echo json_encode(array("success" => "1", "mensaje" => "Género eliminado correctamente"));
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

