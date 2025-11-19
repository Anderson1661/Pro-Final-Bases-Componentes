package transportadora.Conductor

import android.annotation.SuppressLint
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Cliente.Ciudad_almacenados
import transportadora.Almacenados.Cliente.Departamento_almacenados
import transportadora.Almacenados.Cliente.Genero_almacen
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import transportadora.Almacenados.Conductor.Registrar_almacenados
import transportadora.Almacenados.Cliente.Preguntas_almacenados
import transportadora.Modelos.Cliente.Pregunta
import androidx.lifecycle.lifecycleScope

class Registrar_conductor : AppCompatActivity() {

    private var listaPaisesCompleta: List<transportadora.Modelos.Cliente.Pais> = emptyList()

    private lateinit var spinner_tipos_id: Spinner
    private lateinit var spinner_paises: Spinner
    private lateinit var spinner_departamentos: Spinner
    private lateinit var spinner_ciudades: Spinner
    private lateinit var spinner_nacionalidad: Spinner
    private lateinit var spinner_genero: Spinner
    private lateinit var spinner_cantidad_tel: Spinner

    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtTel1: EditText
    private lateinit var txtTel2: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText

    // --- Preguntas de seguridad ---
    private var listapreguntasCompleta: List<Pregunta> = emptyList()
    private lateinit var spinner_pregunta1: Spinner
    private lateinit var spinner_pregunta2: Spinner
    private lateinit var spinner_pregunta3: Spinner
    private lateinit var txt_respuesta1: EditText
    private lateinit var txt_respuesta2: EditText
    private lateinit var txt_respuesta3: EditText
    private var idPregunta1: Int? = null
    private var idPregunta2: Int? = null
    private var idPregunta3: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_conductor)

        spinner_tipos_id = findViewById(R.id.txt_tipo_id_reg1)
        spinner_paises = findViewById(R.id.spinner_pais)
        spinner_departamentos = findViewById(R.id.spinner_departamento)
        spinner_ciudades = findViewById(R.id.spinner_ciudad)
        spinner_nacionalidad = findViewById(R.id.txt_nacionalidad)
        spinner_genero = findViewById(R.id.txt_genero)
        spinner_cantidad_tel = findViewById(R.id.spinner_cantidad_tel)

        txtIdentificacion = findViewById(R.id.txt_id_reg1)
        txtNombre = findViewById(R.id.txt_nombre_reg1)
        txtTel1 = findViewById(R.id.txt_tel1)
        txtTel2 = findViewById(R.id.txt_tel2)
        txtCorreo = findViewById(R.id.txt_email_reg1)
        txtDireccion = findViewById(R.id.txt_dir_reg1)

        txtTel1.isEnabled = true
        txtTel2.isEnabled = false

        spinner_cantidad_tel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "1" -> {
                        txtTel1.isEnabled = true
                        txtTel2.isEnabled = false
                        txtTel2.text.clear()
                    }
                    "2" -> {
                        txtTel1.isEnabled = true
                        txtTel2.isEnabled = true
                    }
                    else -> {
                        txtTel1.isEnabled = true
                        txtTel2.isEnabled = false
                        txtTel2.text.clear()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                txtTel1.isEnabled = true
                txtTel2.isEnabled = false
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinner_nacionalidad.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Registrar_conductor, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }

                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    val listageneros = generos.map { it.descripcion }
                    spinner_genero.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listageneros).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }

                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        spinner_paises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                listaPaisesCompleta.getOrNull(position)?.let {
                    cargarDepartamentos(it.id_pais, spinner_departamentos)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_departamentos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                val posPais = spinner_paises.selectedItemPosition
                listaPaisesCompleta.getOrNull(posPais)?.let {
                    cargarCiudades(it.id_pais, deptoSeleccionado, spinner_ciudades)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Inicializar vistas de preguntas (si existen en el layout)
        try {
            spinner_pregunta1 = findViewById(R.id.txt_pregunta1)
            spinner_pregunta2 = findViewById(R.id.txt_pregunta2)
            spinner_pregunta3 = findViewById(R.id.txt_pregunta3)
            txt_respuesta1 = findViewById(R.id.txt_respuesta1)
            txt_respuesta2 = findViewById(R.id.txt_respuesta2)
            txt_respuesta3 = findViewById(R.id.txt_respuesta3)

            // Cargar preguntas y configurar listeners
            cargarPreguntas()
            configurarListenersSpinners()
        } catch (e: Exception) {
            // Si el layout no tiene las vistas de preguntas, ignorar (no fatal)
            e.printStackTrace()
        }

        val txtVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolver.setOnClickListener { finish() }

        val btnRegistrar = findViewById<Button>(R.id.buttonRegistrar)
        btnRegistrar.setOnClickListener { registrarNuevoConductor() }
    }

    private fun registrarNuevoConductor() {
        val numero_identificacion: String = txtIdentificacion.text.toString().trim()
        val nombrecompleto: String = txtNombre.text.toString().trim()
        val correo: String = txtCorreo.text.toString().trim()
        val direccion: String = txtDireccion.text.toString().trim()
        val tel1: String = txtTel1.text.toString().trim()
        val tel2: String = txtTel2.text.toString().trim()

        val id_tipo_identificacion = spinner_tipos_id.selectedItemPosition + 1
        val id_genero = spinner_genero.selectedItemPosition + 1
        val posNacionalidad = spinner_nacionalidad.selectedItemPosition
        val id_pais_nacionalidad = listaPaisesCompleta.getOrNull(posNacionalidad)?.id_pais ?: -1

        val pais = spinner_paises.selectedItem?.toString() ?: ""
        val departamento = spinner_departamentos.selectedItem?.toString() ?: ""
        val ciudad = spinner_ciudades.selectedItem?.toString() ?: ""

        if (numero_identificacion.isEmpty() || nombrecompleto.isEmpty() || correo.isEmpty() ||
            direccion.isEmpty() || tel1.isEmpty() || pais.isEmpty() || departamento.isEmpty() || ciudad.isEmpty() ||
            id_tipo_identificacion == 0 || id_genero == 0 || id_pais_nacionalidad == -1) {

            Toast.makeText(this, "Por favor, completa todos los campos (excepto Teléfono 2)", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)
                if (codigoPostal != null) {
                    // Generar valores básicos de vehículo para enviar junto al registro
                    val placa = "TMP${numero_identificacion.takeLast(4)}${System.currentTimeMillis() % 10000}"
                    val marca = "Chevrolet"
                    val linea = "Spark"
                    val modeloVeh = "2022"
                    val colorVeh = "Blanco"
                    val tipoServicio = "Pasajeros"

                    val registrado = registrarConductor(
                        id_tipo_identificacion,
                        numero_identificacion,
                        nombrecompleto,
                        direccion,
                        correo,
                        id_genero,
                        id_pais_nacionalidad,
                        codigoPostal,
                        tel1,
                        tel2,
                        placa,
                        marca,
                        linea,
                        modeloVeh,
                        colorVeh,
                        tipoServicio
                    )

                    if (registrado) {
                        // Si el registro del conductor fue exitoso, intentar guardar las respuestas de seguridad
                        val exitoPreguntas = registrarRespuestasSeguridad(correo)
                        if (exitoPreguntas) {
                            Toast.makeText(this@Registrar_conductor, "Registro completado. Inicia sesión para cambiar tu contraseña.", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@Registrar_conductor, "Conductor registrado, pero falló el registro de preguntas de seguridad.", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@Registrar_conductor, "Error al guardar el registro. Revisa la información.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@Registrar_conductor, "No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Registrar_conductor, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- Copiado de Registrar2: manejo de preguntas de seguridad ---
    private fun cargarPreguntas() {
        lifecycleScope.launch {
            try {
                val preguntas = withContext(Dispatchers.IO) { Preguntas_almacenados.obtener_preguntas() }

                if (preguntas.isNotEmpty()) {
                    listapreguntasCompleta = preguntas
                    actualizarSpinners(null, null, null)

                    idPregunta1 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                    idPregunta2 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                    idPregunta3 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun configurarListenersSpinners() {
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

    private fun actualizarSpinners(p1: Int?, p2: Int?, p3: Int?) {
        val listaCompletaDescripciones = listapreguntasCompleta.map { it.descripcion }

        val descripcionActual1 = spinner_pregunta1.selectedItem?.toString()
        val descripcionActual2 = spinner_pregunta2.selectedItem?.toString()
        val descripcionActual3 = spinner_pregunta3.selectedItem?.toString()

        val opciones1 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p2 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p3
        }
        setSpinnerAdapter(spinner_pregunta1, opciones1, descripcionActual1)

        val opciones2 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p1 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p3
        }
        setSpinnerAdapter(spinner_pregunta2, opciones2, descripcionActual2)

        val opciones3 = listaCompletaDescripciones.filter {
            listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p1 &&
                    listapreguntasCompleta.find { preg -> preg.descripcion == it }?.id_pregunta != p2
        }
        setSpinnerAdapter(spinner_pregunta3, opciones3, descripcionActual3)
    }

    private fun setSpinnerAdapter(spinner: Spinner, opciones: List<String>, seleccionPrevia: String?) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter

        if (seleccionPrevia != null) {
            val posicion = opciones.indexOf(seleccionPrevia)
            if (posicion >= 0) {
                spinner.setSelection(posicion, false)
            } else {
                spinner.setSelection(0, false)
            }
        }
    }

    private suspend fun registrarRespuestasSeguridad(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Obtener descripciones seleccionadas
            val desc1 = spinner_pregunta1.selectedItem?.toString() ?: ""
            val desc2 = spinner_pregunta2.selectedItem?.toString() ?: ""
            val desc3 = spinner_pregunta3.selectedItem?.toString() ?: ""

            // Mapear descripciones a IDs reales usando la lista cargada
            val id_p1 = listapreguntasCompleta.find { it.descripcion == desc1 }?.id_pregunta ?: -1
            val id_p2 = listapreguntasCompleta.find { it.descripcion == desc2 }?.id_pregunta ?: -1
            val id_p3 = listapreguntasCompleta.find { it.descripcion == desc3 }?.id_pregunta ?: -1

            val res_p1 = txt_respuesta1.text.toString().trim()
            val res_p2 = txt_respuesta2.text.toString().trim()
            val res_p3 = txt_respuesta3.text.toString().trim()

            // Validaciones
            if (res_p1.isEmpty() || res_p2.isEmpty() || res_p3.isEmpty()) return@withContext false
            if (id_p1 <= 0 || id_p2 <= 0 || id_p3 <= 0) return@withContext false
            if (id_p1 == id_p2 || id_p1 == id_p3 || id_p2 == id_p3) return@withContext false

            val url = java.net.URL(ApiConfig.BASE_URL + "consultas/cliente/preguntas/registrar_respuestas_seguridad.php")

            val jsonParams = org.json.JSONObject().apply {
                put("correo", email)
                put("preguntas", org.json.JSONArray().apply {
                    put(org.json.JSONObject().apply { put("id_pregunta", id_p1); put("respuesta", res_p1) })
                    put(org.json.JSONObject().apply { put("id_pregunta", id_p2); put("respuesta", res_p2) })
                    put(org.json.JSONObject().apply { put("id_pregunta", id_p3); put("respuesta", res_p3) })
                })
            }

            val params = jsonParams.toString()

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
            val responseCode = connection.responseCode

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    private suspend fun obtenerCodigoPostal(pais: String, departamento: String, ciudad: String): String? = withContext(Dispatchers.IO) {
        var codigoPostal: String? = null
        try {
            val url = java.net.URL(ApiConfig.BASE_URL + "consultas/cliente/perfil/obtener_codigo_postal.php")
            val params = "pais=${java.net.URLEncoder.encode(pais, "UTF-8")}" +
                    "&departamento=${java.net.URLEncoder.encode(departamento, "UTF-8")}" +
                    "&ciudad=${java.net.URLEncoder.encode(ciudad, "UTF-8")}" 

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                codigoPostal = json.getJSONObject("datos").getString("codigo_postal")
            } else {
                Log.e("Registrar_conductor", "Error PHP al obtener Código Postal: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Registrar_conductor", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    private suspend fun registrarConductor(
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        correo: String,
        idGenero: Int,
        idNacionalidad: Int,
        codigoPostal: String,
        tel1: String,
        tel2: String,
        placa: String = "",
        marca: String = "",
        linea: String = "",
        modelo: String = "",
        color: String = "",
        tipoServicio: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext Registrar_almacenados.registrarConductor(
            idTipoIdentificacion,
            identificacion,
            nombre,
            direccion,
            correo,
            idGenero,
            idNacionalidad,
            codigoPostal,
            tel1,
            tel2,
            placa,
            marca,
            linea,
            modelo,
            color,
            tipoServicio
        )
    }
}