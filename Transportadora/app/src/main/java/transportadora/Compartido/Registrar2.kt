package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Cliente.Preguntas_almacenados
import transportadora.Login.R
import transportadora.Modelos.Cliente.Pregunta
import transportadora.Configuracion.ApiConfig

class Registrar2 : AppCompatActivity() {
    // Lista completa de preguntas cargadas desde el almacenamiento
    private var listapreguntasCompleta: List<Pregunta> = emptyList()
    private lateinit var emailCliente: String // Variable para almacenar el email

    // Referencias a los Spinners
    private lateinit var spinner_pregunta1: Spinner
    private lateinit var spinner_pregunta2: Spinner
    private lateinit var spinner_pregunta3: Spinner

    // Referencias a los EditText
    private lateinit var txt_respuesta1: EditText
    private lateinit var txt_respuesta2: EditText
    private lateinit var txt_respuesta3: EditText

    // Variables para almacenar la pregunta seleccionada (su ID) - ¡Estas son las que usaremos!
    private var idPregunta1: Int? = null
    private var idPregunta2: Int? = null
    private var idPregunta3: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar2)

        // 1. Capturar el Email
        emailCliente = intent.getStringExtra("EMAIL_CLIENTE") ?: ""
        if (emailCliente.isEmpty()) {
            Toast.makeText(this, "Error: No se recibió el correo electrónico del cliente.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        Log.d("Registrar2", "Email recibido: $emailCliente")

        // 2. Inicializar vistas
        spinner_pregunta1 = findViewById(R.id.txt_pregunta1)
        spinner_pregunta2 = findViewById(R.id.txt_pregunta2)
        spinner_pregunta3 = findViewById(R.id.txt_pregunta3)
        txt_respuesta1 = findViewById(R.id.txt_respuesta1)
        txt_respuesta2 = findViewById(R.id.txt_respuesta2)
        txt_respuesta3 = findViewById(R.id.txt_respuesta3)

        // 3. Cargar datos
        cargarPreguntas()

        // 4. Configurar Listeners de Spinners
        configurarListenersSpinners()

        // 5. Escuchar botones
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val buttonRegistrar = findViewById<Button>(R.id.buttonRegistrar)
        buttonRegistrar.setOnClickListener {
            registrarRespuestasSeguridad() // Llama a la nueva función de registro
        }
    }

    /**
     * Carga las preguntas de seguridad utilizando corrutinas. (Sin cambios)
     */
    private fun cargarPreguntas() {
        lifecycleScope.launch {
            try {
                val preguntas = withContext(Dispatchers.IO) {
                    Preguntas_almacenados.obtener_preguntas()
                }

                if (preguntas.isNotEmpty()) {
                    listapreguntasCompleta = preguntas
                    actualizarSpinners(null, null, null)

                    // Inicializar los IDs con la primera pregunta
                    idPregunta1 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                    idPregunta2 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                    idPregunta3 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                } else {
                    Toast.makeText(this@Registrar2, "No se encontraron preguntas de seguridad.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar2, "Error al cargar preguntas: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Configura los listeners para cada Spinner. (Sin cambios)
     */
    private fun configurarListenersSpinners() {
        // Listener para la Pregunta 1
        spinner_pregunta1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcionSeleccionada = parent.getItemAtPosition(position).toString()
                val preguntaSeleccionada = listapreguntasCompleta.find { it.descripcion == descripcionSeleccionada }

                if (preguntaSeleccionada?.id_pregunta != idPregunta1) {
                    idPregunta1 = preguntaSeleccionada?.id_pregunta
                    actualizarSpinners(idPregunta1, idPregunta2, idPregunta3)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para la Pregunta 2
        spinner_pregunta2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcionSeleccionada = parent.getItemAtPosition(position).toString()
                val preguntaSeleccionada = listapreguntasCompleta.find { it.descripcion == descripcionSeleccionada }

                if (preguntaSeleccionada?.id_pregunta != idPregunta2) {
                    idPregunta2 = preguntaSeleccionada?.id_pregunta
                    actualizarSpinners(idPregunta1, idPregunta2, idPregunta3)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Listener para la Pregunta 3
        spinner_pregunta3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcionSeleccionada = parent.getItemAtPosition(position).toString()
                val preguntaSeleccionada = listapreguntasCompleta.find { it.descripcion == descripcionSeleccionada }

                if (preguntaSeleccionada?.id_pregunta != idPregunta3) {
                    idPregunta3 = preguntaSeleccionada?.id_pregunta
                    actualizarSpinners(idPregunta1, idPregunta2, idPregunta3)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    /**
     * Actualiza las opciones de los Spinners para asegurar que solo haya selección única. (Sin cambios)
     */
    private fun actualizarSpinners(
        p1: Int?, // ID de la pregunta seleccionada en Spinner 1
        p2: Int?, // ID de la pregunta seleccionada en Spinner 2
        p3: Int?  // ID de la pregunta seleccionada en Spinner 3
    ) {
        val listaCompletaDescripciones = listapreguntasCompleta.map { it.descripcion }

        // --- 1. Determinar las selecciones actuales por descripción ---
        val descripcionActual1 = spinner_pregunta1.selectedItem?.toString()
        val descripcionActual2 = spinner_pregunta2.selectedItem?.toString()
        val descripcionActual3 = spinner_pregunta3.selectedItem?.toString()

        // --- 2. Preparar listas de opciones disponibles para cada Spinner ---

        // Opciones para Spinner 1: Excluir p2 y p3
        val opciones1 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p2 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p3
        }
        setSpinnerAdapter(spinner_pregunta1, opciones1, descripcionActual1)

        // Opciones para Spinner 2: Excluir p1 y p3
        val opciones2 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p1 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p3
        }
        setSpinnerAdapter(spinner_pregunta2, opciones2, descripcionActual2)

        // Opciones para Spinner 3: Excluir p1 y p2
        val opciones3 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p1 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p2
        }
        setSpinnerAdapter(spinner_pregunta3, opciones3, descripcionActual3)
    }

    /**
     * Helper para configurar el adaptador de un Spinner y restaurar la selección. (Sin cambios)
     */
    private fun setSpinnerAdapter(spinner: Spinner, opciones: List<String>, seleccionPrevia: String?) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        // Intentar restaurar la selección anterior si todavía está disponible
        if (seleccionPrevia != null) {
            val posicion = opciones.indexOf(seleccionPrevia)
            if (posicion >= 0) {
                spinner.setSelection(posicion, false)
            } else {
                spinner.setSelection(0, false)
            }
        }
    }

    /**
     * Lógica para obtener los datos e iniciar el proceso de registro de respuestas de seguridad.
     */
    private fun registrarRespuestasSeguridad() {
        // --- 1. Obtener valores de IDs y Respuestas ---
        // CORRECCIÓN CLAVE: Usamos las variables globales que guardan el ID único.
        val pospregunta1 = spinner_pregunta1.selectedItemPosition
        val pospregunta2 = spinner_pregunta2.selectedItemPosition
        val pospregunta3 = spinner_pregunta3.selectedItemPosition

        val id_p1 = pospregunta1+1
        val res_p1 = txt_respuesta1.text.toString().trim()

        val id_p2 = pospregunta2+1
        val res_p2 = txt_respuesta2.text.toString().trim()

        val id_p3 = pospregunta3+1
        val res_p3 = txt_respuesta3.text.toString().trim()

        Log.e("Registrar", id_p1.toString()+" "+id_p2+" "+id_p3)

        // --- 2. Validación ---
        if (id_p1 == -1 || id_p2 == -1 || id_p3 == -1 || res_p1.isEmpty() || res_p2.isEmpty() || res_p3.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona las 3 preguntas y escribe las 3 respuestas.", Toast.LENGTH_LONG).show()
            return
        }

        // Validación de selección única (necesaria si el usuario no interactuó y los 3 están en la opción por defecto)
        if (id_p1 == id_p2) {
            Toast.makeText(this, id_p1.toString()+id_p2.toString()+"\n1. Las preguntas de seguridad deben ser únicas. Por favor, selecciona preguntas diferentes.", Toast.LENGTH_LONG).show()
            return
        }
        if (id_p1 == id_p3) {
            Toast.makeText(this, "2. Las preguntas de seguridad deben ser únicas. Por favor, selecciona preguntas diferentes.", Toast.LENGTH_LONG).show()
            return
        }
        if (id_p2 == id_p3) {
            Toast.makeText(this, "3. Las preguntas de seguridad deben ser únicas. Por favor, selecciona preguntas diferentes.", Toast.LENGTH_LONG).show()
            return
        }

        // --- 3. Llamar a la función de registro en Coroutine ---
        lifecycleScope.launch {
            val exito = registrarRespuestas(
                emailCliente,
                id_p1, res_p1,
                id_p2, res_p2,
                id_p3, res_p3
            )

            if (exito) {
                Toast.makeText(
                    this@Registrar2,
                    "Cliente registrado exitosamente, \npor favor ahora inicie sesión",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@Registrar2, Main::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@Registrar2, "Error al guardar las respuestas de seguridad.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Realiza la llamada a la API para registrar las respuestas de seguridad. (Sin cambios)
     */
    private suspend fun registrarRespuestas(
        email: String,
        id1: Int, res1: String,
        id2: Int, res2: String,
        id3: Int, res3: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(ApiConfig.BASE_URL + "consultas/cliente/preguntas/registrar_respuestas_seguridad.php")

            // Crear un objeto JSON con los datos
            val jsonParams = org.json.JSONObject().apply {
                put("correo", email)
                put("preguntas", org.json.JSONArray().apply {
                    put(org.json.JSONObject().apply { put("id_pregunta", id1); put("respuesta", res1) })
                    put(org.json.JSONObject().apply { put("id_pregunta", id2); put("respuesta", res2) })
                    put(org.json.JSONObject().apply { put("id_pregunta", id3); put("respuesta", res3) })
                })
            }

            val params = jsonParams.toString()

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
            val responseCode = connection.responseCode
            Log.d("Registrar2", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                Log.d("Registrar2", "Respuesta recibida: $response")

                val json = org.json.JSONObject(response)
                // Se asume que el PHP devuelve {"success": "1"} si todo sale bien.
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No hay mensaje de error"
                Log.e("Registrar2", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Registrar2", "Error al registrar respuestas (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }
}