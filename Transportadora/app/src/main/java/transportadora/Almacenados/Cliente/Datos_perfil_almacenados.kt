package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Datos_perfil
import transportadora.Network.ApiHelper

object Datos_perfil_almacenados {
    suspend fun obtener_datos_perfil(correo: String): Datos_perfil? = withContext(Dispatchers.IO) {
        try {
            val url = ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_datos_perfil.php"
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

                    return@withContext Datos_perfil(
                        id_cliente = datos.optInt("id_cliente"),
                        identificacion = datos.optString("identificacion"),
                        tipo_identificacion = datos.optString("tipo_identificacion"),
                        nombre = datos.optString("nombre"),
                        direccion = datos.optString("direccion"),
                        correo = datos.optString("correo"),
                        genero = datos.optString("genero"),
                        nacionalidad = datos.optString("nacionalidad"),
                        pais_residencia = datos.optString("pais_residencia"),
                        departamento = datos.optString("departamento"),
                        ciudad = datos.optString("ciudad"),
                        codigo_postal = datos.optString("codigo_postal"),
                        telefonos = telefonosList
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }
}