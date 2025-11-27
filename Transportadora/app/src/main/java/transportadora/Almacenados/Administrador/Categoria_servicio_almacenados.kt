package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class CategoriaServicio(
    val id_categoria_servicio: Int,
    val descripcion: String
)

object Categoria_servicio_almacenados {
    suspend fun obtenerCategoriasServicio(): List<CategoriaServicio> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_categorias_servicio.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<CategoriaServicio>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        CategoriaServicio(
                            id_categoria_servicio = obj.optInt("id_categoria_servicio"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}

