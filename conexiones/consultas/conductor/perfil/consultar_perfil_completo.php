<?php
include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['correo'])) {
    $correo = trim($_POST['correo']);

    // Consulta principal del cliente
    $sql = "SELECT 
                ti.descripcion AS tipo_identificacion,
                c.identificacion,
                c.nombre,
                c.correo,
                c.direccion,
                p_nac.nombre AS nacionalidad,
                cp.pais_nombre AS pais_residencia,
                cp.departamento,
                cp.ciudad
            FROM cliente c
            JOIN tipo_identificacion ti ON c.id_tipo_identificacion = ti.id_tipo_identificacion
            JOIN pais p_nac ON c.id_pais_nacionalidad = p_nac.id_pais
            JOIN (
                SELECT cp_inner.id_codigo_postal, cp_inner.departamento, cp_inner.ciudad, p_res.nombre as pais_nombre
                FROM codigo_postal cp_inner
                JOIN pais p_res ON cp_inner.id_pais = p_res.id_pais
            ) cp ON c.codigo_postal = cp.id_codigo_postal
            WHERE c.correo = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "s", $correo);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $cliente_data = mysqli_fetch_assoc($result);

        // Consulta de teléfonos
        $sql_tel = "SELECT telefono FROM telefono_cliente WHERE id_cliente = (SELECT id_cliente FROM cliente WHERE correo = ?)";
        $stmt_tel = mysqli_prepare($link, $sql_tel);
        mysqli_stmt_bind_param($stmt_tel, "s", $correo);
        mysqli_stmt_execute($stmt_tel);
        $result_tel = mysqli_stmt_get_result($stmt_tel);
        
        $telefonos = [];
        while ($row_tel = mysqli_fetch_assoc($result_tel)) {
            $telefonos[] = $row_tel['telefono'];
        }

        // Añadir teléfonos a los datos del cliente
        $cliente_data['telefonos'] = $telefonos;

        $res["datos"] = $cliente_data;
        $res["success"] = "1";
        $res["mensaje"] = "Datos encontrados";

    } else {
        $res["mensaje"] = "No se encontró el cliente.";
    }
    mysqli_stmt_close($stmt);

}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
