<?php
/**
 * Script para cambiar el estado de una ruta (servicio).
 * 
 * Recibe el ID de la ruta, el nuevo estado y el ID del conductor.
 * Maneja diferentes lógicas según el estado:
 * - Estado 2 (Aceptar): Asigna el conductor y marca la hora de inicio.
 * - Estado 3 (Finalizar): Marca la hora de finalización.
 * - Otros estados: Solo actualiza el estado.
 */

include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_ruta']) && isset($_POST['id_estado'])) {
    $id_ruta = (int)trim($_POST['id_ruta']);
    $id_estado = (int)trim($_POST['id_estado']);
    $id_conductor = isset($_POST['id_conductor']) ? (int)trim($_POST['id_conductor']) : null; // NUEVO: Capturar el ID del conductor

    $sql = "";
    $bind_type = "";
    
    if ($id_estado == 3) {
        // ACCIÓN FINALIZAR: Actualiza estado (3) y fecha_hora_destino = NOW()
        $sql = "UPDATE ruta SET id_estado_servicio = ?, fecha_hora_destino = NOW() WHERE id_ruta = ?";
        $bind_type = "ii";
    } elseif ($id_estado == 2) {
        // ACCIÓN ACEPTAR: Actualiza estado (2), id_conductor, y fecha_hora_origen = NOW()
        if ($id_conductor === null) {
             $res["mensaje"] = "Parámetro id_conductor faltante para la acción ACEPTAR (Estado 2).";
             echo json_encode($res, JSON_UNESCAPED_UNICODE);
             mysqli_close($link);
             return;
        }
        $sql = "UPDATE ruta SET id_estado_servicio = ?, id_conductor = ?, fecha_hora_origen = NOW() WHERE id_ruta = ?";
        $bind_type = "iii";
    } else {
        // OTROS ESTADOS (ej. 4): Solo actualiza el estado (sin hora)
        $sql = "UPDATE ruta SET id_estado_servicio = ? WHERE id_ruta = ?";
        $bind_type = "ii";
    }

    $stmt = mysqli_prepare($link, $sql);
    
    if ($stmt) {
        // Aplicar bind_param dependiendo del estado
        if ($id_estado == 2) {
             // Estado (i), ID Conductor (i), ID Ruta (i)
             mysqli_stmt_bind_param($stmt, $bind_type, $id_estado, $id_conductor, $id_ruta);
        } else {
             // Estado (i), ID Ruta (i)
             mysqli_stmt_bind_param($stmt, $bind_type, $id_estado, $id_ruta);
        }

        mysqli_stmt_execute($stmt);

        if (mysqli_stmt_affected_rows($stmt) > 0) {
            $res["success"] = "1";
            $res["mensaje"] = "Estado de ruta actualizado correctamente.";
        } else {
            $res["mensaje"] = "No se pudo actualizar la ruta.";
        }
        mysqli_stmt_close($stmt);
    } else {
        $res["mensaje"] = "Error al preparar la consulta.";
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>