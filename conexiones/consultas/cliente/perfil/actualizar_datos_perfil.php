<?php
// Incluir el archivo de conexión a la base de datos
// CORRECCIÓN 1: Se corrigió la ruta. 
// Para ir de '/consultas/cliente/perfil/' a '/config/', solo se necesitan tres '../'.
include('../../../config/conexion.php');
$link = Conectar();

// Establecer la cabecera para la respuesta JSON
header('Content-Type: application/json; charset=utf-8');
// Inicializar la respuesta
$res = array("success" => "0", "mensaje" => "Parámetros incompletos o incorrectos.");

// Lista de parámetros requeridos (se asume que todos son enviados)
$required_params = [
    'id_cliente', 'id_tipo_identificacion', 'identificacion', 'nombre', 
    'direccion', 'correo', 'id_genero', 'id_nacionalidad', 'codigo_postal',
    'tel1', 'tel2' // tel1 y tel2 pueden ser vacíos, pero deben estar presentes
];

// Comprobar que todos los parámetros estén presentes en $_POST
$all_set = true;
foreach ($required_params as $param) {
    if (!isset($_POST[$param])) {
        $all_set = false;
        break;
    }
}

if ($all_set) {
    // 1. Obtener y limpiar los datos
    $id_cliente = (int)$_POST['id_cliente'];
    $id_tipo_identificacion = (int)$_POST['id_tipo_identificacion'];
    $identificacion = trim($_POST['identificacion']);
    $nombre = trim($_POST['nombre']);
    $direccion = trim($_POST['direccion']);
    $correo = trim($_POST['correo']);
    $id_genero = (int)$_POST['id_genero'];
    $id_nacionalidad = (int)$_POST['id_nacionalidad'];
    $codigo_postal = trim($_POST['codigo_postal']);
    $tel1 = trim($_POST['tel1']);
    $tel2 = trim($_POST['tel2']);

    // Iniciar transacción para asegurar que todo se actualice o nada
    mysqli_begin_transaction($link);
    $success_transaction = true;
    
    try {
        // --- 2. Actualizar la tabla CLIENTE ---
        $sql_cliente = "
            UPDATE cliente 
            SET 
                id_tipo_identificacion = ?,
                identificacion = ?,
                nombre = ?,
                direccion = ?,
                correo = ?,
                id_genero = ?,
                id_pais_nacionalidad = ?,
                codigo_postal = ?
            WHERE id_cliente = ?
        ";
        
        $stmt_cliente = mysqli_prepare($link, $sql_cliente);
        
        // CORRECCIÓN 2: El string de tipificación era "issssisss" y se corrigió a "issssisii"
        // Los tipos son: int, string, string, string, string, int, int, string, int (i s s s s i i s i)
        // Se asume que codigo_postal es string (s) y id_cliente es int (i)
        mysqli_stmt_bind_param(
            $stmt_cliente, 
            "issssisii", 
            $id_tipo_identificacion, 
            $identificacion, 
            $nombre, 
            $direccion, 
            $correo, 
            $id_genero, 
            $id_nacionalidad, 
            $codigo_postal, 
            $id_cliente
        );
        
        if (!mysqli_stmt_execute($stmt_cliente)) {
            $success_transaction = false;
            $res["mensaje"] = "Error al actualizar datos del cliente: " . mysqli_stmt_error($stmt_cliente);
        }
        mysqli_stmt_close($stmt_cliente);


        // --- 3. Actualizar la tabla TELEFONO_CLIENTE ---
        if ($success_transaction) {
            // A. Eliminar todos los teléfonos existentes del cliente
            $sql_del_tel = "DELETE FROM telefono_cliente WHERE id_cliente = ?";
            $stmt_del = mysqli_prepare($link, $sql_del_tel);
            mysqli_stmt_bind_param($stmt_del, "i", $id_cliente);
            if (!mysqli_stmt_execute($stmt_del)) {
                $success_transaction = false;
                $res["mensaje"] = "Error al eliminar teléfonos existentes: " . mysqli_stmt_error($stmt_del);
            }
            mysqli_stmt_close($stmt_del);
        }

        if ($success_transaction) {
            // B. Insertar tel1 si no está vacío
            if (!empty($tel1)) {
                // Se cambió ON DUPLICATE KEY UPDATE por un simple INSERT, 
                // ya que se borran todos los teléfonos antes (DELETE FROM).
                $sql_ins_tel1 = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (?, ?)";
                $stmt_ins1 = mysqli_prepare($link, $sql_ins_tel1);
                // Usamos 's' para telefono ya que puede ser VARCHAR/BIGINT en la BD
                mysqli_stmt_bind_param($stmt_ins1, "is", $id_cliente, $tel1); 
                if (!mysqli_stmt_execute($stmt_ins1)) {
                    $success_transaction = false;
                    $res["mensaje"] = "Error al insertar teléfono 1: " . mysqli_stmt_error($stmt_ins1);
                }
                mysqli_stmt_close($stmt_ins1);
            }
        }
        
        if ($success_transaction) {
            // C. Insertar tel2 si no está vacío
            if (!empty($tel2)) {
                // Se cambió ON DUPLICATE KEY UPDATE por un simple INSERT.
                $sql_ins_tel2 = "INSERT INTO telefono_cliente (id_cliente, telefono) VALUES (?, ?)";
                $stmt_ins2 = mysqli_prepare($link, $sql_ins_tel2);
                mysqli_stmt_bind_param($stmt_ins2, "is", $id_cliente, $tel2);
                if (!mysqli_stmt_execute($stmt_ins2)) {
                    $success_transaction = false;
                    $res["mensaje"] = "Error al insertar teléfono 2: " . mysqli_stmt_error($stmt_ins2);
                }
                mysqli_stmt_close($stmt_ins2);
            }
        }


        // --- 4. Finalizar Transacción ---
        if ($success_transaction) {
            mysqli_commit($link);
            $res["success"] = "1";
            $res["mensaje"] = "Perfil actualizado correctamente.";
        } else {
            mysqli_rollback($link);
            // El mensaje de error ya se estableció en la sección que falló
        }

    } catch (Exception $e) {
        mysqli_rollback($link);
        $res["mensaje"] = "Excepción: " . $e->getMessage();
        error_log("Error al actualizar perfil: " . $e->getMessage());
    }

}

echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>