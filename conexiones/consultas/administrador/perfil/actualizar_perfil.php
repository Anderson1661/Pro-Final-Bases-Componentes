<?php
/**
 * Script para actualizar el perfil completo de un administrador.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

$required_params = [
    'id_administrador', 'id_tipo_identificacion', 'identificacion', 'nombre', 
    'direccion', 'correo', 'codigo_postal',
    'tel1', 'tel2'
];

$all_set = true;
foreach ($required_params as $param) {
    if (!isset($_POST[$param])) {
        $all_set = false;
        break;
    }
}

if ($all_set) {
    $id_administrador = (int)$_POST['id_administrador'];
    $id_tipo_identificacion = (int)$_POST['id_tipo_identificacion'];
    $identificacion = trim($_POST['identificacion']);
    $nombre = trim($_POST['nombre']);
    $direccion = trim($_POST['direccion']);
    $correo = trim($_POST['correo']);
    $codigo_postal = trim($_POST['codigo_postal']);
    $tel1 = trim($_POST['tel1']);
    $tel2 = trim($_POST['tel2']);

    mysqli_begin_transaction($link);
    $success_transaction = true;
    
    try {
        // Actualizar la tabla ADMINISTRADOR
        $sql_admin = "
            UPDATE administrador 
            SET 
                id_tipo_identificacion = ?,
                identificacion = ?,
                nombre = ?,
                direccion = ?,
                correo = ?,
                codigo_postal = ?
            WHERE id_administrador = ?
        ";
        
        $stmt_admin = mysqli_prepare($link, $sql_admin);
        mysqli_stmt_bind_param(
            $stmt_admin, 
            "isssssi", 
            $id_tipo_identificacion, 
            $identificacion, 
            $nombre, 
            $direccion, 
            $correo, 
            $codigo_postal, 
            $id_administrador
        );
        
        if (!mysqli_stmt_execute($stmt_admin)) {
            $success_transaction = false;
            $res["mensaje"] = "Error al actualizar datos del administrador: " . mysqli_stmt_error($stmt_admin);
        }
        mysqli_stmt_close($stmt_admin);

        // Actualizar la tabla TELEFONO_ADMINISTRADOR
        if ($success_transaction) {
            // Eliminar todos los teléfonos existentes
            $sql_del_tel = "DELETE FROM telefono_administrador WHERE id_administrador = ?";
            $stmt_del = mysqli_prepare($link, $sql_del_tel);
            mysqli_stmt_bind_param($stmt_del, "i", $id_administrador);
            if (!mysqli_stmt_execute($stmt_del)) {
                $success_transaction = false;
                $res["mensaje"] = "Error al eliminar teléfonos existentes: " . mysqli_stmt_error($stmt_del);
            }
            mysqli_stmt_close($stmt_del);
        }

        if ($success_transaction) {
            // Insertar tel1 si no está vacío
            if (!empty($tel1)) {
                $sql_ins_tel1 = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES (?, ?)";
                $stmt_ins1 = mysqli_prepare($link, $sql_ins_tel1);
                mysqli_stmt_bind_param($stmt_ins1, "is", $id_administrador, $tel1);
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
                $sql_ins_tel2 = "INSERT INTO telefono_administrador (id_administrador, telefono) VALUES (?, ?)";
                $stmt_ins2 = mysqli_prepare($link, $sql_ins_tel2);
                mysqli_stmt_bind_param($stmt_ins2, "is", $id_administrador, $tel2);
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
        } else {
            mysqli_rollback($link);
        }

    } catch (Exception $e) {
        mysqli_rollback($link);
        $res["mensaje"] = "Excepción: " . $e->getMessage();
        error_log("Error al actualizar perfil: " . $e->getMessage());
    }
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>