package transportadora.Network

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ApiHelper {
    fun postRequest(urlString: String, params: Map<String, String>): String {
        val connection = URL(urlString).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.outputStream.write(
            params.entries.joinToString("&") { "${it.key}=${it.value}" }
                .toByteArray(Charsets.UTF_8)
        )
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
