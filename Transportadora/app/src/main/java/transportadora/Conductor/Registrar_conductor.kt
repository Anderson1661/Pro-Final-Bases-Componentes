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
import transportadora.Almacenados.Cliente.Preguntas_almacenados
import transportadora.Almacenados.Conductor.Color_almacenados
import transportadora.Almacenados.Conductor.Linea_almacenados
import transportadora.Almacenados.Conductor.Marca_almacenados
import transportadora.Almacenados.Conductor.Tipo_servicios_almacenados
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Tipo_identificacion
import transportadora.Modelos.Cliente.Pregunta
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Conductor.Color_vehiculo
import transportadora.Modelos.Conductor.Linea_vehiculo
import transportadora.Modelos.Conductor.Marca_vehiculo
import transportadora.Modelos.Conductor.Tipo_servicio
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import transportadora.Compartido.Main

class Registrar_conductor : AppCompatActivity() {

    // Listas para datos personales
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var listapreguntasCompleta: List<Pregunta> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()

    // Listas para datos del vehículo
    private var listaMarcas: List<Marca_vehiculo> = emptyList()
    private var listaColores: List<Color_vehiculo> = emptyList()
    private var listaTiposServicio: List<Tipo_servicio> = emptyList()
    private var listaLíneas: List<Linea_vehiculo> = emptyList()

    // Spinners datos personales
    private lateinit var spinner_tipos_id: Spinner
    private lateinit var spinner_paises: Spinner
    private lateinit var spinner_departamentos: Spinner
    private lateinit var spinner_ciudades: Spinner
    private lateinit var spinner_nacionalidad: Spinner
    private lateinit var spinner_genero: Spinner
    private lateinit var spinner_cantidad_tel: Spinner

    // Spinners vehículo
    private lateinit var spinner_marca: Spinner
    private lateinit var spinner_linea: Spinner
    private lateinit var spinner_color: Spinner
    private lateinit var spinner_servicio: Spinner

    // Spinners preguntas seguridad
    private lateinit var spinner_pregunta1: Spinner
    private lateinit var spinner_pregunta2: Spinner
    private lateinit var spinner_pregunta3: Spinner

    // EditText datos personales
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtTel1: EditText
    private lateinit var txtTel2: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText

    // EditText vehículo
    private lateinit var txtPlaca: EditText
    private lateinit var txtModelo: EditText

    // EditText preguntas seguridad
    private lateinit var txt_respuesta1: EditText
    private lateinit var txt_respuesta2: EditText
    private lateinit var txt_respuesta3: EditText

