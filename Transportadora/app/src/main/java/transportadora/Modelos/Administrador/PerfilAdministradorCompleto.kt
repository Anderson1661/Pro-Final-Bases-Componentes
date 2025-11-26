package transportadora.Modelos.Administrador

data class PerfilAdministradorCompleto(
    val tipo_identificacion: String,
    val identificacion: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val pais_residencia: String,
    val departamento: String,
    val ciudad: String,
    val telefonos: List<String>
)
