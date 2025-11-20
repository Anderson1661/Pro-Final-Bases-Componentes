package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.Marca_vehiculo
import transportadora.Network.ApiHelper

object Marca_almacenados {
    suspend fun obtener_marcas(): List<Marca_vehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/vehiculo/consultar_marca.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Marca_vehiculo>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Marca_vehiculo(
                            id_marca = obj.optInt("id_marca"),
                            nombre_marca = obj.optString("nombre_marca")
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