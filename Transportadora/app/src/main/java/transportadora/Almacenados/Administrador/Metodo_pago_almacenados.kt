package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Administrador.Metodo_pago


object Metodo_pago_almacenados {
    suspend fun obtenerMetodosPago(): List<Metodo_pago> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_metodos_pago.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Metodo_pago>()
        val json = JSONObject(response)
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
        }
        lista
    }
}


