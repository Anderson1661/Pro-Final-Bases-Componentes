package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Estado_servicio
import transportadora.Network.ApiHelper

object Estados_servicio_almacenados {
    suspend fun obtenerEstadosConductor(): List<Estado_servicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_estados_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Estado_servicio>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Estado_servicio(
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