package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Administrador.Pais

object Pais_almacenados {
    suspend fun obtenerPaises(): List<Pais> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_paises.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Pais>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Pais(
                            id_pais = obj.optInt("id_pais"),
                            nombre = obj.optString("nombre")
                        )
                    )
                }
            }
        }
        lista
    }
}