package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.Linea_vehiculo
import transportadora.Network.ApiHelper

object Linea_almacenados {
    suspend fun obtenerLineas(id_marca: Int): List<Linea_vehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/vehiculo/consultar_linea.php"
        val params = mapOf("id_marca" to id_marca.toString())
        val response = ApiHelper.postRequest(url, params)

        val lista = mutableListOf<Linea_vehiculo>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Linea_vehiculo(
                            id_marca = id_marca, // Usamos el id_marca que ya tenemos
                            linea = obj.optString("linea")
                        )
                    )
                }
            }
        }
        lista
    }
}