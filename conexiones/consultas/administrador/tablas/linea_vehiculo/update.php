<?php
include('../../../../config/conexion.php');
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);

// IDs Originales (para identificar el registro a cambiar)
$id_linea_original = isset($input['id_linea_original']) ? trim($input['id_linea_original']) : '';
$id_marca_original = isset($input['id_marca_original']) ? trim($input['id_marca_original']) : '';

// IDs Nuevos (los valores a insertar)
$id_linea_nuevo = isset($input['id_linea_nuevo']) ? trim($input['id_linea_nuevo']) : '';
$id_marca_nuevo = isset($input['id_marca_nuevo']) ? trim($input['id_marca_nuevo']) : '';


if (empty($id_linea_original) || empty($id_marca_original)) {
    echo json_encode(array("success" => "0", "mensaje" => "Los IDs originales son requeridos"));
    exit;
} else if (empty($id_linea_nuevo) || empty($id_marca_nuevo)) {
    echo json_encode(array("success" => "0", "mensaje" => "Los nuevos IDs son requeridos"));
    exit;
} else {
    // Escapar variables
    $id_linea_original = mysqli_real_escape_string($link, $id_linea_original);
    $id_marca_original = mysqli_real_escape_string($link, $id_marca_original);
    $id_linea_nuevo = mysqli_real_escape_string($link, $id_linea_nuevo);
    $id_marca_nuevo = mysqli_real_escape_string($link, $id_marca_nuevo);
    
    // 1. Verificar si no hubo cambios (para evitar operaciones innecesarias)
    if ($id_linea_original == $id_linea_nuevo && $id_marca_original == $id_marca_nuevo) {
        echo json_encode(array("success" => "1", "mensaje" => "No se realizaron cambios"));
        exit;
    }

    // 2. Validar que el nuevo ID de Marca (FK) exista
    $check_marca = "SELECT id_marca FROM marca_vehiculo WHERE id_marca = '$id_marca_nuevo'";
    if (mysqli_num_rows(mysqli_query($link, $check_marca)) == 0) {
        echo json_encode(array("success" => "0", "mensaje" => "El ID de marca proporcionado no existe"));
        exit;
    }

    // 3. Validar que la nueva combinación (PK) no exista ya (si es diferente a la original)
    $check_pk = "SELECT id_linea FROM linea_vehiculo 
                 WHERE id_linea = '$id_linea_nuevo' AND id_marca = '$id_marca_nuevo'";

    // Si la nueva clave primaria es diferente a la original
    if ($id_linea_original != $id_linea_nuevo || $id_marca_original != $id_marca_nuevo) {
        if (mysqli_num_rows(mysqli_query($link, $check_pk)) > 0) {
            echo json_encode(array("success" => "0", "mensaje" => "Ya existe una línea con esa combinación de nombre y marca"));
            exit;
        }
    }
    
    // 4. Iniciar transacción para asegurar la integridad
    mysqli_begin_transaction($link);
    $success = true;

    try {
        // A. Eliminar el registro original
        $sql_delete = "DELETE FROM linea_vehiculo 
                       WHERE id_linea = '$id_linea_original' AND id_marca = '$id_marca_original'";
        $res_delete = mysqli_query($link, $sql_delete);
        
        if (!$res_delete || mysqli_affected_rows($link) == 0) {
             throw new Exception("Error al eliminar el registro original o registro no encontrado.");
        }

        // B. Insertar el nuevo registro
        $sql_insert = "INSERT INTO linea_vehiculo (id_linea, id_marca) 
                       VALUES ('$id_linea_nuevo', '$id_marca_nuevo')";
        $res_insert = mysqli_query($link, $sql_insert);
        
        if (!$res_insert) {
            throw new Exception("Error al insertar el nuevo registro.");
        }
        
        // Confirmar transacción
        mysqli_commit($link);
        echo json_encode(array("success" => "1", "mensaje" => "Línea de vehículo actualizada correctamente"));

    } catch (Exception $e) {
        // Revertir transacción en caso de error
        mysqli_rollback($link);
        echo json_encode(array("success" => "0", "mensaje" => "Error al actualizar la línea: " . $e->getMessage()));
    }
}

mysqli_close($link);
?>