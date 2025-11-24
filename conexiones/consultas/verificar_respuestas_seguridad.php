<?php
/**
 * Script para verificar las respuestas a las preguntas de seguridad.
 * 
 * Se utiliza para validar la identidad del usuario antes de permitirle cambiar la contraseña.
 * Recibe el ID de usuario y 3 pares de pregunta-respuesta.
 */

include('../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

// Verificar que lleguen todos los datos de las 3 preguntas
if (isset($_POST['id_usuario'], $_POST['id_pregunta1'], $_POST['respuesta1'],
          $_POST['id_pregunta2'], $_POST['respuesta2'], $_POST['id_pregunta3'], $_POST['respuesta3'])) {

    $id_usuario = intval($_POST['id_usuario']);
    
    // Organizar las respuestas recibidas en un array
    $preguntas_respuestas = array(
        array(
            'id_pregunta' => intval($_POST['id_pregunta1']),
            'respuesta' => $_POST['respuesta1'] // No se usa trim() para mantener el valor literal
        ),
        array(
            'id_pregunta' => intval($_POST['id_pregunta2']),
            'respuesta' => $_POST['respuesta2']
        ),
        array(
            'id_pregunta' => intval($_POST['id_pregunta3']),
            'respuesta' => $_POST['respuesta3']
        )
    );

    $respuestas_correctas = 0;
    
    // Consulta preparada para verificar cada respuesta individualmente
    // Se busca coincidencia exacta (case-sensitive dependiendo de la collation de la BD)
    $sql_check = "SELECT COUNT(*) AS count_match 
                  FROM respuestas_seguridad 
                  WHERE id_usuario = ? AND id_pregunta = ? AND respuesta_pregunta = ?"; 

    $stmt = mysqli_prepare($link, $sql_check);

    // Iterar sobre cada pregunta recibida
    foreach ($preguntas_respuestas as $pr) {
        if ($pr['id_pregunta'] > 0) {
            
            mysqli_stmt_bind_param($stmt, "iis", $id_usuario, $pr['id_pregunta'], $pr['respuesta']);
            
            if (mysqli_stmt_execute($stmt)) {
                $result = mysqli_stmt_get_result($stmt);
                $row = mysqli_fetch_assoc($result);
                
                // Si hay coincidencia, incrementar contador
                if ($row['count_match'] == 1) {
                    $respuestas_correctas++;
                }
            }
        }
    }
    
    mysqli_stmt_close($stmt);

    // Validación final: Las 3 respuestas deben ser correctas
    if ($respuestas_correctas == 3) {
        $res["success"] = "1";
        $res["mensaje"] = "Respuestas verificadas con éxito. Ahora puede cambiar su contraseña.";
    } else {
        $res["success"] = "0";
        $res["mensaje"] = "Una o más respuestas de seguridad son incorrectas. Intente de nuevo.";
    }

} 

echo json_encode($res);
mysqli_close($link);
?>