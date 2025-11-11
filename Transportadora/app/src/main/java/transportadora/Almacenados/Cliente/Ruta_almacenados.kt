package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Ruta
import transportadora.Network.ApiHelper
import kotlin.String

object Ruta_almacenados {
    suspend fun Crear_ruta(id_cliente: String): Ruta? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/ruta/crear_ruta.php"
        val params = mapOf("id_cliente" to id_cliente)
        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONObject("datos")
            if (datos != null) {
                return@withContext Ruta(
                    id_ruta = datos.optString("id_ruta"),
                    direccion_origen = datos.optString("direccion_origen"),
                    direccion_destino = datos.optString("direccion_destino"),
                    id_codigo_postal_origen = datos.optString("id_codigo_postal_origen"),
                    id_codigo_postal_destino = datos.optString("id_codigo_postal_destino"),
                    distancia_km = datos.optString("distancia_km"),
                    fecha_hora_reserva = datos.optString("fecha_hora_reserva"),
                    fecha_hora_origen = datos.optString("fecha_hora_origen"),
                    fecha_hora_destino = datos.optString("fecha_hora_destino"),
                    id_conductor = datos.optString("id_conductor"),
                    id_tipo_servicio = datos.optString("id_tipo_servicio"),
                    id_cliente = datos.optString("id_cliente"),
                    id_estado_servicio = datos.optString("id_estado_servicio"),
                    id_categoria_servicio = datos.optString("id_categoria_servicio"),
                    id_metodo_pago = datos.optString("id_metodo_pago"),
                    total = datos.optString("total"),
                    pago_conductor = datos.optString("pago_conductor"),
                )
            }
        }
        return@withContext null
    }
}
