package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Estado_conductor
import transportadora.Network.ApiHelper

object Estados_conductor_almacenados {
    suspend fun obtenerEstadosConductor(): List<Estado_conductor> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_estados_conductor.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Estado_conductor>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Estado_conductor(
                            id_estado_conductor = obj.optInt("id_estado_conductor"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}