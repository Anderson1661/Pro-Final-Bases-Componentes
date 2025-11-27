package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class PreguntaSimple(
    val id_pregunta: Int,
    val descripcion: String
)

object Pregunta_almacenados {
    suspend fun obtenerPreguntas(): List<PreguntaSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_preguntas_seguridad.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<PreguntaSimple>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        PreguntaSimple(
                            id_pregunta = obj.optInt("id_pregunta"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

