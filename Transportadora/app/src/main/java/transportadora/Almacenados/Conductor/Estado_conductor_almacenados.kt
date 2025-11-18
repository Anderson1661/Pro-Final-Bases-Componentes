package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.Estado_Conductor
import transportadora.Network.ApiHelper
object Estado_conductor_almacenados {
        suspend fun obtenerEstadoConductor(idConductor: Int): Estado_Conductor? = withContext(Dispatchers.IO) {
            val url = ApiConfig.BASE_URL + "consultas/conductor/estado_conductor/consultar_estado_conductor.php"
            val params = mapOf("id_conductor" to idConductor.toString())
            val response = ApiHelper.postRequest(url, params)

            val json = JSONObject(response)
            if (json.optString("success") == "1") {
                val datos = json.optJSONObject("datos")
                if (datos != null) {
                    return@withContext Estado_Conductor(
                        id_estado_conductor = datos.optInt("id_estado_conductor"),
                        descripcion = datos.optString("descripcion")
                    )
                }
            }
            return@withContext null
        }

        suspend fun actualizarEstadoConductor(idConductor: Int, nuevoEstadoId: Int): Boolean = withContext(Dispatchers.IO) {
            val url = ApiConfig.BASE_URL + "consultas/conductor/estado_conductor/actualizar_estado_conductor.php"
            val params = mapOf(
                "id_conductor" to idConductor.toString(),
                "nuevo_estado_id" to nuevoEstadoId.toString()
            )
            val response = ApiHelper.postRequest(url, params)

            val json = JSONObject(response)
            return@withContext json.optString("success") == "1"
        }
}