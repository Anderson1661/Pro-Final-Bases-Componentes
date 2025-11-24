<?php
/**
 * Script para consultar departamentos por país.
 * 
 * Recibe el ID del país y devuelve una lista única de departamentos.
 * Se utiliza para llenar selectores dependientes en formularios.
 */

include('../../../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array();
$res['datos'] = array();

// El id_pais se enviará desde la app.
// Si no se envía, se puede poner un valor por defecto o manejar el error.
$id_pais = isset($_POST['id_pais']) ? (int)$_POST['id_pais'] : 0;

if ($id_pais > 0) {
    // Usamos DISTINCT para no repetir departamentos
    $sql = "SELECT DISTINCT departamento 
            FROM codigo_postal 
            WHERE id_pais = ?";
    
    $stmt = mysqli_prepare($link, $sql);
    mysqli_stmt_bind_param($stmt, "i", $id_pais);
    
    if (mysqli_stmt_execute($stmt)) {
        $res1 = mysqli_stmt_get_result($stmt);
        if (mysqli_num_rows($res1) > 0) {
            while ($row = mysqli_fetch_assoc($res1)) {
                $item = array(
                    "nombre"  => $row['departamento']
                );
                array_push($res['datos'], $item);
            }
            $res["success"] = "1";
        } else {
            $res["success"] = "1";
            $res["mensaje"] = "No hay departamentos para el país seleccionado.";
        }
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Error en la consulta: " . mysqli_stmt_error($stmt);
    }
    mysqli_stmt_close($stmt);
} else {
    $res["success"] = "0";
    $res["mensaje"] = "ID de país no válido o no proporcionado.";
}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
