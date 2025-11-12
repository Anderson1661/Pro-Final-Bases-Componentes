package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.PerfilCliente
import transportadora.Network.ApiHelper

object Perfil_cliente_almacenados {
    suspend fun obtenerPerfil(correo: String): PerfilCliente? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_perfil.php"
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONObject("datos")
            if (datos != null) {
                return@withContext PerfilCliente(
                    direccion = datos.optString("direccion"),
                    departamento = datos.optString("departamento"),
                    ciudad = datos.optString("ciudad")
                )
            }
        }
        return@withContext null
    }
}
