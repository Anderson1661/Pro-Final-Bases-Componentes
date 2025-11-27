package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class ClienteSimple(
    val id_cliente: Int,
    val nombre: String
)

object Cliente_almacenados {
    suspend fun obtenerClientes(): List<ClienteSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_clientes.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<ClienteSimple>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        ClienteSimple(
                            id_cliente = obj.optInt("id_cliente"),
                            nombre = obj.optString("nombre")
                        )
                    )
                }
            }
        }
        lista
    }
}


