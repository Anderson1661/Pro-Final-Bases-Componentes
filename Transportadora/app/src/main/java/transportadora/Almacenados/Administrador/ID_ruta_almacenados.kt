package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Id_Ruta
import transportadora.Network.ApiHelper

object ID_ruta_almacenados {
    suspend fun obtenerIDrutas(): List<Id_Ruta> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/datos/consultar_id_rutas.php"
        val response = ApiHelper.getRequest(url)

        val lista = mutableListOf<Id_Ruta>()
        val json = JSONObject(response)

        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Id_Ruta(
                            id_ruta = obj.optInt("id_ruta")
                        )
                    )
                }
            }
        }
        lista
    }
}