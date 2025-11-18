package transportadora.Modelos.Conductor

data class Datos_perfil_conductor(
    val id_conductor: Int,
    val identificacion: String,
    val tipo_identificacion: String,
    val nombre: String,
    val direccion: String,
    val correo: String,
    val genero: String,
    val nacionalidad: String,
    val pais_residencia: String,
    val departamento: String,
    val ciudad: String,
    val codigo_postal: String,
    val telefonos: List<String>
)
