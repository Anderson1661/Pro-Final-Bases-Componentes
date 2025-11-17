<?php
include('../config/conexion.php');
$link = Conectar();

header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");

if (isset($_POST['id_usuario'], $_POST['id_pregunta1'], $_POST['respuesta1'],
          $_POST['id_pregunta2'], $_POST['respuesta2'], $_POST['id_pregunta3'], $_POST['respuesta3'])) {

    $id_usuario = intval($_POST['id_usuario']);
    
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
    
    // Consulta para verificar una respuesta (SIN LOWER() para coincidencia exacta)
    $sql_check = "SELECT COUNT(*) AS count_match 
                  FROM respuestas_seguridad 
                  WHERE id_usuario = ? AND id_pregunta = ? AND respuesta_pregunta = ?"; // CORREGIDO: Sin LOWER()

    $stmt = mysqli_prepare($link, $sql_check);

    foreach ($preguntas_respuestas as $pr) {
        if ($pr['id_pregunta'] > 0) {
            
            // Asumiendo que la respuesta vacía es una respuesta incorrecta
            mysqli_stmt_bind_param($stmt, "iis", $id_usuario, $pr['id_pregunta'], $pr['respuesta']);
            
            if (mysqli_stmt_execute($stmt)) {
                $result = mysqli_stmt_get_result($stmt);
                $row = mysqli_fetch_assoc($result);
                
                if ($row['count_match'] == 1) {
                    $respuestas_correctas++;
                }
            }
        }
    }
    
    mysqli_stmt_close($stmt);

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