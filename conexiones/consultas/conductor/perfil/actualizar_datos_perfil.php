<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

$required_params = [
    'id_conductor', 'id_tipo_identificacion', 'identificacion', 'nombre',
    'direccion', 'correo', 'id_genero', 'id_pais_nacionalidad', 'codigo_postal',
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
    $id_conductor = (int)$_POST['id_conductor'];
    $id_tipo_identificacion = (int)$_POST['id_tipo_identificacion'];
    $identificacion = trim($_POST['identificacion']);
    $nombre = trim($_POST['nombre']);
    $direccion = trim($_POST['direccion']);
    $correo = trim($_POST['correo']);
    $id_genero = (int)$_POST['id_genero'];
    $id_pais_nacionalidad = (int)$_POST['id_pais_nacionalidad'];
    $codigo_postal = trim($_POST['codigo_postal']);
    $tel1 = trim($_POST['tel1']);
    $tel2 = trim($_POST['tel2']);

    // Iniciar transacción
    mysqli_begin_transaction($link);
    $success_transaction = true;

    try {
        // Actualizar tabla conductor con prepared statement
        $sql_conductor = "UPDATE conductor SET
            id_tipo_identificacion = ?,
            identificacion = ?,
            nombre = ?,
            direccion = ?,
            correo = ?,
            id_genero = ?,
            id_pais_nacionalidad = ?,
            codigo_postal = ?
            WHERE id_conductor = ?";

        $stmt_con = mysqli_prepare($link, $sql_conductor);
        mysqli_stmt_bind_param(
            $stmt_con,
            "issssisisi",
            $id_tipo_identificacion,
            $identificacion,
            $nombre,
            $direccion,
            $correo,
            $id_genero,
            $id_pais_nacionalidad,
            $codigo_postal,
            $id_conductor
        );

        if (!mysqli_stmt_execute($stmt_con)) {
            $success_transaction = false;
            $res["mensaje"] = "Error al actualizar datos del conductor: " . mysqli_stmt_error($stmt_con);
        }
        mysqli_stmt_close($stmt_con);

        if ($success_transaction) {
            // Eliminar teléfonos existentes
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
            if (!empty($tel1)) {
                $sql_ins1 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (?, ?)";
                $stmt_ins1 = mysqli_prepare($link, $sql_ins1);
                mysqli_stmt_bind_param($stmt_ins1, "is", $id_conductor, $tel1);
                if (!mysqli_stmt_execute($stmt_ins1)) {
                    $success_transaction = false;
                    $res["mensaje"] = "Error al insertar teléfono 1: " . mysqli_stmt_error($stmt_ins1);
                }
                mysqli_stmt_close($stmt_ins1);
            }
        }

        if ($success_transaction) {
            if (!empty($tel2)) {
                $sql_ins2 = "INSERT INTO telefono_conductor (id_conductor, telefono) VALUES (?, ?)";
                $stmt_ins2 = mysqli_prepare($link, $sql_ins2);
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
        } else {
            mysqli_rollback($link);
        }

    } catch (Exception $e) {
        mysqli_rollback($link);
        $res["mensaje"] = "Excepción: " . $e->getMessage();
        error_log("Error al actualizar perfil conductor: " . $e->getMessage());
    }

}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
?>
