<?php
/**
 * Script para actualizar las respuestas de seguridad de un cliente.
 * 
 * Recibe el ID del usuario y 3 conjuntos de (pregunta original, nueva pregunta, respuesta).
 * Realiza una transacción para asegurar que todas las preguntas se actualicen correctamente.
 * Utiliza REPLACE INTO para insertar o actualizar, y DELETE si la pregunta ha cambiado.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

if (isset($_POST['id_usuario'], $_POST['original_id_pregunta1'], $_POST['nuevo_id_pregunta1'], $_POST['respuesta1'],
          $_POST['original_id_pregunta2'], $_POST['nuevo_id_pregunta2'], $_POST['respuesta2'],
          $_POST['original_id_pregunta3'], $_POST['nuevo_id_pregunta3'], $_POST['respuesta3'])) {
    
    $id_usuario = intval($_POST['id_usuario']);

    $updates = [];
    for ($i = 1; $i <= 3; $i++) {
        $updates[] = [
            'original_id' => intval($_POST["original_id_pregunta$i"]),
            'nuevo_id' => intval($_POST["nuevo_id_pregunta$i"]),
            'respuesta' => $_POST["respuesta$i"]
        ];
    }
    
    // Iniciar transacción (más seguro para operaciones de DELETE/INSERT o REPLACE)
    mysqli_autocommit($link, FALSE);
    $all_success = true;

    // Usaremos REPLACE INTO para simplificar: si el par (id_usuario, id_pregunta) existe, actualiza; si no, inserta.
    // Necesitamos DELETE si el ID de la pregunta *cambió* para eliminar el registro viejo.
    
    $sql_update_or_insert = "
        REPLACE INTO respuestas_seguridad (id_usuario, id_pregunta, respuesta_pregunta) 
        VALUES (?, ?, ?)";
    
    $sql_delete_old = "
        DELETE FROM respuestas_seguridad 
        WHERE id_usuario = ? AND id_pregunta = ?";

    $stmt_update = mysqli_prepare($link, $sql_update_or_insert);
    $stmt_delete = mysqli_prepare($link, $sql_delete_old);


    foreach ($updates as $data) {
        $original_id = $data['original_id'];
        $nuevo_id = $data['nuevo_id'];
        $respuesta = $data['respuesta'];

        // Si la pregunta es nueva, primero borramos la entrada vieja.
        if ($original_id != $nuevo_id) {
            mysqli_stmt_bind_param($stmt_delete, "ii", $id_usuario, $original_id);
            if (!mysqli_stmt_execute($stmt_delete)) {
                $all_success = false;
                break;
            }
        }
        
        // Insertar/Reemplazar el nuevo par (id_pregunta, respuesta_pregunta).
        mysqli_stmt_bind_param($stmt_update, "iis", $id_usuario, $nuevo_id, $respuesta);
        if (!mysqli_stmt_execute($stmt_update)) {
            $all_success = false;
            break;
        }
    }
    
    mysqli_stmt_close($stmt_update);
    mysqli_stmt_close($stmt_delete);

    if ($all_success) {
        mysqli_commit($link);
        $res["success"] = "1";
        $res["mensaje"] = "Preguntas de seguridad actualizadas con éxito.";
    } else {
        mysqli_rollback($link);
        $res["success"] = "0";
        $res["mensaje"] = "Fallo al actualizar las preguntas de seguridad. Se revirtieron los cambios.";
    }

} 

echo json_encode($res);
mysqli_close($link);
?>