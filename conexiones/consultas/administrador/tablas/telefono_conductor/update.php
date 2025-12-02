<?php
// consultas/conductor/tablas/telefono_conductor/update.php

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Asegúrate de ajustar la ruta a tu archivo de conexión si es necesario
    include('../../../../config/conexion.php'); 
    $link = Conectar();
    
    // Obtener datos del cuerpo JSON
    $input = json_decode(file_get_contents('php://input'), true);
    
    // Clave primaria original y nuevo valor
    $id_conductor = isset($input['id_conductor']) ? $input['id_conductor'] : '';
    $telefono_original = isset($input['telefono_original']) ? $input['telefono_original'] : '';
    $telefono_nuevo = isset($input['telefono_nuevo']) ? $input['telefono_nuevo'] : '';

    
    // Validar campos requeridos
    if (empty($id_conductor) || empty($telefono_original) || empty($telefono_nuevo)) {
        echo json_encode(array("success" => "0", "mensaje" => "ID de conductor, teléfono original y teléfono nuevo son requeridos."));
        mysqli_close($link);
        exit;
    }
    
    // Validar que el nuevo teléfono no sea igual al original (buena práctica)
    if ($telefono_original === $telefono_nuevo) {
         echo json_encode(array("success" => "0", "mensaje" => "El nuevo teléfono es idéntico al original. No se requiere actualización."));
         mysqli_close($link);
         exit;
    }

    // Escapar y sanitizar
    $id_conductor = mysqli_real_escape_string($link, $id_conductor);
    $telefono_original = mysqli_real_escape_string($link, $telefono_original);
    $telefono_nuevo = mysqli_real_escape_string($link, $telefono_nuevo);
    
    // 1. Validar existencia del id_conductor (FK)
    $sql_check_admin = "SELECT 1 FROM conductor WHERE id_conductor = '$id_conductor'";
    $res_check_admin = mysqli_query($link, $sql_check_admin);
    
    if (mysqli_num_rows($res_check_admin) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El ID de conductor ('$id_conductor') no existe."));
        mysqli_close($link);
        exit;
    }

    // 2. Realizar el DELETE del registro original
    $sql_delete = "DELETE FROM telefono_conductor 
                   WHERE id_conductor='$id_conductor' AND telefono='$telefono_original'";
    
    $res_delete = mysqli_query($link, $sql_delete);
    
    if ($res_delete) {
        if (mysqli_affected_rows($link) == 0) {
             echo json_encode(array("success" => "0", "mensaje" => "Error: No se encontró el teléfono original para el ID de conductor."));
             mysqli_close($link);
             exit;
        }
        
        // 3. Realizar el INSERT del nuevo registro
        $sql_insert = "INSERT INTO telefono_conductor (id_conductor, telefono) 
                       VALUES ('$id_conductor', '$telefono_nuevo')";
        
        $res_insert = mysqli_query($link, $sql_insert);
        
        if ($res_insert) {
            echo json_encode(array("success" => "1", "mensaje" => "Teléfono del conductor actualizado correctamente"));
        } else {
            // Revertir el DELETE si el INSERT falla (Ej: si el nuevo teléfono ya existe con ese conductor)
            $sql_revert = "INSERT INTO telefono_conductor (id_conductor, telefono) 
                           VALUES ('$id_conductor', '$telefono_original')";
            // Intentamos revertir. No manejamos el error de la reversión para no complicar la respuesta al usuario.
            mysqli_query($link, $sql_revert); 

            echo json_encode(array("success" => "0", "mensaje" => "Error al insertar el nuevo teléfono (puede ser duplicado): " . mysqli_error($link)));
        }
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "Error al eliminar el teléfono original: " . mysqli_error($link)));
    }
    
    mysqli_close($link);
} else {
    echo json_encode(array("success" => "0", "mensaje" => "Método no permitido. Use POST"));
}
?>