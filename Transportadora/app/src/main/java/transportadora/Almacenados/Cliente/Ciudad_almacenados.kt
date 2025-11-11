package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Network.ApiHelper

object Ciudad_almacenados {
    suspend fun obtenerCiudades(id_pais: Int, departamento: String): List<Ciudad> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/principal/consultar_ciudades.php"
        val params = mapOf(
            "id_pais" to id_pais.toString(),
            "departamento" to departamento
        )
        val response = ApiHelper.postRequest(url, params)

        val lista = mutableListOf<Ciudad>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Ciudad(
                            id_codigo_postal = obj.optString("id_codigo_postal"),
                            nombre = obj.optString("nombre")
                        )
                    )
                }
            }
        }
        lista
    }
}
