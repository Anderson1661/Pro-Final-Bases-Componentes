package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class LineaSimple(
    val id_linea: String
)

object Linea_por_marca_almacenados {
    suspend fun obtenerLineasPorMarca(idMarca: Int): List<LineaSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_lineas_por_marca.php?id_marca=$idMarca"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<LineaSimple>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        LineaSimple(
                            id_linea = obj.optString("id_linea")
                        )
                    )
                }
            }
        }
        lista
    }
}

