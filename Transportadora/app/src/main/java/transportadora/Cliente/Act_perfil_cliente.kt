package transportadora.Cliente

import android.annotation.SuppressLint
import android.widget.AdapterView
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
import transportadora.Almacenados.Cliente.Perfil_cliente_completo_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Compartido.Registrar2
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion
import transportadora.Almacenados.Cliente.Departamento_almacenados
import transportadora.Almacenados.Cliente.Ciudad_almacenados
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.EditText
import kotlinx.coroutines.delay

class Act_perfil_cliente : AppCompatActivity() {
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()
    private var listaCiudadesDestino: List<Ciudad> = emptyList()

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
    private lateinit var txtCorreo: TextView // Email is typically not editable
    private lateinit var txtDireccion: EditText

    private var id_cliente_actual: Int = -1
    private var userEmail: String? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_cliente)

        userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo del usuario.", Toast.LENGTH_LONG).show()
        } else {
            // 1. Obtener el id_cliente por correo (nueva lógica)
            CoroutineScope(Dispatchers.Main).launch {
                id_cliente_actual = obtenerIdClientePorCorreo(userEmail!!)
                if (id_cliente_actual == -1) {
                    Toast.makeText(this@Act_perfil_cliente, "Error: No se pudo obtener el ID del cliente. Verifica tu sesión.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Initialize views
        spinner_tipos_id = findViewById(R.id.txt_tipo_id)
        spinner_paises = findViewById(R.id.txt_pais)
        spinner_departamentos = findViewById(R.id.txt_departamento)
        spinner_ciudades = findViewById(R.id.txt_ciudad)
        spinner_nacionalidad = findViewById(R.id.txt_nacionalidad)
        spinner_genero = findViewById(R.id.txt_genero)

        // Initialize EditText/TextView (using EditText for editable fields)
        txtIdentificacion = findViewById(R.id.txt_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtTel1 = findViewById(R.id.txt_tel1)
        txtTel2 = findViewById(R.id.txt_tel2)
        txtCorreo = findViewById(R.id.txt_email)
        txtDireccion = findViewById(R.id.txt_dir)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Load all static data first
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinner_nacionalidad.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }

                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    listagenero = generos
                    val listageneros = generos.map { it.descripcion }
                    spinner_genero.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listageneros).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron generos", Toast.LENGTH_SHORT).show()
                }

                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron tipos de identificacion", Toast.LENGTH_SHORT).show()
                }

                // Now load profile data and perform pre-selection
                if (userEmail != null) {
                    val perfil = withContext(Dispatchers.IO) {
                        transportadora.Almacenados.Cliente.Datos_perfil_almacenados.obtener_datos_perfil(
                            userEmail!!
                        )
                    }

                    if (perfil != null) {
                        // Asignar cada campo del modelo a su respectivo EditText/TextView
                        val tipoIdSeleccionado = perfil.tipo_identificacion
                        val listaDescripcionesId = listatiposid.map { it.descripcion }
                        val indexId = listaDescripcionesId.indexOf(tipoIdSeleccionado)

                        if (indexId != -1) {
                            spinner_tipos_id.setSelection(indexId)
                        }
                        txtIdentificacion.setText(perfil.identificacion)
                        txtNombre.setText(perfil.nombre)
                        txtCorreo.text = perfil.correo // TextView: show email
                        txtDireccion.setText(perfil.direccion)

                        val generoSeleccionado = perfil.genero
                        val listaDescripcionesGenero = listagenero.map { it.descripcion }
                        val indexGenero = listaDescripcionesGenero.indexOf(generoSeleccionado)

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
                            spinner_paises.setSelection(indexPaisResidencia)
                        }

                        // Load departments for the selected country and pre-select the user's department
                        val selectedPais = listaPaisesCompleta.getOrNull(spinner_paises.selectedItemPosition)
                        selectedPais?.let { pais ->
                            cargarDepartamentos(pais.id_pais, spinner_departamentos, perfil.departamento) {
                                // Callback after departments are loaded and selected
                                val selectedDepto = departamentosOrigen.getOrNull(spinner_departamentos.selectedItemPosition)
                                selectedDepto?.let { depto ->
                                    cargarCiudades(pais.id_pais, depto, spinner_ciudades, perfil.ciudad)
                                }
                            }
                        }

                        // Teléfonos (usa "" si no hay para un EditText)
                        txtTel1.setText(perfil.telefonos.getOrNull(0) ?: "")
                        txtTel2.setText(perfil.telefonos.getOrNull(1) ?: "")

                    } else {
                        Toast.makeText(this@Act_perfil_cliente, "No se pudo cargar el perfil.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@Act_perfil_cliente, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error general al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
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

        // escuchar botones y volver
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
                finish() // Cierra la actividad
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Solo cierra el diálogo
            }
            builder.show()
        }

        // Botón GUARDAR → Muestra toast y abre la siguiente pantalla
        buttonGuardar.setOnClickListener {

            if (id_cliente_actual == -1) {
                Toast.makeText(this, "Error: No se encontró el ID del cliente. Recarga la pantalla.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Usar los miembros de la clase para obtener los valores de Spinners
            val postipoidentificacion = spinner_tipos_id.selectedItemPosition
            val id_tipo_identificacion = postipoidentificacion + 1

            if (id_tipo_identificacion <= 0) { // Corregido: index + 1 debe ser > 0
                Toast.makeText(this, "Error: Tipo de identificacion no válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Usar los miembros de la clase para obtener los valores de EditText/TextView
            val numero_identificacion: String = txtIdentificacion.text.toString().trim()
            val nombrecompleto: String = txtNombre.text.toString().trim()
            val correo: String = txtCorreo.text.toString().trim() // Lectura del TextView/EditText
            val direccion: String = txtDireccion.text.toString().trim()
            val tel1: String = txtTel1.text.toString().trim()
            val tel2: String = txtTel2.text.toString().trim()

            val posgenero = spinner_genero.selectedItemPosition
            val id_genero = posgenero + 1

            if (id_genero <= 0) {
                Toast.makeText(this, "Error: genero no válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val posnacionalidad = spinner_nacionalidad.selectedItemPosition
            val id_nacionalidad = posnacionalidad + 1

            if (id_nacionalidad <= 0) {
                Toast.makeText(this, "Error: Nacionalidad no válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Usar los miembros de la clase para obtener los valores de ubicación
            val pais=spinner_paises.selectedItem.toString()
            val departamento=spinner_departamentos.selectedItem.toString()
            val ciudad=spinner_ciudades.selectedItem.toString()

            // 3. Llamada Coroutine para obtener Código Postal y luego actualizar el perfil
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)

                    if (codigoPostal != null) { //se trajo el codigo postal
                        // Paso B: Actualizar el perfil con el Código Postal
                        val actualizacionExitosa = actualizarPerfil(
                            id_cliente_actual,
                            id_tipo_identificacion,
                            numero_identificacion,
                            nombrecompleto,
                            direccion,
                            correo,
                            id_genero,
                            id_nacionalidad,
                            codigoPostal,
                            tel1,
                            tel2
                        )

                        if (actualizacionExitosa) {
                            // Si la actualización fue exitosa:
                            Toast.makeText(this@Act_perfil_cliente, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@Act_perfil_cliente, Perfil_cliente::class.java)
                            intent.putExtra("USER_EMAIL", userEmail)
                            startActivity(intent)
                            finish() // Cerrar la actividad de edición
                        } else {
                            // Si la actualización falló (aunque se obtuvo el CP)
                            Toast.makeText(this@Act_perfil_cliente, "Error al guardar datos. Revisa la información.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        // Manejo de error si no se encuentra el Código Postal
                        Toast.makeText(this@Act_perfil_cliente, "Error: No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Act_perfil_cliente, "Error de red al obtener Código Postal: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            // SE ELIMINARON LAS LÍNEAS REDUNDANTES DE TOAST E INTENT, YA QUE LA LÓGICA ASÍNCRONA LAS MANEJA.
        }

    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner, departamentoSeleccionado: String? = null, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { transportadora.Almacenados.Cliente.Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                departamentosOrigen = listaNombres

                spinner.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                departamentoSeleccionado?.let {
                    val deptoIndex = listaNombres.indexOf(it)
                    if (deptoIndex != -1) {
                        spinner.setSelection(deptoIndex)
                        // Pequeño retraso para que el UI se actualice antes de cargar ciudades
                        delay(100)
                    }
                }
                onComplete?.invoke()
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner, ciudadSeleccionada: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { transportadora.Almacenados.Cliente.Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades

                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                ciudadSeleccionada?.let {
                    val ciudadIndex = listaNombres.indexOf(it)
                    if (ciudadIndex != -1) {
                        spinner.setSelection(ciudadIndex)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun obtenerIdClientePorCorreo(email: String): Int = withContext(Dispatchers.IO) {
        var idCliente = -1
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/ruta/obtener_id_cliente_por_correo.php")
            val params = "correo=${java.net.URLEncoder.encode(email, "UTF-8")}"

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                // El campo se llama 'id_cliente' en el JSON de respuesta
                idCliente = json.getInt("id_cliente")
                android.util.Log.d("Act_perfil_cliente", "ID Cliente obtenido: $idCliente")
            } else {
                android.util.Log.e("Act_perfil_cliente", "Error PHP al obtener ID: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_cliente", "Error de red/JSON al obtener ID: ${e.message}", e)
        }
        return@withContext idCliente
    }

    private suspend fun obtenerCodigoPostal(pais: String, departamento: String, ciudad: String): String? = withContext(Dispatchers.IO) {
        var codigoPostal: String? = null
        try {
            // URL a tu nuevo script PHP
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/perfil/obtener_codigo_postal.php")

            // Parámetros a enviar
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
                // El campo que retorna tu PHP se llama 'codigo_postal' dentro del objeto 'datos'
                codigoPostal = json.getJSONObject("datos").getString("codigo_postal")
                android.util.Log.d("Act_perfil_cliente", "Código Postal obtenido: $codigoPostal")
            } else {
                android.util.Log.e("Act_perfil_cliente", "Error PHP al obtener Código Postal: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_cliente", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    // Asegúrate de que esta función esté dentro de la clase Act_perfil_cliente
    private suspend fun actualizarPerfil(
        idCliente: Int,
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        correo: String,
        idGenero: Int,
        idNacionalidad: Int,
        codigoPostal: String,
        tel1: String,
        tel2: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // RUTA CORREGIDA (confirmada por tu estructura de archivos)
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/perfil/actualizar_datos_perfil.php")

            // Construcción de los parámetros POST
            val params = "id_cliente=$idCliente" +
                    "&id_tipo_identificacion=$idTipoIdentificacion" +
                    "&identificacion=${java.net.URLEncoder.encode(identificacion, "UTF-8")}" +
                    "&nombre=${java.net.URLEncoder.encode(nombre, "UTF-8")}" +
                    "&direccion=${java.net.URLEncoder.encode(direccion, "UTF-8")}" +
                    "&correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" +
                    "&id_genero=$idGenero" +
                    "&id_nacionalidad=$idNacionalidad" +
                    "&codigo_postal=${java.net.URLEncoder.encode(codigoPostal, "UTF-8")}" +
                    "&tel1=${java.net.URLEncoder.encode(tel1, "UTF-8")}" +
                    "&tel2=${java.net.URLEncoder.encode(tel2, "UTF-8")}"

            android.util.Log.d("Act_perfil_cliente", "Sending params: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            // **Añadir manejo de código de respuesta para evitar FileNotFoundException**
            val responseCode = connection.responseCode
            android.util.Log.d("Act_perfil_cliente", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) { // Código 200: Éxito
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                android.util.Log.d("Act_perfil_cliente", "Received response: $response")

                // Verificar si el JSON indica éxito
                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                // Manejar errores (4xx o 5xx) leyendo el errorStream para diagnóstico
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided"
                android.util.Log.e("Act_perfil_cliente", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            android.util.Log.e("Act_perfil_cliente", "Error al actualizar perfil (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }
}