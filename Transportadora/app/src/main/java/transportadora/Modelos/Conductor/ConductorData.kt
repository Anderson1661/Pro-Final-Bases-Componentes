package transportadora.Modelos.Conductor

data class ConductorData(
    val idConductor: Int,
    val codigoPostal: String,
    val idTipoServicio: Int,
    val id_estado_conductor: Int
    )