package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Network.ApiHelper
import transportadora.Modelos.Administrador.AdministradorSimple

object Administrador_simple_almacenados {
    suspend fun obtenerAdministradores(): List<AdministradorSimple> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_administradores_simple.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<AdministradorSimple>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        AdministradorSimple(
                            id_administrador = obj.optInt("id_administrador"),
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