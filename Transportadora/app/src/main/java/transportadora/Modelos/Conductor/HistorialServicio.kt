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
    val metodo_pago: String,

    val id_cliente: Int, // Para referencia si se necesita
    val nombre_cliente: String, // Nombre del cliente
    var id_estado: Int,
    val pago_conductor: Float, // Pago

    // CAMPOS DE DETALLE SENSIBLE (Se llenan al aceptar)
    var telefonos_cliente: List<String> = emptyList(),
    var nombres_pasajeros: List<String> = emptyList()
)
