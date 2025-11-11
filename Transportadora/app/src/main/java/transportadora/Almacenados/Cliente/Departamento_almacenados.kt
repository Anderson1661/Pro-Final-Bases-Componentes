package transportadora.Almacenados.Cliente

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Cliente.Departamento
import transportadora.Network.ApiHelper

object Departamento_almacenados {
    suspend fun obtenerDepartamentos(id_pais: Int): List<Departamento> = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/cliente/principal/consultar_departamentos.php"
        val params = mapOf("id_pais" to id_pais.toString())
        val response = ApiHelper.postRequest(url, params)

        val lista = mutableListOf<Departamento>()
        val json = JSONObject(response)
        if (json.optString("success") == "1") {
            val datos = json.optJSONArray("datos")
            if (datos != null) {
                for (i in 0 until datos.length()) {
                    val obj = datos.getJSONObject(i)
                    lista.add(
                        Departamento(
                            nombre = obj.optString("nombre")
                        )
                    )
                }
            }
        }
        lista
    }
}
