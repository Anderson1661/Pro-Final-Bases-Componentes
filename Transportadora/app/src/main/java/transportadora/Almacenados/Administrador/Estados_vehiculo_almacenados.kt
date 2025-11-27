package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Estado_vehiculo
import transportadora.Network.ApiHelper

object Estados_vehiculo_almacenados {
    suspend fun obtenerEstadosVehiculo(): List<Estado_vehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_estados_vehiculo.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Estado_vehiculo>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Estado_vehiculo(
                            id_estado_vehiculo = obj.optInt("id_estado_vehiculo"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}