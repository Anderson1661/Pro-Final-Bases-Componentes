package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Genero
import transportadora.Network.ApiHelper

object Generos_almacenados {
    suspend fun obtenerGeneros(): List<Genero> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_generos.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Genero>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Genero(
                            id_genero = obj.optInt("id_genero"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}