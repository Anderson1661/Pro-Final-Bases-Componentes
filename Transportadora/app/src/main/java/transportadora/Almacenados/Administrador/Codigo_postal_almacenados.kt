package transportadora.Almacenados.Administrador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import java.net.URL
import java.net.URLEncoder

object Codigo_postal_almacenados {
    suspend fun obtenerCodigoPostal(pais: String, departamento: String, ciudad: String): String? = withContext(Dispatchers.IO) {
        var codigoPostal: String? = null
        try {
            val url = URL(ApiConfig.BASE_URL + "consultas/administrador/datos/obtener_codigo_postal.php")
            val params = "pais=${URLEncoder.encode(pais, "UTF-8")}" +
                    "&departamento=${URLEncoder.encode(departamento, "UTF-8")}" +
                    "&ciudad=${URLEncoder.encode(ciudad, "UTF-8")}"

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = JSONObject(response)

            if (json.getString("success") == "1") {
                codigoPostal = json.getJSONObject("datos").getString("codigo_postal")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext codigoPostal
    }
}


