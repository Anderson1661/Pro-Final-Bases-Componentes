package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Tipo_identificacion
import transportadora.Network.ApiHelper

object Tipo_identificacion_almacenado {
    suspend fun obtener_tipos_identificacion(): List<Tipo_identificacion> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_tipo_identificacion.php"
        val response = ApiHelper.getRequest(url) // devuelve el JSON como String

        val lista = mutableListOf<Tipo_identificacion>()
        val json = JSONObject(response)
        // Manejar caso "no hay registros" (success == "1" y mensaje)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Tipo_identificacion(
                            id_tipo_identificacion = obj.optInt("id_tipo_identificacion"),
                            descripcion = obj.optString("descripcion")
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