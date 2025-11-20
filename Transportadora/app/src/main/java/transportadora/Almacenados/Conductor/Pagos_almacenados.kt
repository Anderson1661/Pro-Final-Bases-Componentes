package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.PagoConductor
import transportadora.Network.ApiHelper

object Pagos_almacenados {
    suspend fun obtenerPagos(correo: String): List<PagoConductor> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/servicios/consultar_pagos.php"
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)
        val lista = mutableListOf<PagoConductor>()

        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        PagoConductor(
                            id_ruta = obj.optInt("id_ruta"),
                            fecha_finalizacion = obj.optString("fecha_finalizacion"),
                            nombre_cliente = obj.optString("nombre_cliente"),
                            tipo_servicio = obj.optString("tipo_servicio"),
                            categoria_servicio = obj.optString("categoria_servicio"),
                            metodo_pago = obj.optString("metodo_pago"),
                            total = obj.optDouble("total"),
                            pago_conductor = obj.optDouble("total") * 0.30
                        )
                    )
                }
            }
        }
        lista
    }
}
