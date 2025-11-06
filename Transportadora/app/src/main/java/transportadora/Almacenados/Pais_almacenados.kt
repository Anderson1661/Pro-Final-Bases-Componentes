package transportadora.Almacenados

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Pais

object Pais_almacenados {
    suspend fun obtenerPaises(): List<Pais> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/consultar_paises.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        // Parseamos la estructura { "datos": [ {id_pais, nombre}, ... ], "success": "1" }
        val lista = mutableListOf<Pais>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
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
        } else {
            // Opcional: lanzar excepción o retornar lista vacía
        }
        lista
    }
}
