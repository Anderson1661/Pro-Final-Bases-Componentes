package transportadora.Modelos.Cliente

data class Ruta(
    val id_ruta: String,
    val direccion_origen: String,
    val direccion_destino: String,
    val id_codigo_postal_origen: String,
    val id_codigo_postal_destino: String,
    val distancia_km: String,
    val fecha_hora_reserva: String,
    val fecha_hora_origen: String,
    val fecha_hora_destino: String,
    val id_conductor: String,
    val id_tipo_servicio: String,
    val id_cliente: String,
    val id_estado_servicio: String,
    val id_categoria_servicio: String,
    val id_metodo_pago: String,
    val total: String,
    val pago_conductor: String
    )