    // Variables para IDs de preguntas
    private var idPregunta1: Int? = null
    private var idPregunta2: Int? = null
    private var idPregunta3: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_conductor)

        // Inicializar vistas de datos personales
        initVistasPersonales()

        // Inicializar vistas de vehículo
        initVistasVehiculo()

        // Inicializar vistas de seguridad
        initVistasSeguridad()

        // Configurar listeners
        configurarListeners()

        // Cargar datos iniciales
        cargarDatosIniciales()

        // Botones
        val txtVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolver.setOnClickListener { finish() }

        val btnRegistrar = findViewById<Button>(R.id.buttonRegistrar)
        btnRegistrar.setOnClickListener { registrarNuevoConductor() }
    }

    private fun initVistasPersonales() {
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
    }

    private fun initVistasVehiculo() {
        spinner_marca = findViewById(R.id.spinner_marca)
        spinner_linea = findViewById(R.id.spinner_linea)
        spinner_color = findViewById(R.id.spinner_color)
        spinner_servicio = findViewById(R.id.spinner_servicio)

        txtPlaca = findViewById(R.id.txt_placa)
        txtModelo = findViewById(R.id.txt_modelo)
    }

    private fun initVistasSeguridad() {
        spinner_pregunta1 = findViewById(R.id.txt_pregunta1)
        spinner_pregunta2 = findViewById(R.id.txt_pregunta2)
        spinner_pregunta3 = findViewById(R.id.txt_pregunta3)

        txt_respuesta1 = findViewById(R.id.txt_respuesta1)
        txt_respuesta2 = findViewById(R.id.txt_respuesta2)
        txt_respuesta3 = findViewById(R.id.txt_respuesta3)
    }

    private fun configurarListeners() {
        // Listener para cantidad de teléfonos
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

        // Listeners para spinners en cascada (ubicación)
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

        // Listener para spinner de marcas (cargar líneas)
        spinner_marca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0 && listaMarcas.isNotEmpty()) {
                    val marcaSeleccionada = listaMarcas[position]
                    cargarLineas(marcaSeleccionada.id_marca)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun cargarDatosIniciales() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Cargar países
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    setupSpinner(spinner_paises, listapaisesNombres)
                    setupSpinner(spinner_nacionalidad, listapaisesNombres)
                }

                // Cargar géneros
                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    listagenero = generos
                    val listageneros = generos.map { it.descripcion }
                    setupSpinner(spinner_genero, listageneros)
                }

                // Cargar tipos identificación
                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    setupSpinner(spinner_tipos_id, listatipos)
                }

                // Cargar datos del vehículo
                cargarDatosVehiculo()

                // Cargar preguntas seguridad
                val preguntas = withContext(Dispatchers.IO) { Preguntas_almacenados.obtener_preguntas() }
                if (preguntas.isNotEmpty()) {
                    listapreguntasCompleta = preguntas
                    configurarPreguntasSeguridad()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun cargarDatosVehiculo() {
        try {
            // Cargar marcas
            val marcas = withContext(Dispatchers.IO) { Marca_almacenados.obtener_marcas() }
            if (marcas.isNotEmpty()) {
                listaMarcas = marcas
                val nombresMarcas = marcas.map { it.nombre_marca }
                setupSpinner(spinner_marca, nombresMarcas)

                // Cargar líneas de la primera marca por defecto
                if (marcas.isNotEmpty()) {
                    cargarLineas(marcas[0].id_marca)
                }
            }

            // Cargar colores
            val colores = withContext(Dispatchers.IO) { Color_almacenados.obtener_colores() }
            if (colores.isNotEmpty()) {
                listaColores = colores
                val nombresColores = colores.map { it.descripcion }
                setupSpinner(spinner_color, nombresColores)
            }

            // Cargar tipos de servicio
            val tiposServicio = withContext(Dispatchers.IO) { Tipo_servicios_almacenados.obtener_tipos_servicio() }
            if (tiposServicio.isNotEmpty()) {
                listaTiposServicio = tiposServicio
                val nombresTipos = tiposServicio.map { it.descripcion }
                setupSpinner(spinner_servicio, nombresTipos)
            }

        } catch (e: Exception) {
            Log.e("Registrar_conductor", "Error cargar datos vehículo: ${e.message}")
        }
    }

    private fun cargarLineas(idMarca: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val lineas = withContext(Dispatchers.IO) { Linea_almacenados.obtenerLineas(idMarca) }
                listaLíneas = lineas
                val nombresLineas = lineas.map { it.linea }
                setupSpinner(spinner_linea, nombresLineas)
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar líneas", Toast.LENGTH_SHORT).show()
                Log.e("Registrar_conductor", "Error cargar líneas: ${e.message}")
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, datos: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun configurarPreguntasSeguridad() {
        val listaDescripciones = listapreguntasCompleta.map { it.descripcion }

        // Configurar spinners con todas las preguntas disponibles
        setupSpinner(spinner_pregunta1, listaDescripciones)
        setupSpinner(spinner_pregunta2, listaDescripciones)
        setupSpinner(spinner_pregunta3, listaDescripciones)

        // Inicializar IDs con las primeras tres preguntas por defecto
        idPregunta1 = listapreguntasCompleta.getOrNull(0)?.id_pregunta
        idPregunta2 = listapreguntasCompleta.getOrNull(1)?.id_pregunta
        idPregunta3 = listapreguntasCompleta.getOrNull(2)?.id_pregunta

        // Configurar listeners simples que solo actualicen los IDs
        configurarListenersPreguntas()
    }

    private fun configurarListenersPreguntas() {
        spinner_pregunta1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcion = parent.getItemAtPosition(position).toString()
                idPregunta1 = listapreguntasCompleta.find { it.descripcion == descripcion }?.id_pregunta
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_pregunta2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcion = parent.getItemAtPosition(position).toString()
                idPregunta2 = listapreguntasCompleta.find { it.descripcion == descripcion }?.id_pregunta
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_pregunta3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val descripcion = parent.getItemAtPosition(position).toString()
                idPregunta3 = listapreguntasCompleta.find { it.descripcion == descripcion }?.id_pregunta
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                setupSpinner(spinner, listaNombres)
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar departamentos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades
                val listaNombres = ciudades.map { it.nombre }
                setupSpinner(spinner, listaNombres)
            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error al cargar ciudades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarNuevoConductor() {
        // Validar campos obligatorios
        if (!validarCampos()) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener código postal
                val pais = spinner_paises.selectedItem?.toString() ?: ""
                val departamento = spinner_departamentos.selectedItem?.toString() ?: ""
                val ciudad = spinner_ciudades.selectedItem?.toString() ?: ""

                val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)

                if (codigoPostal != null) {
                    // Registrar conductor
                    val exito = registrarConductorCompleto(codigoPostal)

                    if (exito) {
                        Toast.makeText(
                            this@Registrar_conductor,
                            "Registro exitoso. Tu contraseña inicial es tu número de documento.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@Registrar_conductor, Main::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Registrar_conductor, "Error al registrar conductor", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@Registrar_conductor, "Error: No se encontró código postal", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar_conductor, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validarCampos(): Boolean {
        // Datos personales
        if (txtIdentificacion.text.isEmpty() || txtNombre.text.isEmpty() ||
            txtCorreo.text.isEmpty() || txtDireccion.text.isEmpty() ||
            txtTel1.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos personales obligatorios", Toast.LENGTH_LONG).show()
            return false
        }

        // Datos vehículo
        if (txtPlaca.text.isEmpty() || txtModelo.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los datos del vehículo", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar modelo
        val modelo = txtModelo.text.toString().toIntOrNull()
        if (modelo == null || modelo < 2010) {
            Toast.makeText(this, "El modelo debe ser 2010 o superior", Toast.LENGTH_LONG).show()
            return false
        }

        // Preguntas seguridad
        if (txt_respuesta1.text.isEmpty() || txt_respuesta2.text.isEmpty() || txt_respuesta3.text.isEmpty()) {
            Toast.makeText(this, "Complete todas las respuestas de seguridad", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar preguntas únicas
        if (idPregunta1 == idPregunta2 || idPregunta1 == idPregunta3 || idPregunta2 == idPregunta3) {
            Toast.makeText(this, "Las preguntas de seguridad deben ser diferentes", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private suspend fun obtenerCodigoPostal(pais: String, departamento: String, ciudad: String): String? =
        withContext(Dispatchers.IO) {
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
                    return@withContext json.getJSONObject("datos").getString("codigo_postal")
                }
            } catch (e: Exception) {
                Log.e("Registrar_conductor", "Error obtener código postal: ${e.message}")
            }
            return@withContext null
        }

    private suspend fun registrarConductorCompleto(codigoPostal: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Preparar datos personales
            val idTipoIdentificacion = spinner_tipos_id.selectedItemPosition + 1
            val identificacion = txtIdentificacion.text.toString()
            val nombre = txtNombre.text.toString()
            val direccion = txtDireccion.text.toString()
            val correo = txtCorreo.text.toString()
            val idGenero = spinner_genero.selectedItemPosition + 1
            val posNacionalidad = spinner_nacionalidad.selectedItemPosition
            val idNacionalidad = listaPaisesCompleta.getOrNull(posNacionalidad)?.id_pais ?: -1
            val tel1 = txtTel1.text.toString()
            val tel2 = if (txtTel2.isEnabled) txtTel2.text.toString() else ""

            // Preparar datos vehículo
            val placa = txtPlaca.text.toString()

            // Obtener IDs de los elementos seleccionados
            val posMarca = spinner_marca.selectedItemPosition
            val idMarca = if (posMarca >= 0 && listaMarcas.isNotEmpty()) listaMarcas[posMarca].id_marca else -1
            val marcaNombre = if (posMarca >= 0 && listaMarcas.isNotEmpty()) listaMarcas[posMarca].nombre_marca else ""

            val posLinea = spinner_linea.selectedItemPosition
            val linea = if (posLinea >= 0 && listaLíneas.isNotEmpty()) listaLíneas[posLinea].linea else ""

            val modelo = txtModelo.text.toString().toInt()

            val posColor = spinner_color.selectedItemPosition
            val idColor = if (posColor >= 0 && listaColores.isNotEmpty()) listaColores[posColor].id_color else -1
            val colorNombre = if (posColor >= 0 && listaColores.isNotEmpty()) listaColores[posColor].descripcion else ""

            val posServicio = spinner_servicio.selectedItemPosition
            val idTipoServicio = if (posServicio >= 0 && listaTiposServicio.isNotEmpty()) listaTiposServicio[posServicio].id_tipo_servicio else -1
            val servicioNombre = if (posServicio >= 0 && listaTiposServicio.isNotEmpty()) listaTiposServicio[posServicio].descripcion else ""

            // Preguntas seguridad
            val idP1 = idPregunta1 ?: -1
            val idP2 = idPregunta2 ?: -1
            val idP3 = idPregunta3 ?: -1
            val res1 = txt_respuesta1.text.toString()
            val res2 = txt_respuesta2.text.toString()
            val res3 = txt_respuesta3.text.toString()

            // URL para foto por defecto (mantenida como solicitaste)
            val urlFoto = "https://guillermogonzalezpimiento.com/wp-content/uploads/2022/06/Sin-foto-de-perfil-1024x1024.png"

            // Llamar al PHP de registro
            val url = java.net.URL(ApiConfig.BASE_URL + "consultas/conductor/registrar_conductor.php")

            val jsonParams = org.json.JSONObject().apply {
                put("id_tipo_identificacion", idTipoIdentificacion)
                put("identificacion", identificacion)
                put("nombre", nombre)
                put("direccion", direccion)
                put("correo", correo)
                put("id_genero", idGenero)
                put("id_pais_nacionalidad", idNacionalidad)
                put("codigo_postal", codigoPostal)
                put("telefonos", org.json.JSONArray().apply {
                    put(tel1)
                    if (tel2.isNotEmpty()) put(tel2)
                })
                put("vehiculo", org.json.JSONObject().apply {
                    put("placa", placa)
                    put("marca", marcaNombre)
                    put("linea", linea)
                    put("modelo", modelo)
                    put("color", colorNombre)
                    put("tipo_servicio", servicioNombre)
                })
                put("url_foto", urlFoto)
                put("preguntas_seguridad", org.json.JSONArray().apply {
                    put(org.json.JSONObject().apply {
                        put("id_pregunta", idP1)
                        put("respuesta", res1)
                    })
                    put(org.json.JSONObject().apply {
                        put("id_pregunta", idP2)
                        put("respuesta", res2)
                    })
                    put(org.json.JSONObject().apply {
                        put("id_pregunta", idP3)
                        put("respuesta", res3)
                    })
                })
            }

            val params = jsonParams.toString()
            Log.d("Registrar_conductor", "Enviando: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))

            val responseCode = connection.responseCode
            Log.d("Registrar_conductor", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                Log.d("Registrar_conductor", "Respuesta: $response")

                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No hay mensaje de error"
                Log.e("Registrar_conductor", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Registrar_conductor", "Error registro completo: ${e.message}", e)
            return@withContext false
        }
    }
}