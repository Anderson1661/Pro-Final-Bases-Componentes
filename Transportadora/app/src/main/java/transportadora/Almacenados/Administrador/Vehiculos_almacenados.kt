package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Vehiculo
import transportadora.Network.ApiHelper

object Vehiculos_almacenados {
    suspend fun obtenerVehiculos(): List<Vehiculo> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_vehiculos.php"
        val response = ApiHelper.getRequest(url) // Cambiar a GET si es necesario

        val lista = mutableListOf<Vehiculo>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Vehiculo(
                            placa = obj.optString("placa"),
                            linea_vehiculo = obj.optString("linea"),
                            marca = obj.optString("marca")
                        )
                    )
                }
            }
        }
        lista
    }
}