<?php
// Incluye el archivo de configuración de la conexión a la base de datos.
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res["success"] = "0";

// 1. Recibir y decodificar el JSON
$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

if ($data === null) {
    $res["mensaje"] = "Error al decodificar JSON.";
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    mysqli_close($link);
    exit;
}

$correo = $data['correo'] ?? '';
$preguntas = $data['preguntas'] ?? [];

if (empty($correo) || count($preguntas) !== 3) {
    $res["mensaje"] = "Datos incompletos o formato incorrecto (se esperan 3 preguntas).";
    echo json_encode($res, JSON_UNESCAPED_UNICODE);
    mysqli_close($link);
    exit;
}

// 2. Obtener el id_usuario a partir del correo
$sql_usuario = "SELECT id_usuario FROM usuario WHERE correo = ?";
$stmt_usuario = mysqli_prepare($link, $sql_usuario);

if ($stmt_usuario) {
    mysqli_stmt_bind_param($stmt_usuario, "s", $correo);
    mysqli_stmt_execute($stmt_usuario);
    $res_usuario = mysqli_stmt_get_result($stmt_usuario);

    if (mysqli_num_rows($res_usuario) == 1) {
        $row_usuario = mysqli_fetch_assoc($res_usuario);
        $id_usuario = $row_usuario['id_usuario'];
        mysqli_stmt_close($stmt_usuario);

        // 3. Iniciar Transacción
        mysqli_begin_transaction($link);
        $todo_ok = true;

        // 4. Preparar la sentencia de inserción de respuestas
        $sql_insert = "INSERT INTO respuestas_seguridad (id_pregunta, id_usuario, respuesta_pregunta) VALUES (?, ?, ?)";
        $stmt_insert = mysqli_prepare($link, $sql_insert);

        if ($stmt_insert) {
            // Iterar sobre las 3 preguntas y realizar la inserción
            foreach ($preguntas as $p) {
                $id_pregunta = $p['id_pregunta'] ?? 0;
                $respuesta = $p['respuesta'] ?? '';

                if ($id_pregunta > 0 && !empty($respuesta)) {
                    // Limpiar y Bindear parámetros
                    mysqli_stmt_bind_param($stmt_insert, "iis", $id_pregunta, $id_usuario, $respuesta);

                    if (!mysqli_stmt_execute($stmt_insert)) {
                        // Error en la inserción de una de las preguntas
                        $res["mensaje"] = "Error al insertar respuestas de seguridad: " . mysqli_error($link);
                        $todo_ok = false;
                        break; // Salir del bucle
                    }
                } else {
                    $res["mensaje"] = "Datos de pregunta/respuesta inválidos.";
                    $todo_ok = false;
                    break;
                }
            }
            mysqli_stmt_close($stmt_insert);

            // 5. Finalizar Transacción
            if ($todo_ok) {
                mysqli_commit($link);
                $res["success"] = "1";
                $res["mensaje"] = "Respuestas de seguridad registradas exitosamente.";
            } else {
                mysqli_rollback($link);
                if ($res["mensaje"] === "0") {
                    $res["mensaje"] = "Transacción fallida al registrar respuestas.";
                }
            }

        } else {
            $res["mensaje"] = "Error al preparar la sentencia de inserción: " . mysqli_error($link);
        }

    } else {
        mysqli_stmt_close($stmt_usuario);
        $res["mensaje"] = "Usuario no encontrado con el correo proporcionado.";
    }

} else {
    $res["mensaje"] = "Error al preparar la sentencia de usuario: " . mysqli_error($link);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>