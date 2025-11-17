package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class PreguntaRespuesta(
    val id_pregunta: Int,
    val descripcion: String,
    val respuesta_pregunta: String,
    val orden: Int // Para saber qué pregunta es la 1, 2 o 3
)

data class PreguntasCliente(
    val id_usuario: Int,
    val preguntas: List<PreguntaRespuesta>
)

data class PreguntaCompleta(
    val id: Int,
    val descripcion: String
)

class Act_preguntas_cliente : AppCompatActivity() {

    private var userEmail: String? = null
    private var idUsuarioActual: Int = -1
    private val client = OkHttpClient()

    // Lista de las respuestas y preguntas originales del cliente
    private var preguntasOriginales: List<PreguntaRespuesta> = emptyList()
    // Lista de todas las posibles preguntas (para llenar los Spinners)
    private var listaPreguntasCompletas: List<PreguntaCompleta> = emptyList()

    // Referencias a Vistas
    private lateinit var spinner_pregunta1: Spinner
    private lateinit var spinner_pregunta2: Spinner
    private lateinit var spinner_pregunta3: Spinner
    private lateinit var txt_respuesta1: EditText
    private lateinit var txt_respuesta2: EditText
    private lateinit var txt_respuesta3: EditText
    private lateinit var buttonGuardar: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_preguntas_cliente)

        // 1. CAPTURAR EMAIL DEL INTENT
        userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Email de usuario no encontrado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Inicialización de Vistas
        spinner_pregunta1 = findViewById(R.id.txt_pregunta1)
        spinner_pregunta2 = findViewById(R.id.txt_pregunta2)
        spinner_pregunta3 = findViewById(R.id.txt_pregunta3)

        txt_respuesta1 = findViewById(R.id.txt_respuesta1)
        txt_respuesta2 = findViewById(R.id.txt_respuesta2)
        txt_respuesta3 = findViewById(R.id.txt_respuesta3)

        val buttonDescartar = findViewById<Button>(R.id.buttonDescartar)
        buttonGuardar = findViewById(R.id.buttonGuardar)
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)

        // Deshabilitar el botón de Guardar hasta que los datos se carguen
        buttonGuardar.isEnabled = false

        // 3. Lógica de Carga
        cargarTodasPreguntas()
        cargarPreguntasRespuestasCliente()

        // 4. Listeners
        txtVolverLogin.setOnClickListener {
            finish()
        }

        buttonDescartar.setOnClickListener {
            mostrarDialogoDescartar()
        }

        buttonGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    // =================================================================
    // LÓGICA DE CARGA
    // =================================================================

    /** Carga todas las preguntas posibles al Spinner */
    private fun cargarTodasPreguntas() {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) { obtenerTodasPreguntasAPI() }

            if (result.isNotEmpty()) {
                listaPreguntasCompletas = result
                // Intenta actualizar la UI si las respuestas del cliente ya están cargadas
                actualizarSpinnersYRespuestas()
            } else {
                Toast.makeText(this@Act_preguntas_cliente, "Error: No se pudo cargar el listado completo de preguntas.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Carga las preguntas y respuestas actuales del cliente */
    private fun cargarPreguntasRespuestasCliente() {
        CoroutineScope(Dispatchers.Main).launch {
            val (success, data, message) = withContext(Dispatchers.IO) {
                llamarAPIConsultarPreguntasRespuestas(userEmail!!)
            }

            if (success && data != null && data.preguntas.size == 3) {
                idUsuarioActual = data.id_usuario
                // Ordenar las preguntas por ID para asegurar consistencia
                preguntasOriginales = data.preguntas.sortedBy { it.id_pregunta }
                // Intenta actualizar la UI si la lista completa de preguntas ya está cargada
                actualizarSpinnersYRespuestas()
            } else {
                Toast.makeText(this@Act_preguntas_cliente, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Actualiza la UI (Spinners y EditTexts) una vez que ambos sets de datos estén listos */
    private fun actualizarSpinnersYRespuestas() {
        // Necesitamos AMBAS listas para llenar la UI correctamente
        if (listaPreguntasCompletas.isEmpty() || preguntasOriginales.isEmpty() || idUsuarioActual == -1) {
            return // Datos incompletos, espera el otro coroutine
        }

        // 1. Configurar Adaptador para Spinners
        val descripciones = listaPreguntasCompletas.map { it.descripcion }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, descripciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinners = listOf(spinner_pregunta1, spinner_pregunta2, spinner_pregunta3)
        val respuestasEt = listOf(txt_respuesta1, txt_respuesta2, txt_respuesta3)

        // 2. Asignar datos
        for (i in 0 until 3) {
            spinners[i].adapter = adapter

            val pregResp = preguntasOriginales.getOrNull(i) ?: return

            // Buscar la posición de la pregunta actual del cliente en la lista completa
            val index = listaPreguntasCompletas.indexOfFirst { it.id == pregResp.id_pregunta }
            if (index != -1) {
                spinners[i].setSelection(index)
            }

            // Cargar la respuesta actual
            respuestasEt[i].setText(pregResp.respuesta_pregunta)
        }

        // 3. Habilitar el botón Guardar
        buttonGuardar.isEnabled = true
    }

    // =================================================================
    // LÓGICA DE GUARDADO
    // =================================================================

    private fun guardarCambios() {
        if (idUsuarioActual == -1 || listaPreguntasCompletas.isEmpty() || preguntasOriginales.isEmpty()) {
            Toast.makeText(this, "Error de carga de datos. Intente de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        val spinners = listOf(spinner_pregunta1, spinner_pregunta2, spinner_pregunta3)
        val respuestasEt = listOf(txt_respuesta1, txt_respuesta2, txt_respuesta3)

        // Triple: <ID Original de Pregunta, Nuevo ID de Pregunta, Nueva Respuesta>
        val datosActualizar = mutableListOf<Triple<Int, Int, String>>()
        val newIds = mutableSetOf<Int>()

        for (i in 0 until 3) {
            val originalId = preguntasOriginales[i].id_pregunta

            val newQuestionDesc = spinners[i].selectedItem.toString()
            val newId = listaPreguntasCompletas.firstOrNull { it.descripcion == newQuestionDesc }?.id ?: -1

            val newAnswer = respuestasEt[i].text.toString().trim() // Limpiar espacios

            // 1. Validaciones
            if (newId == -1 || newAnswer.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar una pregunta y dar una respuesta para el ítem ${i+1}.", Toast.LENGTH_LONG).show()
                return
            }
            if (newIds.contains(newId)) {
                Toast.makeText(this, "Las preguntas seleccionadas deben ser únicas. (Error en ítem ${i+1})", Toast.LENGTH_LONG).show()
                return
            }
            newIds.add(newId)

            datosActualizar.add(Triple(originalId, newId, newAnswer))
        }

        // 2. Llamada a la API de actualización
        CoroutineScope(Dispatchers.Main).launch {
            buttonGuardar.isEnabled = false // Deshabilitar mientras se guarda
            val (success, message) = withContext(Dispatchers.IO) {
                llamarAPIActualizarRespuestas(idUsuarioActual, datosActualizar)
            }

            Toast.makeText(this@Act_preguntas_cliente, message, Toast.LENGTH_LONG).show()
            buttonGuardar.isEnabled = true

            if (success) {
                val intent = Intent(this@Act_preguntas_cliente, Perfil_cliente::class.java)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun mostrarDialogoDescartar() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Descartar cambios")
        builder.setMessage("¿Deseas descartar los cambios realizados? Se perderán todas las ediciones.")
        builder.setPositiveButton("Sí") { dialog, _ ->
            dialog.dismiss()
            finish() // Cierra la actividad y vuelve a Perfil_cliente
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    // =================================================================
    // FUNCIONES DE RED
    // =================================================================

    private fun obtenerTodasPreguntasAPI(): List<PreguntaCompleta> {
        val url = "${ApiConfig.BASE_URL}/consultas/cliente/perfil/consultar_preguntas_cliente.php"
        val request = Request.Builder().url(url).build()
        val lista = mutableListOf<PreguntaCompleta>()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    Log.e("Act_preguntas", "Error de red al obtener todas las preguntas: ${response.code}")
                    return emptyList()
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val preguntasJson = json.optJSONArray("preguntas")

                if (success && preguntasJson != null) {
                    for (i in 0 until preguntasJson.length()) {
                        val obj = preguntasJson.getJSONObject(i)
                        lista.add(PreguntaCompleta(
                            id = obj.optInt("id_pregunta"),
                            descripcion = obj.optString("descripcion")
                        ))
                    }
                }
                lista
            }
        } catch (e: IOException) {
            Log.e("Act_preguntas", "Error de conexión (Obtener todas): ${e.message}")
            emptyList()
        } catch (e: Exception) {
            Log.e("Act_preguntas", "Error al procesar JSON (Obtener todas): ${e.message}")
            emptyList()
        }
    }

    private fun llamarAPIConsultarPreguntasRespuestas(correo: String): Triple<Boolean, PreguntasCliente?, String> {
        val url = "${ApiConfig.BASE_URL}/consultas/cliente/perfil/consultar_respuestas_cliente.php"

        val requestBody = FormBody.Builder()
            .add("correo", correo)
            .build()

        val request = Request.Builder().url(url).post(requestBody).build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return Triple(false, null, "Error de red: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Error desconocido.")
                val idUsuario = json.optInt("id_usuario", -1)
                val preguntasJson = json.optJSONArray("preguntas")
                val preguntasList = mutableListOf<PreguntaRespuesta>()

                if (success && preguntasJson != null) {
                    for (i in 0 until preguntasJson.length()) {
                        val obj = preguntasJson.getJSONObject(i)
                        preguntasList.add(PreguntaRespuesta(
                            id_pregunta = obj.optInt("id_pregunta"),
                            descripcion = obj.optString("descripcion"),
                            respuesta_pregunta = obj.optString("respuesta_pregunta"),
                            orden = i + 1
                        ))
                    }
                    val data = PreguntasCliente(idUsuario, preguntasList)
                    return Triple(true, data, mensaje)
                }

                Triple(success, null, mensaje)
            }
        } catch (e: IOException) {
            Log.e("Act_preguntas", "Error de conexión (Consultar Respuestas): ${e.message}")
            Triple(false, null, "Error de conexión: Verifique su red.")
        } catch (e: Exception) {
            Log.e("Act_preguntas", "Error al procesar la respuesta (Consultar Respuestas): ${e.message}")
            Triple(false, null, "Error al procesar la respuesta del servidor.")
        }
    }

    private fun llamarAPIActualizarRespuestas(idUsuario: Int, datos: List<Triple<Int, Int, String>>): Pair<Boolean, String> {
        val url = "${ApiConfig.BASE_URL}/consultas/cliente/perfil/actualizar_respuestas_cliente.php"

        val formBuilder = FormBody.Builder()
            .add("id_usuario", idUsuario.toString())
            // Los datos se envían indexados del 1 al 3
            .add("original_id_pregunta1", datos[0].first.toString())
            .add("nuevo_id_pregunta1", datos[0].second.toString())
            .add("respuesta1", datos[0].third)

            .add("original_id_pregunta2", datos[1].first.toString())
            .add("nuevo_id_pregunta2", datos[1].second.toString())
            .add("respuesta2", datos[1].third)

            .add("original_id_pregunta3", datos[2].first.toString())
            .add("nuevo_id_pregunta3", datos[2].second.toString())
            .add("respuesta3", datos[2].third)

        val request = Request.Builder().url(url).post(formBuilder.build()).build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return Pair(false, "Error de red: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Error desconocido.")

                Pair(success, mensaje)
            }
        } catch (e: IOException) {
            Log.e("Act_preguntas", "Error de conexión (Actualizar Resp): ${e.message}")
            Pair(false, "Error de conexión. Verifique su red.")
        } catch (e: Exception) {
            Log.e("Act_preguntas", "Error al procesar la respuesta (Actualizar Resp): ${e.message}")
            Pair(false, "Error al procesar la respuesta del servidor.")
        }
    }
}