package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Tipo_identificacion
import transportadora.Network.ApiHelper

object Tipo_identificacion_almacenados {
    suspend fun obtenerTiposIdentificacion(): List<Tipo_identificacion> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_tipos_identificacion.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Tipo_identificacion>()
        val json = JSONObject(response)

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
        }
        lista
    }
}