package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.HistorialServicio
import transportadora.Network.ApiHelper

object Historial_servicio_almacenados {
    suspend fun obtenerHistorial(correo: String): List<HistorialServicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/historial/consultar_historial.php"
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)
        val lista = mutableListOf<HistorialServicio>()

        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        HistorialServicio(
                            id_ruta = obj.optInt("id_ruta"),
                            fecha_hora_reserva = obj.optString("fecha_hora_reserva"),
                            direccion_origen = obj.optString("direccion_origen"),
                            ciudad_origen = obj.optString("ciudad_origen"),
                            direccion_destino = obj.optString("direccion_destino"),
                            ciudad_destino = obj.optString("ciudad_destino"),
                            tipo_servicio = obj.optString("tipo_servicio"),
                            estado_servicio = obj.optString("estado_servicio"),
                            metodo_pago = obj.optString("metodo_pago"),
                            // Nuevo campo
                            url_foto_conductor = obj.optString("url_foto_conductor").takeIf {
                                it != null && it != "null" && it.isNotEmpty()
                            }
                        )
                    )
                }
            }
        }
        lista
    }
}