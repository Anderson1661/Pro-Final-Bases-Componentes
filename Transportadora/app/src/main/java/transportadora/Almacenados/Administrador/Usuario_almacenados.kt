package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper

data class UsuarioSimple(
    val id_usuario: Int,
    val correo: String
)

object Usuario_almacenados {
    suspend fun obtenerUsuarios(): List<UsuarioSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_usuarios.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<UsuarioSimple>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        UsuarioSimple(
                            id_usuario = obj.optInt("id_usuario"),
                            correo = obj.optString("correo")
                        )
                    )
                }
            }
        }
        lista
    }
}


