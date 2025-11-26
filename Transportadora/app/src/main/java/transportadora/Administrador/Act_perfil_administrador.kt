package transportadora.Administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.Cliente.Ciudad_almacenados
import transportadora.Almacenados.Cliente.Departamento_almacenados
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Almacenados.Administrador.Perfil_administrador_completo_almacenados
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion
import transportadora.Login.R
import android.util.Log

class Act_perfil_administrador : AppCompatActivity() {
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()

    // Spinners
    private lateinit var spinner_tipos_id: Spinner
    private lateinit var spinner_paises: Spinner
    private lateinit var spinner_departamentos: Spinner
    private lateinit var spinner_ciudades: Spinner
    private lateinit var spinner_cantidad_tel: Spinner


    // EditText/TextView
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtTel1: EditText
    private lateinit var txtTel2: EditText
    private lateinit var txtCorreo: TextView
    private lateinit var txtDireccion: EditText

    private var id_administrador_actual: Int = -1
    private var userEmail: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_administrador)

        userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo del administrador.", Toast.LENGTH_LONG).show()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                id_administrador_actual = obtenerIdAdministradorPorCorreo(userEmail!!)
                if (id_administrador_actual == -1) {
                    Toast.makeText(this@Act_perfil_administrador, "Error: No se pudo obtener el ID del administrador. Verifica tu sesión.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Initialize views
        spinner_tipos_id = findViewById(R.id.txt_tipo_id)
        spinner_paises = findViewById(R.id.txt_pais)
        spinner_departamentos = findViewById(R.id.txt_departamento)
        spinner_ciudades = findViewById(R.id.txt_ciudad)
        spinner_cantidad_tel = findViewById(R.id.spinner_cantidad_tel)


        // Initialize EditText/TextView
        txtIdentificacion = findViewById(R.id.txt_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtTel1 = findViewById(R.id.txt_tel1)
        txtTel2 = findViewById(R.id.txt_tel2)
        txtCorreo = findViewById(R.id.txt_email)
        txtDireccion = findViewById(R.id.txt_dir)

        val opcionesCantidadTel = arrayOf("1", "2")
        val adapterCantidadTel = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesCantidadTel)
        adapterCantidadTel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_cantidad_tel.adapter = adapterCantidadTel

        // ESTABLECER VALOR POR DEFECTO A "1"
        spinner_cantidad_tel.setSelection(0)

        spinner_cantidad_tel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cantidadSeleccionada = parent.getItemAtPosition(position).toString().toIntOrNull() ?: 1
                manejarCamposTelefono(cantidadSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Por defecto, manejar como 1 teléfono
                manejarCamposTelefono(1)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Load all static data first
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Act_perfil_administrador, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_administrador, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }

                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Act_perfil_administrador, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_administrador, "No se encontraron tipos de identificacion", Toast.LENGTH_SHORT).show()
                }

                // Now load profile data and perform pre-selection
                if (userEmail != null) {
                    val perfil = withContext(Dispatchers.IO) {
                        Perfil_administrador_completo_almacenados.obtenerPerfilCompleto(userEmail!!)
                    }

                    if (perfil != null) {
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

                        val listaNombresPaises = listaPaisesCompleta.map { it.nombre }

                        val paisResidenciaSeleccionado = perfil.pais_residencia
                        val indexPaisResidencia = listaNombresPaises.indexOf(paisResidenciaSeleccionado)
                        if (indexPaisResidencia != -1) {
                            spinner_paises.setSelection(indexPaisResidencia)
                        }

                        // Load departments for the selected country and pre-select the user's department
                        val selectedPais = listaPaisesCompleta.getOrNull(spinner_paises.selectedItemPosition)
                        selectedPais?.let { pais ->
                            cargarDepartamentos(pais.id_pais, spinner_departamentos, perfil.departamento) {
                                val selectedDepto = departamentosOrigen.getOrNull(spinner_departamentos.selectedItemPosition)
                                selectedDepto?.let { depto ->
                                    cargarCiudades(pais.id_pais, depto, spinner_ciudades, perfil.ciudad)
                                }
                            }
                        }

                        // Teléfonos - determinar cantidad basada en teléfonos existentes
                        val telefonosExistentes = perfil.telefonos.filter { it.isNotBlank() }
                        txtTel1.setText(telefonosExistentes.getOrNull(0) ?: "")
                        txtTel2.setText(telefonosExistentes.getOrNull(1) ?: "")

                        // Establecer la selección del spinner basado en teléfonos existentes
                        val cantidadTel = when {
                            telefonosExistentes.size >= 2 -> 1 // Índice 1 = "2"
                            telefonosExistentes.size == 1 -> 0 // Índice 0 = "1"
                            else -> 0 // Por defecto "1"
                        }
                        spinner_cantidad_tel.setSelection(cantidadTel)

                        // Aplicar el estado inicial de los campos
                        manejarCamposTelefono(cantidadTel + 1) // +1 porque los índices son 0="1", 1="2"


                    } else {
                        Toast.makeText(this@Act_perfil_administrador, "No se pudo cargar el perfil.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@Act_perfil_administrador, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
                    finish()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_administrador, "Error general al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
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
                finish()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        // Botón GUARDAR
        buttonGuardar.setOnClickListener {

            if (id_administrador_actual == -1) {
                Toast.makeText(this, "Error: No se encontró el ID del administrador. Recarga la pantalla.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val postipoidentificacion = spinner_tipos_id.selectedItemPosition
            val id_tipo_identificacion = postipoidentificacion + 1

            if (id_tipo_identificacion <= 0) {
                Toast.makeText(this, "Error: Tipo de identificacion no válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val numero_identificacion: String = txtIdentificacion.text.toString().trim()
            val nombrecompleto: String = txtNombre.text.toString().trim()
            val nuevoCorreo: String = txtCorreo.text.toString().trim()
            val direccion: String = txtDireccion.text.toString().trim()
            val tel1: String = txtTel1.text.toString().trim()
            val tel2: String = txtTel2.text.toString().trim()

            val pais = spinner_paises.selectedItem.toString()
            val departamento = spinner_departamentos.selectedItem.toString()
            val ciudad = spinner_ciudades.selectedItem.toString()

            val correoOriginal = userEmail
            val cambioCorreo: Boolean = nuevoCorreo != correoOriginal

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)

                    if (codigoPostal != null) {
                        val actualizacionExitosa = actualizarPerfil(
                            id_administrador_actual,
                            id_tipo_identificacion,
                            numero_identificacion,
                            nombrecompleto,
                            direccion,
                            nuevoCorreo,
                            codigoPostal,
                            tel1,
                            tel2
                        )

                        if (actualizacionExitosa) {
                            if (cambioCorreo) {
                                Toast.makeText(this@Act_perfil_administrador, "¡Correo actualizado! Por favor, vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()

                                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                prefs.edit().clear().apply()

                                val intent = Intent(this@Act_perfil_administrador, transportadora.Compartido.Main::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@Act_perfil_administrador, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Act_perfil_administrador, Perfil_administrador::class.java)
                                intent.putExtra("USER_EMAIL", userEmail)
                                startActivity(intent)
                                finish()
                            }

                        } else {
                            Toast.makeText(this@Act_perfil_administrador, "Error al guardar datos. Revisa la información.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(this@Act_perfil_administrador, "Error: No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Act_perfil_administrador, "Error de red al obtener Código Postal: ${e.message}", Toast.LENGTH_LONG).show()
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

                spinner.adapter = ArrayAdapter(this@Act_perfil_administrador, android.R.layout.simple_spinner_item, listaNombres).apply {
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
                Toast.makeText(this@Act_perfil_administrador, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // FUNCIÓN PARA MANEJAR LOS CAMPOS DE TELÉFONO SEGÚN LA CANTIDAD SELECCIONADA
    private fun manejarCamposTelefono(cantidad: Int) {
        when (cantidad) {
            1 -> {
                // Habilitar solo teléfono 1
                txtTel1.isEnabled = true
                txtTel1.hint = "Ingrese su teléfono"

                // Deshabilitar y limpiar teléfono 2
                txtTel2.isEnabled = false
                txtTel2.hint = "No disponible"
                txtTel2.text?.clear()
            }
            2 -> {
                // Habilitar ambos teléfonos
                txtTel1.isEnabled = true
                txtTel1.hint = "Teléfono principal"

                txtTel2.isEnabled = true
                txtTel2.hint = "Teléfono secundario (opcional)"
            }
            else -> {
                // Por defecto, manejar como 1 teléfono
                txtTel1.isEnabled = true
                txtTel2.isEnabled = false
                txtTel2.text?.clear()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner, ciudadSeleccionada: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades

                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Act_perfil_administrador, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                ciudadSeleccionada?.let {
                    val ciudadIndex = listaNombres.indexOf(it)
                    if (ciudadIndex != -1) {
                        spinner.setSelection(ciudadIndex)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_administrador, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun obtenerIdAdministradorPorCorreo(email: String): Int = withContext(Dispatchers.IO) {
        var idAdministrador = -1
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/administrador/perfil/obtener_id_administrador_por_correo.php")
            val params = "correo=${java.net.URLEncoder.encode(email, "UTF-8")}"

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                idAdministrador = json.getInt("id_administrador")
                Log.d("Act_perfil_administrador", "ID Administrador obtenido: $idAdministrador")
            } else {
                Log.e("Act_perfil_administrador", "Error PHP al obtener ID: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Act_perfil_administrador", "Error de red/JSON al obtener ID: ${e.message}", e)
        }
        return@withContext idAdministrador
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
                Log.d("Act_perfil_administrador", "Código Postal obtenido: $codigoPostal")
            } else {
                Log.e("Act_perfil_administrador", "Error PHP al obtener Código Postal: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Act_perfil_administrador", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    private suspend fun actualizarPerfil(
        idAdministrador: Int,
        idTipoIdentificacion: Int,
        identificacion: String,
        nombre: String,
        direccion: String,
        correo: String,
        codigoPostal: String,
        tel1: String,
        tel2: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/administrador/perfil/actualizar_perfil.php")

            // Determinar qué teléfonos enviar basado en la selección del spinner
            val cantidadSeleccionada = spinner_cantidad_tel.selectedItem.toString().toIntOrNull() ?: 1

            val tel1Final = if (cantidadSeleccionada >= 1) tel1 else ""
            val tel2Final = if (cantidadSeleccionada >= 2) tel2 else ""

            val params = "id_administrador=$idAdministrador" +
                    "&id_tipo_identificacion=$idTipoIdentificacion" +
                    "&identificacion=${java.net.URLEncoder.encode(identificacion, "UTF-8")}" +
                    "&nombre=${java.net.URLEncoder.encode(nombre, "UTF-8")}" +
                    "&direccion=${java.net.URLEncoder.encode(direccion, "UTF-8")}" +
                    "&correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" +
                    "&codigo_postal=${java.net.URLEncoder.encode(codigoPostal, "UTF-8")}" +
                    "&tel1=${java.net.URLEncoder.encode(tel1Final, "UTF-8")}" +
                    "&tel2=${java.net.URLEncoder.encode(tel2Final, "UTF-8")}"

            Log.d("Act_perfil_administrador", "Sending params: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val responseCode = connection.responseCode
            Log.d("Act_perfil_administrador", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                Log.d("Act_perfil_administrador", "Received response: $response")

                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error message provided"
                Log.e("Act_perfil_administrador", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Act_perfil_administrador", "Error al actualizar perfil (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }
}
