package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class EstadoVehiculo(
    val id_estado_vehiculo: Int,
    val descripcion: String
)

object Estado_vehiculo_almacenados {
    suspend fun obtenerEstadosVehiculo(): List<EstadoVehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_estados_vehiculo.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<EstadoVehiculo>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        EstadoVehiculo(
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

