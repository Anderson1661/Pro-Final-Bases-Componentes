<?php
/**
 * Script para obtener el código postal basado en ubicación.
 * 
 * Recibe País, Departamento y Ciudad y devuelve el ID del código postal correspondiente.
 * Se utiliza en formularios donde el usuario selecciona su ubicación mediante selectores.
 */

// Incluir el archivo de conexión a la base de datos
include('../../../config/conexion.php');
$link = Conectar();

// Establecer la cabecera para la respuesta JSON
header('Content-Type: application/json; charset=utf-8');
// Inicializar la respuesta
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

// Verificar que se hayan recibido los parámetros necesarios (pais, departamento, ciudad)
if (isset($_POST['pais']) && isset($_POST['departamento']) && isset($_POST['ciudad'])) {
    
    // Limpiar y obtener los valores de los parámetros
    $nombre_pais = trim($_POST['pais']);
    $departamento = trim($_POST['departamento']);
    $ciudad = trim($_POST['ciudad']);

    // Consulta SQL para obtener el código postal.
    // Se une con la tabla 'pais' para buscar por el nombre del país.
    $sql = "
        SELECT 
            cp.id_codigo_postal AS codigo_postal
        FROM codigo_postal cp
        INNER JOIN pais p ON cp.id_pais = p.id_pais
        WHERE 
            p.nombre = ? AND 
            cp.departamento = ? AND 
            cp.ciudad = ?
        LIMIT 1
    ";

    // Preparar la consulta
    $stmt = mysqli_prepare($link, $sql);

    // Verificar si la preparación de la consulta fue exitosa
    if ($stmt) {
        // Enlazar los parámetros: 's' para string
        mysqli_stmt_bind_param($stmt, "sss", $nombre_pais, $departamento, $ciudad);
        
        // Ejecutar la consulta
        mysqli_stmt_execute($stmt);
        
        // Obtener el resultado
        $result = mysqli_stmt_get_result($stmt);

        // Verificar si se encontró un resultado
        if ($result && mysqli_num_rows($result) > 0) {
            $codigo_postal_data = mysqli_fetch_assoc($result);

            // Armar respuesta de éxito
            $res["success"] = "1";
            $res["mensaje"] = "Código postal encontrado correctamente.";
            $res["datos"] = $codigo_postal_data;

        } else {
            // No se encontró código postal con esos criterios
            $res["mensaje"] = "No se encontró un código postal para la ubicación especificada.";
        }

        // Cerrar el statement
        mysqli_stmt_close($stmt);
    } else {
        // Error en la preparación de la consulta
        $res["mensaje"] = "Error al preparar la consulta SQL.";
    }
}

// Devolver la respuesta en formato JSON
echo json_encode($res, JSON_UNESCAPED_UNICODE);

// Cerrar la conexión
mysqli_close($link);
?>