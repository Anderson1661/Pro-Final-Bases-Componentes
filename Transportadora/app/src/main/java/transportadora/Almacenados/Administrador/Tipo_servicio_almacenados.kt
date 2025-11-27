package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class TipoServicio(
    val id_tipo_servicio: Int,
    val descripcion: String
)

object Tipo_servicio_almacenados {
    suspend fun obtenerTiposServicio(): List<TipoServicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_tipos_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<TipoServicio>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        TipoServicio(
                            id_tipo_servicio = obj.optInt("id_tipo_servicio"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

