package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class RutaSimple(
    val id_ruta: Int,
    val descripcion: String
)

object Ruta_almacenados {
    suspend fun obtenerRutas(): List<RutaSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_rutas.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<RutaSimple>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        RutaSimple(
                            id_ruta = obj.optInt("id_ruta"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

