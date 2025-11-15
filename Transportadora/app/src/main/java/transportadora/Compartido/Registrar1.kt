package transportadora.Compartido

import android.annotation.SuppressLint
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
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion
import android.content.Intent

class Registrar1 : AppCompatActivity() {

    // --- Listas para almacenar los datos completos ---
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()

    // --- Declaración de Vistas (Spinners) ---
    private lateinit var spinner_tipos_id: Spinner
    private lateinit var spinner_paises: Spinner
    private lateinit var spinner_departamentos: Spinner
    private lateinit var spinner_ciudades: Spinner
    private lateinit var spinner_nacionalidad: Spinner
    private lateinit var spinner_genero: Spinner
    private lateinit var spinner_cantidad_tel: Spinner // <-- NUEVO

    // --- Declaración de Vistas (EditText) ---
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtTel1: EditText
    private lateinit var txtTel2: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar1)

        // --- Inicializar Vistas (IDs tomados de activity_registrar1.xml) ---
        spinner_tipos_id = findViewById(R.id.txt_tipo_id_reg1)
        spinner_paises = findViewById(R.id.spinner_pais)
        spinner_departamentos = findViewById(R.id.spinner_departamento)
        spinner_ciudades = findViewById(R.id.spinner_ciudad)
        spinner_nacionalidad = findViewById(R.id.txt_nacionalidad)
        spinner_genero = findViewById(R.id.txt_genero)
        spinner_cantidad_tel = findViewById(R.id.spinner_cantidad_tel) // <-- NUEVO

        txtIdentificacion = findViewById(R.id.txt_id_reg1)
        txtNombre = findViewById(R.id.txt_nombre_reg1)
        txtTel1 = findViewById(R.id.txt_tel1)
        txtTel2 = findViewById(R.id.txt_tel2)
        txtCorreo = findViewById(R.id.txt_email_reg1)
        txtDireccion = findViewById(R.id.txt_dir_reg1)

        // --- LÓGICA DE TELÉFONOS (NUEVO) ---
        // Estado inicial: Habilitar Tel 1, Deshabilitar Tel 2 (asumiendo que "1" es la opción por defecto)
        txtTel1.isEnabled = true
        txtTel2.isEnabled = false

        spinner_cantidad_tel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "1" -> {
                        txtTel1.isEnabled = true
                        txtTel2.isEnabled = false
                        txtTel2.text.clear() // Limpiar el campo si se deshabilita
                    }
                    "2" -> {
                        txtTel1.isEnabled = true
                        txtTel2.isEnabled = true
                    }
                    else -> {
                        // Por si acaso, estado por defecto
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

        // --- Cargar datos iniciales (Países, Géneros, Tipos ID) ---
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Cargar Países
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }

                    spinner_paises.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinner_nacionalidad.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Registrar1, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }

                // Cargar Géneros
                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    listagenero = generos
                    val listageneros = generos.map { it.descripcion }
                    spinner_genero.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listageneros).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Registrar1, "No se encontraron generos", Toast.LENGTH_SHORT).show()
                }

                // Cargar Tipos de Identificación
                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Registrar1, "No se encontraron tipos de identificacion", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar1, "Error general al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // --- Listeners para Spinners en Cascada ---
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

        // --- Botones de Navegación ---
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val buttonContinuar = findViewById<Button>(R.id.buttonContinuar)
        buttonContinuar.setOnClickListener {
            registrarNuevoCliente()
        }
    }

    private fun registrarNuevoCliente() {
        // --- 1. Obtener valores de los campos de texto ---
        val numero_identificacion: String = txtIdentificacion.text.toString().trim()
        val nombrecompleto: String = txtNombre.text.toString().trim()
        val correo: String = txtCorreo.text.toString().trim()
        val direccion: String = txtDireccion.text.toString().trim()
        val tel1: String = txtTel1.text.toString().trim()
        val tel2: String = txtTel2.text.toString().trim() // Opcional

        // --- 2. Obtener IDs de los Spinners ---

        // ########## INICIO DE LA CORRECCIÓN ##########
        // Usamos tu lógica de "posicion + 1" para evitar el ID 0

        val posTipoId = spinner_tipos_id.selectedItemPosition
        // Si la posición es 0, el ID será 1. Si es -1 (inválido), será 0.
        val id_tipo_identificacion = posTipoId + 1

        val posGenero = spinner_genero.selectedItemPosition
        // Si la posición es 0, el ID será 1. Si es -1 (inválido), será 0.
        val id_genero = posGenero + 1
        // ########## FIN DE LA CORRECCIÓN ##########

        // Para País, mantenemos la lógica de objeto, ya que el log (id_pais_nacionalidad=1)
        // muestra que funciona y los IDs de país no son necesariamente secuenciales desde 1.
        val posNacionalidad = spinner_nacionalidad.selectedItemPosition
        val id_pais_nacionalidad = listaPaisesCompleta.getOrNull(posNacionalidad)?.id_pais ?: -1

        // --- 3. Obtener valores de ubicación para Código Postal ---
        val pais = spinner_paises.selectedItem?.toString() ?: ""
        val departamento = spinner_departamentos.selectedItem?.toString() ?: ""
        val ciudad = spinner_ciudades.selectedItem?.toString() ?: ""

        // --- 4. Validación básica ---
        // Ahora validamos que los IDs no sean 0 (inválidos) o -1 (error de país)
        if (numero_identificacion.isEmpty() || nombrecompleto.isEmpty() || correo.isEmpty() ||
            direccion.isEmpty() || tel1.isEmpty() || pais.isEmpty() || departamento.isEmpty() || ciudad.isEmpty() ||
            id_tipo_identificacion == 0 || id_genero == 0 || id_pais_nacionalidad == -1) {

            Log.e("Registrar1", "Error de validación. IDs: TipoID=$id_tipo_identificacion, Genero=$id_genero, Nacionalidad=$id_pais_nacionalidad")
            Toast.makeText(this, "Por favor, completa todos los campos (excepto Teléfono 2)", Toast.LENGTH_LONG).show()
            return
        }

        // --- 5. Coroutine para obtener Código Postal y Registrar ---
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Paso A: Obtener Código Postal
                val codigoPostal = obtenerCodigoPostal(pais, departamento, ciudad)

                if (codigoPostal != null) {
                    // Paso B: Registrar el cliente con el Código Postal
                    val registroExitoso = registrarCliente(
                        id_tipo_identificacion,
                        numero_identificacion,
                        nombrecompleto,
                        direccion,
                        correo,
                        id_genero,
                        id_pais_nacionalidad,
                        codigoPostal,
                        tel1,
                        tel2
                    )

                    if (registroExitoso) {
                        val intent = Intent(this@Registrar1, Registrar2::class.java).apply {
                            putExtra("EMAIL_CLIENTE", correo)
                        }

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Registrar1, "Error al guardar el registro. Revisa la información (ej: correo duplicado).", Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this@Registrar1, "Error: No se encontró el Código Postal para esa ubicación.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar1, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- Funciones de Carga (Sin cambios) ---

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                departamentosOrigen = listaNombres

                spinner.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar1, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                listaCiudadesOrigen = ciudades

                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registrar1, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
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
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                codigoPostal = json.getJSONObject("datos").getString("codigo_postal")
            } else {
                Log.e("Registrar1", "Error PHP al obtener Código Postal: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            Log.e("Registrar1", "Error de red/JSON al obtener Código Postal: ${e.message}", e)
        }
        return@withContext codigoPostal
    }

    // --- Función de Registro (Sin cambios) ---

    private suspend fun registrarCliente(
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
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/registrar_cliente.php")

            val params = "id_tipo_identificacion=$idTipoIdentificacion" +
                    "&identificacion=${java.net.URLEncoder.encode(identificacion, "UTF-8")}" +
                    "&nombre=${java.net.URLEncoder.encode(nombre, "UTF-8")}" +
                    "&direccion=${java.net.URLEncoder.encode(direccion, "UTF-8")}" +
                    "&correo=${java.net.URLEncoder.encode(correo, "UTF-8")}" +
                    "&id_genero=$idGenero" +
                    "&id_pais_nacionalidad=$idNacionalidad" +
                    "&codigo_postal=${java.net.URLEncoder.encode(codigoPostal, "UTF-8")}" +
                    "&tel1=${java.net.URLEncoder.encode(tel1, "UTF-8")}" +
                    "&tel2=${java.net.URLEncoder.encode(tel2, "UTF-8")}"

            Log.d("Registrar1", "Enviando params: $params") // <-- Esto ahora mostrará IDs = 1

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))
            val responseCode = connection.responseCode
            Log.d("Registrar1", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                Log.d("Registrar1", "Respuesta recibida: $response")

                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No hay mensaje de error"
                Log.e("Registrar1", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Registrar1", "Error al registrar (Network/Parse): ${e.message}", e)
            return@withContext false
        }
    }
}