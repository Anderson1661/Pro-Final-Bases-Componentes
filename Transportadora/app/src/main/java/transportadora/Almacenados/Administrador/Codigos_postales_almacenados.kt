package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import java.net.URL
import java.net.URLEncoder
import transportadora.Network.ApiHelper
import transportadora.Modelos.Administrador.Codigo_postal

object Codigos_postales_almacenados {
    suspend fun obtenerCodigosPostales(): List<Codigo_postal> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_codigos_postales.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Codigo_postal>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Codigo_postal(
                            id_codigo_postal = obj.optString("id_codigo_postal"),
                            id_pais = obj.optInt("id_pais"),
                            departamento = obj.optString("departamento"),
                            ciudad = obj.optString("ciudad")
                        )
                    )
                }
            }
        }
        lista
    }
}