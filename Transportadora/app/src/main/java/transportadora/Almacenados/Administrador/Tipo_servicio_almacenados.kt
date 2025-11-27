package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Administrador.Tipo_servicio

object Tipo_servicio_almacenados {
    suspend fun obtenerTiposServicio(): List<Tipo_servicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_tipos_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Tipo_servicio>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Tipo_servicio(
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

