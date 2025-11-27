package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class EstadoServicio(
    val id_estado_servicio: Int,
    val descripcion: String
)

object Estado_servicio_almacenados {
    suspend fun obtenerEstadosServicio(): List<EstadoServicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_estados_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<EstadoServicio>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        EstadoServicio(
                            id_estado_servicio = obj.optInt("id_estado_servicio"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

