package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.HistorialServicio
import transportadora.Network.ApiHelper

object Servicios_conductor_almacenados {
    // Dentro de object Servicios_conductor_almacenados

    suspend fun obtenerServiciosConductor(correo: String): List<HistorialServicio> = withContext(Dispatchers.IO) {

        val codigoPostal = obtenerCodigoPostalConductor(correo)

        if (codigoPostal.isNullOrEmpty()) {
            // Si no se encuentra el CP, retornamos lista vacía
            return@withContext emptyList<HistorialServicio>()
        }

        // 2. CONSULTAR SERVICIOS PENDIENTES EN ESE CÓDIGO POSTAL PARA HOY
        val url = ApiConfig.BASE_URL + "consultas/conductor/servicios/consultar_servicios_pendientes.php" // Nuevo Endpoint
        val params = mapOf("codigo_postal" to codigoPostal) // Usamos el CP como parámetro

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
                            // Usamos fecha_inicio que viene del PHP (fecha_hora_reserva)
                            fecha_inicio = obj.optString("fecha_inicio"),
                            direccion_origen = obj.optString("direccion_origen"),
                            ciudad_origen = obj.optString("ciudad_origen"),
                            direccion_destino = obj.optString("direccion_destino"),
                            ciudad_destino = obj.optString("ciudad_destino"),
                            tipo_servicio = obj.optString("tipo_servicio"),
                            estado = obj.optString("estado"),
                            metodo_pago = obj.optString("metodo_pago")
                        )
                    )
                }
            }
        }
        lista
    }

    suspend fun obtenerCodigoPostalConductor(correo: String): String? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/servicios/consultar_cp_conductor.php" // Nuevo Endpoint
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        // Verificar si tuvo éxito y si existe el campo codigo_postal
        return@withContext if (json.optString("success") == "1") {
            json.optString("codigo_postal")
        } else {
            null
        }
    }
}
