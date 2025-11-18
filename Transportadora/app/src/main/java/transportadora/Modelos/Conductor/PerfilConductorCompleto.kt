package transportadora.Modelos.Conductor

data class PerfilConductorCompleto(
    val tipo_identificacion: String,
    val identificacion: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val genero: String,
    val nacionalidad: String,
    val pais_residencia: String,
    val departamento: String,
    val ciudad: String,
    val telefonos: List<String>,
    // Datos del vehículo
    val placa: String,
    val linea_vehiculo: String,
    val modelo: Int,
    val color: String,
    val marca: String,
    val tipo_servicio: String,
    val estado_vehiculo: String,
    val estado_conductor: String,
    val url_foto: String
)