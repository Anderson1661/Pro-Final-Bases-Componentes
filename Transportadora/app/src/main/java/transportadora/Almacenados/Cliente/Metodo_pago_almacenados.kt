package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Metodo_pago
import transportadora.Network.ApiHelper

object Metodo_pago_almacenados {
    suspend fun obtener_metodo_pago(): List<Metodo_pago> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/principal/consultar_metodo_pago.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Metodo_pago>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Metodo_pago(
                            id_metodo_pago = obj.optInt("id_metodo_pago"),
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