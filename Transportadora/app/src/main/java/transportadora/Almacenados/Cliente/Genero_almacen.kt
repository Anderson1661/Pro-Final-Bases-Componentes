package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Genero
import transportadora.Network.ApiHelper

object Genero_almacen {
    suspend fun obtener_generos(): List<Genero> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_genero.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Genero>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
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
        } else {
            // Opcional: lanzar excepción o retornar lista vacía
        }
        lista
    }
}