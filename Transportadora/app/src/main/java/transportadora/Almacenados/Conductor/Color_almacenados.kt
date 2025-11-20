package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.Color_vehiculo
import transportadora.Network.ApiHelper

object Color_almacenados {
    suspend fun obtener_colores(): List<Color_vehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/vehiculo/consultar_colores.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Color_vehiculo>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Color_vehiculo(
                            id_color = obj.optInt("id_color"),
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