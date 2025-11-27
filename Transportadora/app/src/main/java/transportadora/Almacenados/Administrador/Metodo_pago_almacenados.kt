package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class MetodoPago(
    val id_metodo_pago: Int,
    val descripcion: String
)

object Metodo_pago_almacenados {
    suspend fun obtenerMetodosPago(): List<MetodoPago> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_metodos_pago.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<MetodoPago>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        MetodoPago(
                            id_metodo_pago = obj.optInt("id_metodo_pago"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

