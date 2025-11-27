package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Cliente.Categoria_servicio

object Categorias_almacenados {

    suspend fun obtener_categoria_servicio(): List<Categoria_servicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/principal/consultar_categoria_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Categoria_servicio>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Categoria_servicio(
                            id_categoria_servicio = obj.optInt("id_categoria_servicio"),
                            descripcion = obj.optString("descripcion"),
                            valor_km = obj.optDouble("valor_km")
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