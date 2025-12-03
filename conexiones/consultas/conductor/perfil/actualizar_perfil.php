<?php
/**
 * Script para actualizar el perfil completo de un conductor.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

$required_params = [
    'correo', 'id_tipo_identificacion', 'identificacion', 'nombre', 
    'direccion', 'codigo_postal', 'placa', 'id_pais_nacionalidad',
    'id_genero', 'tel1', 'tel2'
];

$all_set = true;
foreach ($required_params as $param) {
    if (!isset($_POST[$param])) {
        $all_set = false;
        break;
    }
}

if ($all_set) {
    // Obtener primero el ID del conductor usando el correo
    $correo = trim($_POST['correo']);
    
    // Función para obtener ID del conductor por correo
    $sql_obtener_id = "SELECT id_conductor FROM conductor WHERE correo = ?";
    $stmt_obtener = mysqli_prepare($link, $sql_obtener_id);
    mysqli_stmt_bind_param($stmt_obtener, "s", $correo);
    mysqli_stmt_execute($stmt_obtener);
    $result_obtener = mysqli_stmt_get_result($stmt_obtener);
    
    if ($row = mysqli_fetch_assoc($result_obtener)) {
        $id_conductor = (int)$row['id_conductor'];
        mysqli_stmt_close($stmt_obtener);
        
        // Ahora procedemos con la actualización usando el ID obtenido
        $id_tipo_identificacion = (int)$_POST['id_tipo_identificacion'];
        $identificacion = trim($_POST['identificacion']);
        $nombre = trim($_POST['nombre']);
        $direccion = trim($_POST['direccion']);
        $codigo_postal = trim($_POST['codigo_postal']);
        $placa = trim($_POST['placa']);
        $id_pais_nacionalidad = (int)$_POST['id_pais_nacionalidad'];
        $id_genero = (int)$_POST['id_genero'];
        $tel1 = trim($_POST['tel1']);
        $tel2 = trim($_POST['tel2']);

        mysqli_begin_transaction($link);
        $success_transaction = true;
        
        try {
            // Actualizar la tabla CONDUCTOR
            $sql_conductor = "
                UPDATE conductor 
                SET 
                    id_tipo_identificacion = ?,
                    identificacion = ?,
                    nombre = ?,
                    direccion = ?,
                    codigo_postal = ?,
                    placa_vehiculo = ?,
                    id_pais_nacionalidad = ?,
                    id_genero = ?,
                    id_estado_conductor = 2  -- Siempre queda en 2
                WHERE id_conductor = ?
            ";
            
            $stmt_conductor = mysqli_prepare($link, $sql_conductor);
            mysqli_stmt_bind_param(
                $stmt_conductor, 
                "isssssiii", 
                $id_tipo_identificacion, 
                $identificacion, 
                $nombre, 
                $direccion, 
                $codigo_postal, 
                $placa, 
                $id_pais_nacionalidad, 
                $id_genero, 
                $id_conductor
            );
            
            if (!mysqli_stmt_execute($stmt_conductor)) {
                $success_transaction = false;
                $res["mensaje"] = "Error al actualizar datos del conductor: " . mysqli_stmt_error($stmt_conductor);
            }
            mysqli_stmt_close($stmt_conductor);

            // Actualizar la tabla TELEFONO_CONDUCTOR
            if ($success_transaction) {
                // Eliminar todos los teléfonos existentes del conductor
                $sql_del_tel = "DELETE FROM telefono_conductor WHERE id_conductor = ?";
                $stmt_del = mysqli_prepare($link, $sql_del_tel);
                mysqli_stmt_bind_param($stmt_del, "i", $id_conductor);
                if (!mysqli_stmt_execute($stmt_del)) {
                    $success_transaction = false;
                    $res["mensaje"] = "Error al eliminar teléfonos existentes: " . mysqli_stmt_error($stmt_del);
                }
                mysqli_stmt_close($stmt_del);
            }

            if ($success_transaction) {
                // Insertar tel1 si no está vacío
                if (!empty($tel1)) {
                    $sql_ins_tel1 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (?, ?)";
                    $stmt_ins1 = mysqli_prepare($link, $sql_ins_tel1);
                    mysqli_stmt_bind_param($stmt_ins1, "is", $id_conductor, $tel1);
                    if (!mysqli_stmt_execute($stmt_ins1)) {
                        $success_transaction = false;
                        $res["mensaje"] = "Error al insertar teléfono 1: " . mysqli_stmt_error($stmt_ins1);
                    }
                    mysqli_stmt_close($stmt_ins1);
                }
            }
            
            if ($success_transaction) {
                // Insertar tel2 si no está vacío
                if (!empty($tel2)) {
                    $sql_ins_tel2 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (?, ?)";
                    $stmt_ins2 = mysqli_prepare($link, $sql_ins_tel2);
                    mysqli_stmt_bind_param($stmt_ins2, "is", $id_conductor, $tel2);
                    if (!mysqli_stmt_execute($stmt_ins2)) {
                        $success_transaction = false;
                        $res["mensaje"] = "Error al insertar teléfono 2: " . mysqli_stmt_error($stmt_ins2);
                    }
                    mysqli_stmt_close($stmt_ins2);
                }
            }

            if ($success_transaction) {
                mysqli_commit($link);
                $res["success"] = "1";
                $res["mensaje"] = "Perfil actualizado correctamente.";
                $res["id_conductor"] = $id_conductor;
            } else {
                mysqli_rollback($link);
            }

        } catch (Exception $e) {
            mysqli_rollback($link);
            $res["mensaje"] = "Excepción: " . $e->getMessage();
            error_log("Error al actualizar perfil del conductor: " . $e->getMessage());
        }
    } else {
        $res["mensaje"] = "No se encontró conductor con el correo proporcionado.";
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>