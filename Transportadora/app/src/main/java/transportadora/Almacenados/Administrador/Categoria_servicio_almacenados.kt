package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Categoria
import transportadora.Network.ApiHelper

object Categoria_servicio_almacenados {
    suspend fun obtenerCategoriasServicio(): List<Categoria> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_categorias.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Categoria>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Categoria(
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


