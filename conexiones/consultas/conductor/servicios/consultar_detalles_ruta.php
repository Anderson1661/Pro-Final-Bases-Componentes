<?php
/**
 * Script para consultar detalles adicionales de una ruta.
 * 
 * Recibe el ID de la ruta.
 * Devuelve:
 * 1. Teléfonos del cliente asociado.
 * 2. Lista de pasajeros registrados en la ruta.
 * Se utiliza para que el conductor pueda contactar al cliente o verificar pasajeros.
 */

include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_ruta'])) {
    $id_ruta = (int)trim($_POST['id_ruta']);

    // 1. Obtener los teléfonos del cliente asociado a la ruta
    $sql_phones = "SELECT tc.telefono 
                   FROM telefono_cliente tc
                   JOIN ruta r ON tc.id_cliente = r.id_cliente
                   WHERE r.id_ruta = ?";
    $stmt_phones = mysqli_prepare($link, $sql_phones);
    $telefonos = array();
    if ($stmt_phones) {
        mysqli_stmt_bind_param($stmt_phones, "i", $id_ruta);
        mysqli_stmt_execute($stmt_phones);
        $result_phones = mysqli_stmt_get_result($stmt_phones);
        while ($row = mysqli_fetch_assoc($result_phones)) {
            $telefonos[] = $row['telefono'];
        }
        mysqli_stmt_close($stmt_phones);
    }

    // 2. Obtener los nombres de los pasajeros asociados a la ruta (si es de personas)
    $sql_passengers = "SELECT nombre_pasajero 
                       FROM pasajero_ruta 
                       WHERE id_ruta = ?";
    $stmt_passengers = mysqli_prepare($link, $sql_passengers);
    $pasajeros = array();
    if ($stmt_passengers) {
        mysqli_stmt_bind_param($stmt_passengers, "i", $id_ruta);
        mysqli_stmt_execute($stmt_passengers);
        $result_passengers = mysqli_stmt_get_result($stmt_passengers);
        while ($row = mysqli_fetch_assoc($result_passengers)) {
            $pasajeros[] = $row['nombre_pasajero'];
        }
        mysqli_stmt_close($stmt_passengers);
    }
    
    // 3. Respuesta
    $res["success"] = "1";
    $res["mensaje"] = "Detalles obtenidos.";
    $res["telefonos"] = $telefonos;
    $res["pasajeros"] = $pasajeros;

}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>