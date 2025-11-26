<?php
/**
 * Script para consultar el catálogo de preguntas de seguridad.
 * 
 * Devuelve la lista de todas las preguntas de seguridad disponibles en el sistema.
 * Se utiliza para llenar los selectores en el formulario de registro o configuración de seguridad.
 */

// Incluye el archivo de configuración de la conexión a la base de datos.
include('../../../config/conexion.php');
// Establece la conexión. Se asume que Conectar() devuelve un objeto de conexión (mysqli_connect o similar).
$link = Conectar();

// Establece la cabecera para indicar que la respuesta es un JSON con codificación UTF-8.
header('Content-Type: application/json; charset=utf-8');

// Inicializa el array de respuesta.
$res = array();
$res['datos'] = array();

// Consulta SQL para seleccionar el ID y la descripción de la tabla preguntas_seguridad.
// El ID se incluye aunque no se use en el JSON final por si se necesita para depuración.
$sql = "SELECT id_pregunta, descripcion FROM preguntas_seguridad ORDER BY id_pregunta";
$res1 = mysqli_query($link, $sql);

if ($res1) {
    // Si la consulta fue exitosa
    if (mysqli_num_rows($res1) > 0) {
        // Hay registros para mostrar
        while ($row = mysqli_fetch_assoc($res1)) {
            // Incluir id_pregunta y descripcion para uso en el cliente
            $item = array(
                "id_pregunta" => (int)$row['id_pregunta'],
                "descripcion"  => $row['descripcion']
            );
            array_push($res['datos'], $item);
        }
        $res["success"] = "1"; // Éxito en la operación y se encontraron datos.
    } else {
        // La consulta fue exitosa, pero no se encontraron registros.
        $res["success"] = "1";
        $res["mensaje"] = "No hay registros de preguntas de seguridad.";
    }
} else {
    // La consulta falló.
    $res["success"] = "0";
    $res["mensaje"] = "Error al consultar las preguntas de seguridad: " . mysqli_error($link);
}

// Devuelve el array de respuesta codificado en JSON.
// JSON_UNESCAPED_UNICODE asegura que los caracteres especiales (como tildes y eñes)
// se muestren correctamente sin codificación Unicode (ej: \u00e1 en lugar de á).
echo json_encode($res, JSON_UNESCAPED_UNICODE);

// Cierra la conexión a la base de datos.
mysqli_close($link);
?>