package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.ConductorSimple
import transportadora.Network.ApiHelper

object Conductor_simple_almacenados {
    suspend fun obtenerConductores(): List<ConductorSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_conductores_simple.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<ConductorSimple>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        ConductorSimple(
                            id_conductor = obj.optInt("id_conductor"),
                            nombre = obj.optString("nombre"),
                            correo = obj.optString("correo")
                        )
                    )
                }
            }
        }
        lista
    }
}