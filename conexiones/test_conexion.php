<?php
include('config/conexion.php');
$link = Conectar();
if ($link) {
    echo "✅ Conexión exitosa a la base de datos";
} else {
    echo "❌ Error de conexión";
}
mysqli_close($link);
?>