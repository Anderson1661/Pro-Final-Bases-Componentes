<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    include('../config/conexion.php');
    $link = Conectar();
    
    $id_conductor = isset($_REQUEST['id_conductor']) ? $_REQUEST['id_conductor'] : '';
    $telefono = isset($_REQUEST['telefono']) ? $_REQUEST['telefono'] : '';
    $telefono_nuevo = isset($_REQUEST['telefono_nuevo']) ? $_REQUEST['telefono_nuevo'] : '';
    
    if (empty($id_conductor) || empty($telefono)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID conductor y teléfono son requeridos"));
    } else if (empty($telefono_nuevo)) {
        echo json_encode(array("success" => "0", "mensaje" => "El nuevo teléfono es requerido"));
    } else {
        $id_conductor = mysqli_real_escape_string($link, $id_conductor);
        $telefono = mysqli_real_escape_string($link, $telefono);
        $telefono_nuevo = mysqli_real_escape_string($link, $telefono_nuevo);
        
        $sql = "DELETE FROM telefono_conductor WHERE id_conductor='$id_conductor' AND telefono='$telefono'";
        $res1 = mysqli_query($link, $sql);
        
        if ($res1) {
            $sql2 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES ('$id_conductor', '$telefono_nuevo')";
            $res2 = mysqli_query($link, $sql2);
            
            if ($res2) {
                echo json_encode(array("success" => "1", "mensaje" => "Teléfono actualizado correctamente"));
            } else {
                echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar: " . mysqli_error($link)));
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

