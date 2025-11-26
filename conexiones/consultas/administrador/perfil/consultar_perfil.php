<?php
/**
 * Script para consultar el perfil completo de un administrador.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // Consulta principal del administrador con JOINs a catálogos
    $sql = "SELECT 
                ti.descripcion AS tipo_identificacion,
                a.identificacion,
                a.nombre,
                a.correo,
                a.direccion,
                cp.pais_nombre AS pais_residencia,
                cp.departamento,
                cp.ciudad
            FROM administrador a
            JOIN tipo_identificacion ti ON a.id_tipo_identificacion = ti.id_tipo_identificacion
            JOIN (
                SELECT cp_inner.id_codigo_postal, cp_inner.departamento, cp_inner.ciudad, p_res.nombre as pais_nombre
                FROM codigo_postal cp_inner
                JOIN pais p_res ON cp_inner.id_pais = p_res.id_pais
            ) cp ON a.codigo_postal = cp.id_codigo_postal
            WHERE a.correo = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $admin_data = mysqli_fetch_assoc($result);

        // Subconsulta para obtener los teléfonos
        $sql_tel = "SELECT telefono FROM telefono_administrador WHERE id_administrador = (SELECT id_administrador FROM administrador WHERE correo = ?)";
        $stmt_tel = mysqli_prepare($link, $sql_tel);
        mysqli_stmt_bind_param($stmt_tel, "s", $correo);
        mysqli_stmt_execute($stmt_tel);
        $result_tel = mysqli_stmt_get_result($stmt_tel);
        
        $telefonos = [];
        while ($row_tel = mysqli_fetch_assoc($result_tel)) {
            $telefonos[] = $row_tel['telefono'];
        }

        $admin_data['telefonos'] = $telefonos;

        $res["datos"] = $admin_data;
        $res["success"] = "1";
        $res["mensaje"] = "Datos encontrados";

    } else {
        $res["mensaje"] = "No se encontró el administrador.";
    }
    mysqli_stmt_close($stmt);

}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>