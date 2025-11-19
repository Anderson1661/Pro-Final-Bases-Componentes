package transportadora.Conductor

import android.annotation.SuppressLint
import android.widget.AdapterView // Added import
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Cliente.Genero_almacen
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Almacenados.Conductor.Datos_perfil_almacenados
import transportadora.Compartido.Registrar2
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion
import transportadora.Almacenados.Cliente.Departamento_almacenados // Added import
import transportadora.Almacenados.Cliente.Ciudad_almacenados // Added import
import android.os.Bundle
import android.widget.Toast.makeText
import android.view.View
import android.content.Intent

class Act_perfil_conductor : AppCompatActivity() {
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()
    private var listaCiudadesDestino: List<Ciudad> = emptyList()
    private lateinit var spinner_ciudades1: Spinner
    private lateinit var spinner_tipos_id: Spinner
    private var spinner_paises: Spinner? = null
    private var spinner_departamentos: Spinner? = null
    private var spinner_ciudades: Spinner? = null
    private lateinit var spinner_nacionalidad: Spinner
    private lateinit var spinner_genero: Spinner
    // Security questions UI
    private lateinit var spinner_pregunta1: Spinner
    private lateinit var spinner_pregunta2: Spinner
    private lateinit var spinner_pregunta3: Spinner
    private lateinit var txt_respuesta1: android.widget.EditText
    private lateinit var txt_respuesta2: android.widget.EditText
    private lateinit var txt_respuesta3: android.widget.EditText
    private var preguntasIdsOriginales: MutableList<Int> = mutableListOf()
    private var idUsuarioPreguntas: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_conductor)

        // Initialize views
        spinner_tipos_id = findViewById(R.id.txt_tipo_id)
        // Some layouts use different ids for the country spinner; try common ids and fall back to nacionalidad
        spinner_paises = try { findViewById(R.id.txt_pais) } catch (e: Exception) { null }
            ?: try { findViewById(R.id.spinner_pais) } catch (e: Exception) { null }
            ?: try { findViewById(R.id.txt_nacionalidad) } catch (e: Exception) { null }
        spinner_departamentos = try { findViewById(R.id.txt_departamento) } catch (e: Exception) { null }
        spinner_ciudades = try { findViewById(R.id.txt_ciudad) } catch (e: Exception) { null }
        spinner_nacionalidad = findViewById(R.id.txt_nacionalidad)
        // Correct genero id (some layouts use spinner_genero)
        spinner_genero = try { findViewById(R.id.txt_genero) } catch (e: Exception) { findViewById(R.id.spinner_genero) }

        // Security questions views
        spinner_pregunta1 = findViewById(R.id.spinner_pregunta1)
        spinner_pregunta2 = findViewById(R.id.spinner_pregunta2)
        spinner_pregunta3 = findViewById(R.id.spinner_pregunta3)
        txt_respuesta1 = findViewById(R.id.txt_respuesta1)
        txt_respuesta2 = findViewById(R.id.txt_respuesta2)
        txt_respuesta3 = findViewById(R.id.txt_respuesta3)

        val txtIdentificacion = findViewById<TextView>(R.id.txt_id)
        val txtNombre = findViewById<TextView>(R.id.txt_nombre)
        val txtTel1 = findViewById<TextView>(R.id.txt_tel1)
        val txtTel2 = findViewById<TextView>(R.id.txt_tel2)
        val txtCorreo = findViewById<TextView>(R.id.txt_email)
        val txtDireccion = findViewById<TextView>(R.id.txt_dir)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Load all static data first
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises?.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinner_nacionalidad.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_conductor, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }

                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    listagenero = generos
                    val listageneros = generos.map { it.descripcion }
                    spinner_genero.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listageneros).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_conductor, "No se encontraron generos", Toast.LENGTH_SHORT).show()
                }

                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_conductor, "No se encontraron tipos de identificacion", Toast.LENGTH_SHORT).show()
                }

                // Now load profile data and perform pre-selection
                if (userEmail != null) {
                    val perfil = withContext(Dispatchers.IO) {
                        Datos_perfil_almacenados.obtener_datos_perfil(userEmail)
                    }

                    // Load available security questions and populate spinners
                    val preguntasDisponibles = withContext(Dispatchers.IO) { transportadora.Almacenados.Cliente.Preguntas_almacenados.obtener_preguntas() }
                    val listaPreguntasDesc = preguntasDisponibles.map { it.descripcion }
                    spinner_pregunta1.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaPreguntasDesc).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                    spinner_pregunta2.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaPreguntasDesc).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                    spinner_pregunta3.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaPreguntasDesc).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

                    if (perfil != null) {
                        // Asignar cada campo del modelo a su respectivo TextView
                        val tipoIdSeleccionado = perfil.tipo_identificacion
                        val listaDescripcionesId = listatiposid.map { it.descripcion }
                        val indexId = listaDescripcionesId.indexOf(tipoIdSeleccionado)

                        if (indexId != -1) {
                            spinner_tipos_id.setSelection(indexId)
                        }
                        txtIdentificacion.text = perfil.identificacion
                        txtNombre.text = perfil.nombre
                        txtCorreo.text = perfil.correo
                        txtDireccion.text = perfil.direccion

                        val generoSeleccionado = perfil.genero
                        val listaDescripcionesGenero = listagenero.map { it.descripcion }
                        var indexGenero = listaDescripcionesGenero.indexOf(generoSeleccionado)

                        // If the server returned an ID instead of description, try to match by id
                        if (indexGenero == -1) {
                            try {
                                val generoId = generoSeleccionado?.toIntOrNull()
                                if (generoId != null) {
                                    indexGenero = listagenero.indexOfFirst { it.id_genero == generoId }
                                }
                            } catch (e: Exception) {
                                // ignore and leave indexGenero as -1
                            }
                        }

                        if (indexGenero != -1) {
                            spinner_genero.setSelection(indexGenero)
                        }

                        val listaNombresPaises = listaPaisesCompleta.map { it.nombre }

                        val nacionalidadSeleccionada = perfil.nacionalidad
                        val indexNacionalidad = listaNombresPaises.indexOf(nacionalidadSeleccionada)
                        if (indexNacionalidad != -1) {
                            spinner_nacionalidad.setSelection(indexNacionalidad)
                        }

                        val paisResidenciaSeleccionado = perfil.pais_residencia
                        val indexPaisResidencia = listaNombresPaises.indexOf(paisResidenciaSeleccionado)
                        if (indexPaisResidencia != -1) {
                            spinner_paises?.setSelection(indexPaisResidencia)
                        }

                        // Load departments for the selected country and pre-select the user's department
                        val selectedPaisIndex = spinner_paises?.selectedItemPosition ?: -1
                        val selectedPais = listaPaisesCompleta.getOrNull(selectedPaisIndex)
                        selectedPais?.let { pais ->
                            spinner_departamentos?.let { depSpinner ->
                                cargarDepartamentos(pais.id_pais, depSpinner, perfil.departamento) {
                                    // Callback after departments are loaded and selected
                                    val selectedDepto = departamentosOrigen.getOrNull(depSpinner.selectedItemPosition)
                                    selectedDepto?.let { depto ->
                                        spinner_ciudades?.let { citySpinner ->
                                            cargarCiudades(pais.id_pais, depto, citySpinner, perfil.ciudad)
                                        }
                                    }
                                }
                            }
                        }

                        // Teléfonos (muestra máximo dos)
                        txtTel1.text = perfil.telefonos.getOrNull(0) ?: "No registrado"
                        txtTel2.text = perfil.telefonos.getOrNull(1) ?: "No registrado"

                        // Cargar preguntas y respuestas ya registradas (si existen)
                        try {
                            val respuestasJson = withContext(Dispatchers.IO) { obtenerRespuestasSeguridad(userEmail) }
                            if (respuestasJson != null && respuestasJson.first) {
                                // respuestasJson.second = JSONObject with id_usuario and preguntas array
                                val dataObj = respuestasJson.second
                                idUsuarioPreguntas = dataObj.optInt("id_usuario", -1)
                                val preguntasArr = dataObj.optJSONArray("preguntas")
                                preguntasIdsOriginales.clear()
                                if (preguntasArr != null) {
                                    for (i in 0 until preguntasArr.length()) {
                                        val obj = preguntasArr.getJSONObject(i)
                                        val idPreg = obj.optInt("id_pregunta")
                                        val desc = obj.optString("descripcion")
                                        val resp = obj.optString("respuesta_pregunta")
                                        preguntasIdsOriginales.add(idPreg)
                                        // preseleccionar el spinner correspondiente
                                        val index = listaPreguntasDesc.indexOf(desc)
                                        when (i) {
                                            0 -> {
                                                if (index != -1) spinner_pregunta1.setSelection(index)
                                                txt_respuesta1.setText(resp)
                                            }
                                            1 -> {
                                                if (index != -1) spinner_pregunta2.setSelection(index)
                                                txt_respuesta2.setText(resp)
                                            }
                                            2 -> {
                                                if (index != -1) spinner_pregunta3.setSelection(index)
                                                txt_respuesta3.setText(resp)
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // No hay preguntas registradas o error; dejar campos vacíos
                        }

                    } else {
                        Toast.makeText(this@Act_perfil_conductor, "No se pudo cargar el perfil.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@Act_perfil_conductor, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_conductor, "Error general al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Set up listeners for cascading spinners
        spinner_paises?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                listaPaisesCompleta.getOrNull(position)?.let {
                    spinner_departamentos?.let { dep -> cargarDepartamentos(it.id_pais, dep) }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_departamentos?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                val posPais = spinner_paises?.selectedItemPosition ?: -1
                listaPaisesCompleta.getOrNull(posPais)?.let {
                    spinner_ciudades?.let { city -> cargarCiudades(it.id_pais, deptoSeleccionado, city) }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // escuchar botones y volver
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener { finish() }

        // Buscar botones: layout tiene `btn_actualizar`; `buttonGuardar`/`buttonDescartar` pueden faltar
        val buttonDescartar: Button? = findViewById(R.id.buttonDescartar)
        val buttonGuardar: Button? = findViewById(R.id.buttonGuardar) ?: findViewById(R.id.btn_actualizar)

        // Botón DESCARTAR → Confirmación antes de salir (si existe)
        buttonDescartar?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Descartar cambios")
            builder.setMessage("¿Deseas descartar los cambios realizados?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                finish() // Cierra la actividad
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Solo cierra el diálogo
            }
            builder.show()
        }

        // Botón GUARDAR → Realizar actualización de perfil del conductor
        buttonGuardar?.setOnClickListener {
            // Recopilar valores (similar a la versión cliente)
            val numero_identificacion = findViewById<TextView>(R.id.txt_id).text.toString().trim()
            val nombrecompleto = findViewById<TextView>(R.id.txt_nombre).text.toString().trim()
            val nuevoCorreo = findViewById<TextView>(R.id.txt_email).text.toString().trim()
            val direccion = findViewById<TextView>(R.id.txt_dir).text.toString().trim()
            val tel1 = findViewById<TextView>(R.id.txt_tel1).text.toString().trim()
            val tel2 = findViewById<TextView>(R.id.txt_tel2).text.toString().trim()

            val id_tipo_identificacion = listatiposid.getOrNull(spinner_tipos_id.selectedItemPosition)?.id_tipo_identificacion
                ?: (spinner_tipos_id.selectedItemPosition + 1)
            val id_genero = listagenero.getOrNull(spinner_genero.selectedItemPosition)?.id_genero
                ?: (spinner_genero.selectedItemPosition + 1)
            val pais = spinner_paises?.selectedItem?.toString() ?: spinner_nacionalidad.selectedItem?.toString() ?: ""
            val departamento = spinner_departamentos?.selectedItem?.toString() ?: ""
            val ciudad = spinner_ciudades?.selectedItem?.toString() ?: ""

            // Validaciones básicas
            if (userEmail.isNullOrEmpty()) {
                Toast.makeText(this, "Error: correo de usuario no disponible", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // 1) Obtener id_conductor por correo
                    val idConductor = withContext(Dispatchers.IO) { obtenerIdConductorPorCorreo(userEmail) }
                    if (idConductor == -1) {
                        Toast.makeText(this@Act_perfil_conductor, "No se pudo obtener el ID del conductor.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // 2) Obtener código postal
                    val codigoPostal = withContext(Dispatchers.IO) { obtenerCodigoPostal(pais, departamento, ciudad) }
                    if (codigoPostal == null) {
                        Toast.makeText(this@Act_perfil_conductor, "No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // 3) Llamar al endpoint de actualización del conductor
                    // Mapear la selección del spinner de nacionalidad al id_pais_nacionalidad real
                    val idPaisNacionalidad = try {
                        val posNac = spinner_nacionalidad.selectedItemPosition
                        listaPaisesCompleta.getOrNull(posNac)?.id_pais ?: 1
                    } catch (e: Exception) {
                        1
                    }

                    val actualizado = withContext(Dispatchers.IO) {
                        actualizarPerfilConductor(
                            idConductor,
                            id_tipo_identificacion,
                            numero_identificacion,
                            nombrecompleto,
                            direccion,
                            nuevoCorreo,
                            id_genero,
                            codigoPostal,
                            idPaisNacionalidad,
                            tel1,
                            tel2
                        )
                    }

                    if (actualizado) {
                        Toast.makeText(this@Act_perfil_conductor, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        // Volver al perfil conductor
                            // Después de actualizar perfil, también actualizar/registrar las respuestas de seguridad
                            try {
                                val nuevosIds = listOf(
                                    // map spinner selection to pregunta id via Preguntas_almacenados mapping
                                    transportadora.Almacenados.Cliente.Preguntas_almacenados.obtener_preguntas().getOrNull(spinner_pregunta1.selectedItemPosition)?.id_pregunta ?: -1,
                                    transportadora.Almacenados.Cliente.Preguntas_almacenados.obtener_preguntas().getOrNull(spinner_pregunta2.selectedItemPosition)?.id_pregunta ?: -1,
                                    transportadora.Almacenados.Cliente.Preguntas_almacenados.obtener_preguntas().getOrNull(spinner_pregunta3.selectedItemPosition)?.id_pregunta ?: -1
                                )
                                val respuestas = listOf(txt_respuesta1.text.toString(), txt_respuesta2.text.toString(), txt_respuesta3.text.toString())

                                val enviado = withContext(Dispatchers.IO) {
                                    if (preguntasIdsOriginales.size == 3 && idUsuarioPreguntas != -1) {
                                        actualizarRespuestasCliente(idUsuarioPreguntas, preguntasIdsOriginales, nuevosIds, respuestas)
                                    } else {
                                        // Registrar nuevas respuestas
                                        val lista = listOf(Pair(nuevosIds[0], respuestas[0]), Pair(nuevosIds[1], respuestas[1]), Pair(nuevosIds[2], respuestas[2]))
                                        registrarRespuestasSeguridad(nuevoCorreo, lista)
                                    }
                                }

                                if (!enviado) {
                                    Toast.makeText(this@Act_perfil_conductor, "Perfil actualizado, pero falló actualizar preguntas de seguridad.", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                // Si ocurre error al enviar preguntas, no impedir la navegación pero notificar
                                Toast.makeText(this@Act_perfil_conductor, "Perfil actualizado. Error al gestionar preguntas: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                            val intent = Intent(this@Act_perfil_conductor, Perfil_conductor::class.java)
                            startActivity(intent)
                            finish()
                    } else {
                        Toast.makeText(this@Act_perfil_conductor, "Error al actualizar perfil", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Act_perfil_conductor, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner, departamentoSeleccionado: String? = null, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { transportadora.Almacenados.Cliente.Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                departamentosOrigen = listaNombres

                spinner.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                departamentoSeleccionado?.let {
                    val deptoIndex = listaNombres.indexOf(it)
                    if (deptoIndex != -1) {
                        spinner.setSelection(deptoIndex)
                        // Add a small delay to allow UI to update before invoking callback
                        kotlinx.coroutines.delay(100) // Small delay, adjust if needed
                    }
                }
                onComplete?.invoke()
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_conductor, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner, ciudadSeleccionada: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { transportadora.Almacenados.Cliente.Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades // Assuming this is for the origin, consistent with Principal_cliente

                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                ciudadSeleccionada?.let {
                    val ciudadIndex = listaNombres.indexOf(it)
                    if (ciudadIndex != -1) {
                        spinner.setSelection(ciudadIndex)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_conductor, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun obtenerIdConductorPorCorreo(email: String): Int = withContext(Dispatchers.IO) {
        var idConductor = -1
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/conductor/perfil/obtener_id_conductor_por_correo.php")
            val params = "correo=${java.net.URLEncoder.encode(email, "UTF-8")}" 

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                idConductor = json.getInt("id_conductor")
                android.util.Log.d("Act_perfil_conductor", "ID Conductor obtenido: $idConductor")
            } else {
                android.util.Log.e("Act_perfil_conductor", "Error PHP al obtener ID: ${json.optString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error de red/JSON al obtener ID: ${e.message}", e)
        }
        return@withContext idConductor
    }

    private suspend fun obtenerCodigoPostal(pais: String, departamento: String, ciudad: String): String? = withContext(Dispatchers.IO) {
        var codigoPostal: String? = null
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/perfil/obtener_codigo_postal.php")

            val params = "pais=${java.net.URLEncoder.encode(pais, "UTF-8")}" +
                    "&departamento=${java.net.URLEncoder.encode(departamento, "UTF-8")}" +
                    "&ciudad=${java.net.URLEncoder.encode(ciudad, "UTF-8")}" 

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)

            if (json.getString("success") == "1") {
                codigoPostal = json.getJSONObject("datos").getString("codigo_postal")
                android.util.Log.d("Act_perfil_conductor", "Código Postal obtenido: $codigoPostal")
            } else {
                android.util.Log.e("Act_perfil_conductor", "Error PHP al obtener Código Postal: ${json.optString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    private suspend fun actualizarPerfilConductor(
        idConductor: Int,
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        correo: String,
        idGenero: Int,
        codigoPostal: String,
        idPaisNacionalidad: Int,
        tel1: String,
        tel2: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/conductor/perfil/actualizar_datos_perfil.php")

            val params = "id_conductor=$idConductor" +
                    "&id_tipo_identificacion=$idTipoIdentificacion" +
                    "&identificacion=${java.net.URLEncoder.encode(identificacion, "UTF-8")}" +
                    "&nombre=${java.net.URLEncoder.encode(nombre, "UTF-8")}" +
                    "&direccion=${java.net.URLEncoder.encode(direccion, "UTF-8")}" +
                    "&correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" +
                    "&id_genero=$idGenero" +
                    "&codigo_postal=${java.net.URLEncoder.encode(codigoPostal, "UTF-8")}" +
                    "&id_pais_nacionalidad=$idPaisNacionalidad" +
                    "&tel1=${java.net.URLEncoder.encode(tel1, "UTF-8")}" +
                    "&tel2=${java.net.URLEncoder.encode(tel2, "UTF-8")}" 

            android.util.Log.d("Act_perfil_conductor", "Sending params: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val responseCode = connection.responseCode
            android.util.Log.d("Act_perfil_conductor", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                android.util.Log.d("Act_perfil_conductor", "Received response: $response")
                val json = org.json.JSONObject(response)
                return@withContext json.optString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided"
                android.util.Log.e("Act_perfil_conductor", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error al actualizar perfil (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }

    private suspend fun obtenerRespuestasSeguridad(correo: String): Pair<Boolean, org.json.JSONObject?> = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/perfil/consultar_respuestas_cliente.php")
            val params = "correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" 

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            return@withContext Pair(json.optString("success") == "1", json)
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error al obtener respuestas seguridad: ${e.message}", e)
            return@withContext Pair(false, null)
        }
    }

    private suspend fun actualizarRespuestasCliente(
        idUsuario: Int,
        originales: List<Int>,
        nuevos: List<Int>,
        respuestas: List<String>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/perfil/actualizar_respuestas_cliente.php")
            val paramsBuilder = StringBuilder()
            paramsBuilder.append("id_usuario=${idUsuario}")
            for (i in 0 until 3) {
                paramsBuilder.append("&original_id_pregunta${i+1}=${originales.getOrNull(i) ?: 0}")
                paramsBuilder.append("&nuevo_id_pregunta${i+1}=${nuevos.getOrNull(i) ?: 0}")
                paramsBuilder.append("&respuesta${i+1}=${java.net.URLEncoder.encode(respuestas.getOrNull(i) ?: "", "UTF-8")}")
            }

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(paramsBuilder.toString().toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            return@withContext json.optString("success") == "1"
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error al actualizar respuestas (Network): ${e.message}", e)
            return@withContext false
        }
    }

    private suspend fun registrarRespuestasSeguridad(correo: String, preguntas: List<Pair<Int, String>>): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/preguntas/registrar_respuestas_seguridad.php")

            val jsonObj = org.json.JSONObject()
            jsonObj.put("correo", correo)
            val arr = org.json.JSONArray()
            for (p in preguntas) {
                val o = org.json.JSONObject()
                o.put("id_pregunta", p.first)
                o.put("respuesta", p.second)
                arr.put(o)
            }
            jsonObj.put("preguntas", arr)

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.outputStream.write(jsonObj.toString().toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            return@withContext json.optString("success") == "1"
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_conductor", "Error al registrar respuestas seguridad: ${e.message}", e)
            return@withContext false
        }
    }
}