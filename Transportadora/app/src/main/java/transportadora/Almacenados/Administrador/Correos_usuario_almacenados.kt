package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Correo_usuario
import transportadora.Network.ApiHelper

object Correos_usuario_almacenados {
    suspend fun obtenerCorreosUsuarios(): List<Correo_usuario> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_correo_usuarios.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Correo_usuario>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Correo_usuario(
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