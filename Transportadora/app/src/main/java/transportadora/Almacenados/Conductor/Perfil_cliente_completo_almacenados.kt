package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.PerfilClienteCompleto
import transportadora.Network.ApiHelper

object Perfil_cliente_completo_almacenados {
    suspend fun obtenerPerfilCompleto(correo: String): PerfilClienteCompleto? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_perfil_completo.php"
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONObject("datos")
            if (datos != null) {
                val telefonosJson = datos.optJSONArray("telefonos")
                val telefonosList = mutableListOf<String>()
                if (telefonosJson != null) {
                    for (i in 0 until telefonosJson.length()) {
                        telefonosList.add(telefonosJson.getString(i))
                    }
                }

                return@withContext PerfilClienteCompleto(
                    tipo_identificacion = datos.optString("tipo_identificacion"),
                    identificacion = datos.optString("identificacion"),
                    nombre = datos.optString("nombre"),
                    correo = datos.optString("correo"),
                    direccion = datos.optString("direccion"),
                    nacionalidad = datos.optString("nacionalidad"),
                    pais_residencia = datos.optString("pais_residencia"),
                    departamento = datos.optString("departamento"),
                    ciudad = datos.optString("ciudad"),
                    telefonos = telefonosList
                )
            }
        }
        return@withContext null
    }
}
