<?php
/**
 * Script para consultar los datos del perfil de un cliente.
 * 
 * Recibe el correo del cliente.
 * Devuelve información detallada: identificación, nombre, dirección, género, nacionalidad, residencia, ubicación y teléfonos.
 * Realiza múltiples JOINs para obtener las descripciones de las claves foráneas.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');
$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // Consulta principal con todos los JOIN necesarios
    $sql = "
        SELECT 
            c.id_cliente,
            c.identificacion,
            ti.descripcion AS tipo_identificacion,
            c.nombre,
            c.direccion,
            c.correo,
            g.descripcion AS genero,
            p_nac.nombre AS nacionalidad,
            p_res.nombre AS pais_residencia,
            cp.departamento,
            cp.ciudad,
            cp.id_codigo_postal AS codigo_postal
        FROM cliente c
        INNER JOIN tipo_identificacion ti ON c.id_tipo_identificacion = ti.id_tipo_identificacion
        INNER JOIN genero g ON c.id_genero = g.id_genero
        INNER JOIN pais p_nac ON c.id_pais_nacionalidad = p_nac.id_pais
        INNER JOIN codigo_postal cp ON c.codigo_postal = cp.id_codigo_postal
        INNER JOIN pais p_res ON cp.id_pais = p_res.id_pais
        WHERE c.correo = ?
        LIMIT 1
    ";

    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $cliente_data = mysqli_fetch_assoc($result);

        // Consulta los teléfonos del cliente
        $sql_tel = "
            SELECT telefono 
            FROM telefono_cliente 
            WHERE id_cliente = ?
        ";
        $stmt_tel = mysqli_prepare($link, $sql_tel);
        mysqli_stmt_bind_param($stmt_tel, "i", $cliente_data['id_cliente']);
        mysqli_stmt_execute($stmt_tel);
        $result_tel = mysqli_stmt_get_result($stmt_tel);

        $telefonos = [];
        while ($row_tel = mysqli_fetch_assoc($result_tel)) {
            $telefonos[] = $row_tel['telefono'];
        }

        // Añadir los teléfonos al resultado principal
        $cliente_data['telefonos'] = $telefonos;

        // Armar respuesta final
        $res["success"] = "1";
        $res["mensaje"] = "Datos encontrados correctamente.";
        $res["datos"] = $cliente_data;

    } else {
        $res["mensaje"] = "No se encontró ningún cliente con ese correo.";
    }

    mysqli_stmt_close($stmt);
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
