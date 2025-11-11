<?php
include('../../../config/conexion.php');
$link = Conectar();
header('Content-Type: application/json; charset=utf-8');

$res = array("success" => "0", "mensaje" => "Parámetros incompletos");
if (isset($_POST['id_ruta'])) {
	$id_ruta = trim($_POST['id_ruta']);

	// Consulta principal de la ruta
	$sql = "SELECT 
				r.id_ruta,
				r.fecha_hora_origen AS fecha_inicio,
				es.descripcion AS estado,
				r.fecha_hora_reserva,
				r.direccion_origen,
				cp_ori.departamento AS origen_departamento,
				cp_ori.ciudad AS origen_ciudad,
				p_ori.nombre AS origen_pais,
				r.direccion_destino,
				cp_dest.departamento AS destino_departamento,
				cp_dest.ciudad AS destino_ciudad,
				p_dest.nombre AS destino_pais,
				ts.descripcion AS tipo_servicio,
				cs.descripcion AS categoria_servicio,
				cs.valor_km AS precio_km,
				r.total,
				mp.descripcion AS metodo_pago,
				r.fecha_hora_destino AS fecha_entrega,
				c.nombre AS nombre_conductor,
				c.placa_vehiculo
			FROM ruta r
			LEFT JOIN estado_servicio es ON r.id_estado_servicio = es.id_estado_servicio
			LEFT JOIN tipo_servicio ts ON r.id_tipo_servicio = ts.id_tipo_servicio
			LEFT JOIN categoria_servicio cs ON r.id_categoria_servicio = cs.id_categoria_servicio
			LEFT JOIN metodo_pago mp ON r.id_metodo_pago = mp.id_metodo_pago
			LEFT JOIN conductor c ON r.id_conductor = c.id_conductor
			LEFT JOIN codigo_postal cp_ori ON r.id_codigo_postal_origen = cp_ori.id_codigo_postal
			LEFT JOIN pais p_ori ON cp_ori.id_pais = p_ori.id_pais
			LEFT JOIN codigo_postal cp_dest ON r.id_codigo_postal_destino = cp_dest.id_codigo_postal
	    LEFT JOIN pais p_dest ON cp_dest.id_pais = p_dest.id_pais
	    WHERE r.id_ruta = ? LIMIT 1";

    $stmt = mysqli_prepare($link, $sql);
	if ($stmt) {
		mysqli_stmt_bind_param($stmt, "i", $id_ruta);
		mysqli_stmt_execute($stmt);
		$result = mysqli_stmt_get_result($stmt);

		if ($result && mysqli_num_rows($result) > 0) {
			$row = mysqli_fetch_assoc($result);
			// Obtener cantidad de pasajeros
			$sql_count = "SELECT COUNT(*) AS cnt FROM pasajero_ruta WHERE id_ruta = ?";
			$stmt_cnt = mysqli_prepare($link, $sql_count);
			$cantidad_pasajeros = 0;
			if ($stmt_cnt) {
				mysqli_stmt_bind_param($stmt_cnt, "i", $id_ruta);
				mysqli_stmt_execute($stmt_cnt);
				$res_cnt = mysqli_stmt_get_result($stmt_cnt);
				if ($res_cnt) {
					$r_cnt = mysqli_fetch_assoc($res_cnt);
					$cantidad_pasajeros = (int)$r_cnt['cnt'];
				}
				mysqli_stmt_close($stmt_cnt);
			}

			// Obtener hasta 4 nombres de pasajeros (orden de inserción)
			$sql_pas = "SELECT nombre_pasajero FROM pasajero_ruta WHERE id_ruta = ? LIMIT 4";
			$stmt_pas = mysqli_prepare($link, $sql_pas);
			$pasajeros = array();
			if ($stmt_pas) {
				mysqli_stmt_bind_param($stmt_pas, "i", $id_ruta);
				mysqli_stmt_execute($stmt_pas);
				$res_pas = mysqli_stmt_get_result($stmt_pas);
				while ($r_pas = mysqli_fetch_assoc($res_pas)) {
					$pasajeros[] = $r_pas['nombre_pasajero'];
				mysqli_stmt_close($stmt_pas);
			}

			// Rellenar pasajeros 1..4 con null si faltan
			for ($i = 0; $i < 4; $i++) {
				$key = 'nombre_pasajero' . ($i + 1);
				$row[$key] = isset($pasajeros[$i]) ? $pasajeros[$i] : null;
			}

			$row['cantidad_pasajeros'] = $cantidad_pasajeros;
			// Normalizar nombres de campos de salida
			$datos = array(
				'id_ruta' => (int)$row['id_ruta'],
				'fecha_inicio' => $row['fecha_inicio'],
				'estado' => $row['estado'],
				'fecha_reserva' => $row['fecha_hora_reserva'],
				'direccion_origen' => $row['direccion_origen'],
				'origen_pais' => $row['origen_pais'],
				'origen_departamento' => $row['origen_departamento'],
				'origen_ciudad' => $row['origen_ciudad'],
				'tipo_servicio' => $row['tipo_servicio'],
				'cantidad_pasajeros' => $row['cantidad_pasajeros'],
				'nombre_pasajero1' => $row['nombre_pasajero1'],
				'nombre_pasajero2' => $row['nombre_pasajero2'],
				'nombre_pasajero3' => $row['nombre_pasajero3'],
				'nombre_pasajero4' => $row['nombre_pasajero4'],
				'categoria_servicio' => $row['categoria_servicio'],
				'direccion_destino' => $row['direccion_destino'],
				'destino_pais' => $row['destino_pais'],
				'destino_departamento' => $row['destino_departamento'],
				'destino_ciudad' => $row['destino_ciudad'],
				'precio_km' => $row['precio_km'],
				'total' => $row['total'],
				'metodo_pago' => $row['metodo_pago'],
				'fecha_entrega' => $row['fecha_entrega'],
				'nombre_conductor' => $row['nombre_conductor'],
				'placa_vehiculo' => $row['placa_vehiculo']
			);

			$res["datos"] = $datos;
			$res["success"] = "1";
			$res["mensaje"] = "Datos encontrados";
		} else {
			$res["mensaje"] = "No se encontró la ruta.";
		}
		mysqli_stmt_close($stmt);
	} else {
		$res["mensaje"] = "Error al preparar la consulta.";
	}

}
echo json_encode($res, JSON_UNESCAPED_UNICODE);
mysqli_close($link);
?>
