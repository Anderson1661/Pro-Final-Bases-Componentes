package transportadora.Network

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ApiHelper {
    private fun encodeParams(params: Map<String, String>): String {
        return params.entries.joinToString("&") { (k, v) ->
            java.net.URLEncoder.encode(k, "UTF-8") + "=" + java.net.URLEncoder.encode(v, "UTF-8")
        }
    }

    fun postRequest(urlString: String, params: Map<String, String>): String {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")

        val body = encodeParams(params).toByteArray(Charsets.UTF_8)
        connection.outputStream.use { it.write(body) }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        return response
    }

    fun getRequest(urlString: String): String {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        return response
    }
}
