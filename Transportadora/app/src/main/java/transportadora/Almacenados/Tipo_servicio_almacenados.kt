package transportadora.Almacenados

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Tipo_servicio

object Tipo_servicio_almacenados {
    suspend fun obtener_tipo_servicio(): List<Tipo_servicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/consultar_tipo_servicio.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Tipo_servicio>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
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
        } else {
            // Opcional: lanzar excepción o retornar lista vacía
        }
        lista
    }


}