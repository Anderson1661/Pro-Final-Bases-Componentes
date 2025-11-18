package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

object Registrar_almacenados {
    suspend fun registrarConductor(
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        correo: String,
        idGenero: Int,
        idNacionalidad: Int,
        codigoPostal: String,
        tel1: String,
        tel2: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = ApiConfig.BASE_URL + "consultas/conductor/registrar_conductor.php"
            val params = mapOf(
                "id_tipo_identificacion" to idTipoIdentificacion.toString(),
                "identificacion" to identificacion,
                "nombre" to nombre,
                "direccion" to direccion,
                "correo" to correo,
                "id_genero" to idGenero.toString(),
                "id_pais_nacionalidad" to idNacionalidad.toString(),
                "codigo_postal" to codigoPostal,
                "tel1" to tel1,
                "tel2" to tel2
            )

            val response = ApiHelper.postRequest(url, params)
            val json = JSONObject(response)
            return@withContext json.optString("success") == "1"
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
