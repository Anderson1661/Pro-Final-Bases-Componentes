package transportadora.Modelos

data class HistorialServicio(
    val id_ruta: Int,
    val fecha_hora_reserva: String,
    val direccion_origen: String,
    val ciudad_origen: String,
    val direccion_destino: String,
    val ciudad_destino: String,
    val tipo_servicio: String,
    val estado_servicio: String,
    val metodo_pago: String
)
