<?php
/**
 * Script de prueba de conexión a la base de datos.
 * 
 * Este archivo se utiliza para verificar si la configuración de conexión
 * es correcta y si el servidor de base de datos es accesible.
 */

include('config/conexion.php'); // Incluye el archivo de configuración de conexión

// Intenta establecer la conexión
$link = Conectar();

if ($link) {
    // Si la conexión es exitosa, muestra un mensaje de éxito con un emoji
    echo "✅ Conexión exitosa a la base de datos";
} else {
    // Si falla (aunque la función Conectar suele lanzar excepción), muestra error
    echo "❌ Error de conexión";
}

// Cierra la conexión
mysqli_close($link);
?>