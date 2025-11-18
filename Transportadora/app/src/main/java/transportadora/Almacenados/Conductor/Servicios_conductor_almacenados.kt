package transportadora.Almacenados.Conductor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Conductor.HistorialServicio
import transportadora.Modelos.Conductor.ConductorData
import transportadora.Network.ApiHelper
object Servicios_conductor_almacenados {

    // --- 2. MODIFICACIÓN: Usa ConductorData y envía ID y CP al PHP ---

    // Actualiza la función obtenerServiciosConductor para recibir el idConductor
    suspend fun obtenerServiciosConductor(email: String, idConductor: Int?): List<HistorialServicio> {
        return withContext(Dispatchers.IO) {
            try {
                // Primero obtener el código postal
                val conductorData = obtenerDatosConductor(email)
                if (conductorData == null) {
                    return@withContext emptyList()
                }

                val codigoPostal = conductorData.codigoPostal

                // Validar que tenemos idConductor
                if (idConductor == null) {
                    return@withContext emptyList()
                }

                val url = ApiConfig.BASE_URL+"consultas/conductor/servicios/consultar_servicios_pendientes.php"
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("codigo_postal", codigoPostal)
                    .add("id_conductor", idConductor.toString()) // ✅ AÑADIR ESTE PARÁMETRO
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val jsonObject = JSONObject(jsonResponse ?: "")

                    if (jsonObject.getInt("success") == 1) {
                        val serviciosArray = jsonObject.getJSONArray("datos")
                        val serviciosList = mutableListOf<HistorialServicio>()

                        for (i in 0 until serviciosArray.length()) {
                            val servicioJson = serviciosArray.getJSONObject(i)
                            val servicio = HistorialServicio(
                                id_ruta = servicioJson.getInt("id_ruta"),
                                fecha_inicio = servicioJson.getString("fecha_inicio"),
                                direccion_origen = servicioJson.getString("direccion_origen"),
                                ciudad_origen = servicioJson.getString("ciudad_origen"),
                                direccion_destino = servicioJson.getString("direccion_destino"),
                                ciudad_destino = servicioJson.getString("ciudad_destino"),
                                tipo_servicio = servicioJson.getString("tipo_servicio"),
                                estado = servicioJson.getString("estado"),
                                metodo_pago = servicioJson.getString("metodo_pago"),
                                id_cliente = servicioJson.getInt("id_cliente"),
                                nombre_cliente = servicioJson.getString("nombre_cliente"),
                                id_estado = servicioJson.getInt("id_estado"),
                                pago_conductor = servicioJson.getDouble("pago_conductor").toFloat(), // Usar Float
                                telefonos_cliente = emptyList(),
                                nombres_pasajeros = emptyList()
                            )
                            serviciosList.add(servicio)
                        }
                        return@withContext serviciosList
                    }
                }
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // Añade esta función en Servicios_conductor_almacenados.kt
    suspend fun obtenerDetallesServicio(idRuta: Int): Pair<List<String>, List<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = ApiConfig.BASE_URL+"consultas/conductor/servicios/consultar_detalles_ruta.php"
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("id_ruta", idRuta.toString())
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val jsonObject = JSONObject(jsonResponse ?: "")

                    if (jsonObject.getInt("success") == 1) {
                        val telefonosArray = jsonObject.getJSONArray("telefonos")
                        val pasajerosArray = jsonObject.getJSONArray("pasajeros")

                        val telefonos = mutableListOf<String>()
                        val pasajeros = mutableListOf<String>()

                        for (i in 0 until telefonosArray.length()) {
                            telefonos.add(telefonosArray.getString(i))
                        }

                        for (i in 0 until pasajerosArray.length()) {
                            pasajeros.add(pasajerosArray.getString(i))
                        }

                        return@withContext Pair(telefonos, pasajeros)
                    }
                }
                return@withContext Pair(emptyList(), emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Pair(emptyList(), emptyList())
            }
        }
    }

    // --- 3. NUEVA FUNCIÓN QUE REEMPLAZA obtenerCodigoPostalConductor ---
    suspend fun obtenerDatosConductor(correo: String): ConductorData? = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/servicios/consultar_cp_conductor.php"
        val params = mapOf("correo" to correo)
        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        return@withContext if (json.optString("success") == "1") {
            // Se asume que el JSON ahora contiene 'id_conductor' y 'codigo_postal'
            ConductorData(
                idConductor = json.optInt("id_conductor"),
                codigoPostal = json.optString("codigo_postal")
            )
        } else {
            null
        }
    }
    // -------------------------------------------------------------

    // --- 4. MODIFICACIÓN: Acepta idConductor opcional para ACEPTAR ---
    suspend fun actualizarEstadoServicio(idRuta: Int, idEstado: Int, idConductor: Int? = null): Boolean = withContext(Dispatchers.IO) {
        val url = ApiConfig.BASE_URL + "consultas/conductor/servicios/cambiar_estado_ruta.php"

        val params = mutableMapOf(
            "id_ruta" to idRuta.toString(),
            "id_estado" to idEstado.toString()
        )

        // CAMBIO CRÍTICO: Si el estado es ACEPTAR (2), añade el ID del conductor
        if (idEstado == 2 && idConductor != null) {
            params["id_conductor"] = idConductor.toString()
        }

        val response = ApiHelper.postRequest(url, params)

        val json = JSONObject(response)
        return@withContext json.optString("success") == "1"
    }
}