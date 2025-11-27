package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class TipoUsuario(
    val id_tipo_usuario: Int,
    val descripcion: String
)

object Tipo_usuario_almacenados {
    suspend fun obtenerTiposUsuario(): List<TipoUsuario> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_tipos_usuario.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<TipoUsuario>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        TipoUsuario(
                            id_tipo_usuario = obj.optInt("id_tipo_usuario"),
                            descripcion = obj.optString("descripcion")
                        )
                    )
                }
            }
        }
        lista
    }
}


