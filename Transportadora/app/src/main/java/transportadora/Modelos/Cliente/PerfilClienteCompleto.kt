package transportadora.Modelos.Cliente

data class PerfilClienteCompleto(
    val tipo_identificacion: String,
    val identificacion: String,
    val nombre: String,
    val correo: String,
    val direccion: String,
    val nacionalidad: String,
    val pais_residencia: String,
    val departamento: String,
    val ciudad: String,
    val telefonos: List<String>
)
