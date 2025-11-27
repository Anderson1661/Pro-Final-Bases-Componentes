package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class Marca(
    val id_marca: Int,
    val nombre_marca: String
)

object Marca_almacenados {
    suspend fun obtenerMarcas(): List<Marca> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_marcas.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Marca>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Marca(
                            id_marca = obj.optInt("id_marca"),
                            nombre_marca = obj.optString("nombre_marca")
                        )
                    )
                }
            }
        }
        lista
    }
}

