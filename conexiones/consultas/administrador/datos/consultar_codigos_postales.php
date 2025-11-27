<?php
include('../../../config/conexion.php');
$link = Conectar();

$sql = "SELECT cp.id_codigo_postal, cp.id_pais, cp.departamento, cp.ciudad, p.nombre as pais 
        FROM codigo_postal cp 
        JOIN pais p ON cp.id_pais = p.id_pais 
        ORDER BY p.nombre, cp.departamento, cp.ciudad";

$res = mysqli_query($link, $sql);

if ($res && mysqli_num_rows($res) > 0) {
    $datos = array();
    while ($row = mysqli_fetch_assoc($res)) {
        $datos[] = $row;
    }
    echo json_encode(array("success" => "1", "datos" => $datos));
} else {
    echo json_encode(array("success" => "1", "mensaje" => "No hay códigos postales", "datos" => array()));
}

mysqli_close($link);
?>