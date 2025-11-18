package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.PerfilConductorCompleto
import transportadora.Network.ApiHelper

object Perfil_conductor_completo_almacenados {
    suspend fun obtenerPerfilCompleto(correo: String): PerfilConductorCompleto? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/perfil/consultar_perfil_completo.php"
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

                return@withContext PerfilConductorCompleto(
                    tipo_identificacion = datos.optString("tipo_identificacion"),
                    identificacion = datos.optString("identificacion"),
                    nombre = datos.optString("nombre"),
                    correo = datos.optString("correo"),
                    direccion = datos.optString("direccion"),
                    genero = datos.optString("genero"),
                    nacionalidad = datos.optString("nacionalidad"),
                    pais_residencia = datos.optString("pais_residencia"),
                    departamento = datos.optString("departamento"),
                    ciudad = datos.optString("ciudad"),
                    telefonos = telefonosList,
                    placa = datos.optString("placa"),
                    linea_vehiculo = datos.optString("linea_vehiculo"),
                    modelo = datos.optInt("modelo"),
                    color = datos.optString("color"),
                    marca = datos.optString("marca"),
                    tipo_servicio = datos.optString("tipo_servicio"),
                    estado_vehiculo = datos.optString("estado_vehiculo"),
                    estado_conductor = datos.optString("estado_conductor"),
                    url_foto = datos.optString("url_foto")
                )
            }
        }
        return@withContext null
    }
}