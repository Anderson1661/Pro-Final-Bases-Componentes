package transportadora.Modelos.Conductor

data class HistorialServicio(
    val id_ruta: Int,
    val fecha_inicio: String,
    val direccion_origen: String,
    val ciudad_origen: String,
    val direccion_destino: String,
    val ciudad_destino: String,
    val tipo_servicio: String,
    val estado: String,
    val metodo_pago: String
)
