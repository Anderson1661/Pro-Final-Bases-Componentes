<?php
// consultas/administrador/tablas/telefono_administrador/consultar_tel_administrador.php

include('../../../../config/conexion.php'); // Ajusta la ruta
$link = Conectar();

// Leer datos JSON
$input = json_decode(file_get_contents('php://input'), true);
$id_administrador = isset($input['id_administrador']) ? trim($input['id_administrador']) : '';
$telefono = isset($input['telefono']) ? trim($input['telefono']) : '';

if (empty($id_administrador) || empty($telefono)) {
    echo json_encode(array("success" => "0", "mensaje" => "El ID de administrador y el teléfono son requeridos"));
} else {
    $id_administrador = mysqli_real_escape_string($link, $id_administrador);
    $telefono = mysqli_real_escape_string($link, $telefono);
    
    $sql = "SELECT id_administrador, telefono
            FROM telefono_administrador
            WHERE id_administrador = '$id_administrador' AND telefono = '$telefono'";
            
    $res = mysqli_query($link, $sql);
    
    if (mysqli_num_rows($res) > 0) {
        $row = mysqli_fetch_assoc($res);
        
        // Convertir BIGINT a string en PHP para evitar problemas de precisión en JSON/Kotlin
        $row['telefono'] = (string)$row['telefono']; 
        
        echo json_encode(array(
            "success" => "1", 
            "datos" => $row,
            "mensaje" => "Teléfono de administrador encontrado"
        ));
    } else {
        echo json_encode(array("success" => "0", "mensaje" => "No se encontró el teléfono del administrador"));
    }
}

mysqli_close($link);
?>