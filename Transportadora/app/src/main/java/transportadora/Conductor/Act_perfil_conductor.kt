package transportadora.Conductor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.Cliente.Ciudad_almacenados
import transportadora.Almacenados.Cliente.Departamento_almacenados
import transportadora.Almacenados.Cliente.Genero_almacen
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Almacenados.Conductor.Perfil_conductor_completo_almacenados
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion

class Act_perfil_conductor : AppCompatActivity() {
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()

    // Spinners
    private lateinit var spinner_tipos_id: Spinner
    private lateinit var spinner_paises: Spinner
    private lateinit var spinner_departamentos: Spinner
    private lateinit var spinner_ciudades: Spinner
    private lateinit var spinner_nacionalidad: Spinner
    private lateinit var spinner_genero: Spinner

    // EditText/TextView
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtTel1: EditText
    private lateinit var txtTel2: EditText
    private lateinit var txtCorreo: TextView
    private lateinit var txtDireccion: EditText
    private lateinit var txtPlaca: EditText

    private var id_conductor_actual: Int = -1
    private var userEmail: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_conductor)

        // Obtener el correo del SharedPreferences si no viene del intent
        userEmail = intent.getStringExtra("USER_EMAIL") ?: run {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.getString("user_email", null)
        }

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo del conductor.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Initialize views
        spinner_tipos_id = findViewById(R.id.txt_tipo_id)
        spinner_paises = findViewById(R.id.txt_pais)
        spinner_departamentos = findViewById(R.id.txt_departamento)
        spinner_ciudades = findViewById(R.id.txt_ciudad)
        spinner_nacionalidad = findViewById(R.id.txt_nacionalidad)
        spinner_genero = findViewById(R.id.spinner_genero)

        // Initialize EditText/TextView
        txtIdentificacion = findViewById(R.id.txt_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtTel1 = findViewById(R.id.txt_tel1)
        txtTel2 = findViewById(R.id.txt_tel2)
        txtCorreo = findViewById(R.id.txt_email)
        txtDireccion = findViewById(R.id.txt_dir)
        txtPlaca = findViewById(R.id.txt_placa)

        // Obtener ID del conductor
        CoroutineScope(Dispatchers.Main).launch {
            id_conductor_actual = obtenerIdConductorPorCorreo(userEmail!!)
            if (id_conductor_actual == -1) {
                Toast.makeText(this@Act_perfil_conductor, "Error: No se pudo obtener el ID del conductor. Verifica tu sesión.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Load all static data first
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
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
                        Perfil_conductor_completo_almacenados.obtenerPerfilCompleto(userEmail!!)
                    }

                    if (perfil != null) {
                        Log.d("Act_perfil_conductor", "Cargando perfil del conductor: ${perfil.nombre}")
                        Log.d("Act_perfil_conductor", "Placa del vehículo: ${perfil.placa}")

                        // Tipo de identificación
                        val tipoIdSeleccionado = perfil.tipo_identificacion
                        val listaDescripcionesId = listatiposid.map { it.descripcion }
                        val indexId = listaDescripcionesId.indexOf(tipoIdSeleccionado)
                        if (indexId != -1) {
                            spinner_tipos_id.setSelection(indexId)
                        }

                        txtIdentificacion.setText(perfil.identificacion)
                        txtNombre.setText(perfil.nombre)
                        txtCorreo.text = perfil.correo
                        txtDireccion.setText(perfil.direccion)
                        txtPlaca.setText(perfil.placa)

                        // Género
                        val generoSeleccionado = perfil.genero
                        val listaDescripcionesGenero = listagenero.map { it.descripcion }
                        var indexGenero = listaDescripcionesGenero.indexOf(generoSeleccionado)

                        // Si no encuentra por descripción, intenta por ID
                        if (indexGenero == -1) {
                            try {
                                val generoId = generoSeleccionado?.toIntOrNull()
                                if (generoId != null) {
                                    indexGenero = listagenero.indexOfFirst { it.id_genero == generoId }
                                }
                            } catch (e: Exception) {
                                // ignore
                            }
                        }

                        if (indexGenero != -1) {
                            spinner_genero.setSelection(indexGenero)
                        }

                        val listaNombresPaises = listaPaisesCompleta.map { it.nombre }

                        // Nacionalidad
                        val nacionalidadSeleccionada = perfil.nacionalidad
                        val indexNacionalidad = listaNombresPaises.indexOf(nacionalidadSeleccionada)
                        if (indexNacionalidad != -1) {
                            spinner_nacionalidad.setSelection(indexNacionalidad)
                        }

                        // País de residencia
                        val paisResidenciaSeleccionado = perfil.pais_residencia
                        val indexPaisResidencia = listaNombresPaises.indexOf(paisResidenciaSeleccionado)
                        if (indexPaisResidencia != -1) {
                            spinner_paises.setSelection(indexPaisResidencia)
                        }

                        // Cargar departamentos y ciudades para el país seleccionado
                        val selectedPais = listaPaisesCompleta.getOrNull(spinner_paises.selectedItemPosition)
                        selectedPais?.let { pais ->
                            cargarDepartamentos(pais.id_pais, spinner_departamentos, perfil.departamento) {
                                val selectedDepto = departamentosOrigen.getOrNull(spinner_departamentos.selectedItemPosition)
                                selectedDepto?.let { depto ->
                                    cargarCiudades(pais.id_pais, depto, spinner_ciudades, perfil.ciudad)
                                }
                            }
                        }

                        // Teléfonos
                        val telefonosExistentes = perfil.telefonos.filter { it.isNotBlank() }
                        txtTel1.setText(telefonosExistentes.getOrNull(0) ?: "")
                        txtTel2.setText(telefonosExistentes.getOrNull(1) ?: "")

                    } else {
                        Toast.makeText(this@Act_perfil_conductor, "No se pudo cargar el perfil.", Toast.LENGTH_LONG).show()
                    }

                }

            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_conductor, "Error general al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Set up listeners for cascading spinners
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

        // Listeners de botones
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val buttonDescartar = findViewById<Button>(R.id.buttonDescartar)
        val buttonGuardar = findViewById<Button>(R.id.buttonGuardar)

        // Botón DESCARTAR → Confirmación antes de salir
        buttonDescartar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Descartar cambios")
            builder.setMessage("¿Deseas descartar los cambios realizados?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        // Botón GUARDAR
        buttonGuardar.setOnClickListener {
            if (id_conductor_actual == -1) {
                Toast.makeText(this, "Error: No se encontró el ID del conductor. Recarga la pantalla.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val postipoidentificacion = spinner_tipos_id.selectedItemPosition
            val id_tipo_identificacion = postipoidentificacion + 1 // Índice + 1

            if (id_tipo_identificacion <= 0) {
                Toast.makeText(this, "Error: Tipo de identificacion no válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val numero_identificacion: String = txtIdentificacion.text.toString().trim()
            val nombrecompleto: String = txtNombre.text.toString().trim()
            val nuevoCorreo: String = txtCorreo.text.toString().trim()
            val direccion: String = txtDireccion.text.toString().trim()
            val placa: String = txtPlaca.text.toString().trim()
            val tel1: String = txtTel1.text.toString().trim()
            val tel2: String = txtTel2.text.toString().trim()

            val pais = spinner_paises.selectedItem.toString()
            val departamento = spinner_departamentos.selectedItem.toString()
            val ciudad = spinner_ciudades.selectedItem.toString()

            val posGenero = spinner_genero.selectedItemPosition
            val id_genero = posGenero + 1 // Índice + 1

            val posNacionalidad = spinner_nacionalidad.selectedItemPosition
            val id_pais_nacionalidad = posNacionalidad + 1 // Índice + 1

            val correoOriginal = userEmail
            val cambioCorreo: Boolean = nuevoCorreo != correoOriginal

            // Validaciones básicas
            if (numero_identificacion.isEmpty()) {
                Toast.makeText(this, "El número de identificación es obligatorio", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (nombrecompleto.isEmpty()) {
                Toast.makeText(this, "El nombre completo es obligatorio", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (placa.isEmpty()) {
                Toast.makeText(this, "La placa del vehículo es obligatoria", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // Obtener código postal desde el PHP
                    val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)

                    if (codigoPostal != null) {
                        val actualizacionExitosa = actualizarPerfilConductor(
                            nuevoCorreo,
                            id_tipo_identificacion,
                            numero_identificacion,
                            nombrecompleto,
                            direccion,
                            codigoPostal,
                            placa,
                            id_pais_nacionalidad,
                            id_genero,
                            tel1,
                            tel2
                        )

                        if (actualizacionExitosa) {
                            if (cambioCorreo) {
                                Toast.makeText(this@Act_perfil_conductor, "¡Correo actualizado! Por favor, vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()

                                // Limpiar SharedPreferences
                                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                prefs.edit().clear().apply()

                                // Redirigir al Main y limpiar stack de actividades
                                val intent = Intent(this@Act_perfil_conductor, transportadora.Compartido.Main::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish() // Cerrar la actividad actual
                            }
                            else {
                                Toast.makeText(this@Act_perfil_conductor, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Act_perfil_conductor, Perfil_conductor::class.java)
                                startActivity(intent)
                                finish()
                            }

                        } else {
                            Toast.makeText(this@Act_perfil_conductor, "Error al guardar datos. Revisa la información.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(this@Act_perfil_conductor, "Error: No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Act_perfil_conductor, "Error de red al obtener Código Postal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner, departamentoSeleccionado: String? = null, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                departamentosOrigen = listaNombres

                spinner.adapter = ArrayAdapter(this@Act_perfil_conductor, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                departamentoSeleccionado?.let {
                    val deptoIndex = listaNombres.indexOf(it)
                    if (deptoIndex != -1) {
                        spinner.setSelection(deptoIndex)
                        delay(100)
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
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades

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
                Log.d("Act_perfil_conductor", "ID Conductor obtenido: $idConductor")
            } else {
                Log.e("Act_perfil_conductor", "Error PHP al obtener ID: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Act_perfil_conductor", "Error de red/JSON al obtener ID: ${e.message}", e)
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
                Log.d("Act_perfil_conductor", "Código Postal obtenido: $codigoPostal")
            } else {
                Log.e("Act_perfil_conductor", "Error PHP al obtener Código Postal: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Act_perfil_conductor", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    private suspend fun actualizarPerfilConductor(
        correo: String,
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        codigoPostal: String,
        placa: String,
        idPaisNacionalidad: Int,
        idGenero: Int,
        tel1: String,
        tel2: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/conductor/perfil/actualizar_perfil.php")

            val params = "correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" +
                    "&id_tipo_identificacion=$idTipoIdentificacion" +
                    "&identificacion=${java.net.URLEncoder.encode(identificacion, "UTF-8")}" +
                    "&nombre=${java.net.URLEncoder.encode(nombre, "UTF-8")}" +
                    "&direccion=${java.net.URLEncoder.encode(direccion, "UTF-8")}" +
                    "&codigo_postal=${java.net.URLEncoder.encode(codigoPostal, "UTF-8")}" +
                    "&placa=${java.net.URLEncoder.encode(placa, "UTF-8")}" +
                    "&id_pais_nacionalidad=$idPaisNacionalidad" +
                    "&id_genero=$idGenero" +
                    "&tel1=${java.net.URLEncoder.encode(tel1, "UTF-8")}" +
                    "&tel2=${java.net.URLEncoder.encode(tel2, "UTF-8")}"

            Log.d("Act_perfil_conductor", "Sending params: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val responseCode = connection.responseCode
            Log.d("Act_perfil_conductor", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                Log.d("Act_perfil_conductor", "Received response: $response")

                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided"
                Log.e("Act_perfil_conductor", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Act_perfil_conductor", "Error al actualizar perfil (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }
}