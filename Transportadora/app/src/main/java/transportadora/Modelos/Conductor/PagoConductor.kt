package transportadora.Modelos.Conductor

data class PagoConductor(
    val id_ruta: Int,
    val fecha_finalizacion: String,
    val nombre_cliente: String,
    val tipo_servicio: String,
    val categoria_servicio: String,
    val metodo_pago: String,
    val total: Double,
    val pago_conductor: Double
)
