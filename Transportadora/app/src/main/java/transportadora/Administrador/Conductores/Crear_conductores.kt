package transportadora.Administrador.Conductores

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Almacenados.Administrador.*
import transportadora.Configuracion.ApiConfig
import transportadora.Modelos.Administrador.Vehiculo
import transportadora.Login.R

class Crear_conductores : AppCompatActivity() {

    // Spinners datos personales
    private lateinit var spinnerTipoId: Spinner
    private lateinit var spinnerGenero: Spinner
    private lateinit var spinnerNacionalidad: Spinner
    private lateinit var spinnerCodigoPostal: Spinner
    private lateinit var spinnerVehiculo: Spinner
    private lateinit var spinnerEstadoConductor: Spinner

    // EditText datos personales
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtUrlFoto: EditText

    // Listas de datos
    private var listaTiposId: List<transportadora.Modelos.Administrador.Tipo_identificacion> = emptyList()
    private var listaGeneros: List<transportadora.Modelos.Administrador.Genero> = emptyList()
    private var listaPaises: List<transportadora.Modelos.Administrador.Pais> = emptyList()
    private var listaCodigosPostales: List<transportadora.Modelos.Administrador.Codigo_postal> = emptyList()
    private var listaVehiculos: List<Vehiculo> = emptyList()
    private var listaEstadosConductor: List<transportadora.Modelos.Administrador.Estado_conductor> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_conductores)

        // Inicializar vistas
        initVistas()

        // Cargar datos iniciales
        cargarDatosIniciales()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonCrear)
        botonguardar.setOnClickListener {
            crearConductor()
        }
    }

    private fun initVistas() {
        spinnerTipoId = findViewById(R.id.txt_tipo_id)
        spinnerGenero = findViewById(R.id.txt_genero)
        spinnerNacionalidad = findViewById(R.id.txt_nacionalidad)
        spinnerCodigoPostal = findViewById(R.id.txt_codigo_postal)
        spinnerVehiculo = findViewById(R.id.txt_vehiculo)
        spinnerEstadoConductor = findViewById(R.id.txt_estado_conductor)

        txtIdentificacion = findViewById(R.id.txt_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtCorreo = findViewById(R.id.txt_email)
        txtDireccion = findViewById(R.id.txt_dir)
        txtUrlFoto = findViewById(R.id.txt_foto)
    }

    private fun cargarDatosIniciales() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Cargar tipos de identificación
                val tiposId = withContext(Dispatchers.IO) { Tipo_identificacion_almacenados.obtenerTiposIdentificacion() }
                if (tiposId.isNotEmpty()) {
                    listaTiposId = tiposId
                    val nombresTipos = tiposId.map { it.descripcion }
                    setupSpinner(spinnerTipoId, nombresTipos)
                }

                // Cargar géneros
                val generos = withContext(Dispatchers.IO) { Generos_almacenados.obtenerGeneros() }
                if (generos.isNotEmpty()) {
                    listaGeneros = generos
                    val nombresGeneros = generos.map { it.descripcion }
                    setupSpinner(spinnerGenero, nombresGeneros)
                }

                // Cargar países para nacionalidad
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaises = paises
                    val nombresPaises = paises.map { it.nombre }
                    setupSpinner(spinnerNacionalidad, nombresPaises)
                }

                // Cargar códigos postales
                val codigosPostales = withContext(Dispatchers.IO) { Codigos_postales_almacenados.obtenerCodigosPostales() }
                if (codigosPostales.isNotEmpty()) {
                    listaCodigosPostales = codigosPostales
                    val descripcionesCodigos = codigosPostales.map {
                        "${it.id_codigo_postal} - ${it.ciudad}, ${it.departamento}"
                    }
                    setupSpinner(spinnerCodigoPostal, descripcionesCodigos)
                }

                // Cargar vehículos
                val vehiculos = withContext(Dispatchers.IO) { Vehiculos_almacenados.obtenerVehiculos() }
                if (vehiculos.isNotEmpty()) {
                    listaVehiculos = vehiculos
                    val descripcionesVehiculos = vehiculos.map {
                        "${it.placa} - ${it.marca} - ${it.linea_vehiculo}"
                    }
                    setupSpinner(spinnerVehiculo, descripcionesVehiculos)
                }

                // Cargar estados de conductor
                val estadosConductor = withContext(Dispatchers.IO) { Estados_conductor_almacenados.obtenerEstadosConductor() }
                if (estadosConductor.isNotEmpty()) {
                    listaEstadosConductor = estadosConductor
                    val nombresEstados = estadosConductor.map { it.descripcion }
                    setupSpinner(spinnerEstadoConductor, nombresEstados)
                }

            } catch (e: Exception) {
                Toast.makeText(this@Crear_conductores, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, datos: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun crearConductor() {
        // Validar campos obligatorios
        if (!validarCampos()) return

        // Registrar conductor
        val exito = registrarConductorCompleto()

        if (exito) {
            Toast.makeText(
                this@Crear_conductores,
                "Registro exitoso. Tu contraseña inicial es tu número de documento.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this@Crear_conductores, Administrar_conductores::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this@Crear_conductores, "Error al registrar conductor", Toast.LENGTH_LONG).show()
        }
    }

    private fun validarCampos(): Boolean {
        // Datos personales
        if (txtIdentificacion.text.isEmpty() || txtNombre.text.isEmpty() ||
            txtCorreo.text.isEmpty() || txtDireccion.text.isEmpty() || txtUrlFoto.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar selecciones de spinners
        if (spinnerTipoId.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de identificación", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerGenero.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un género", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerNacionalidad.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar una nacionalidad", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerCodigoPostal.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un código postal", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerVehiculo.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un vehículo", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerEstadoConductor.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un estado del conductor", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun registrarConductorCompleto(): Boolean {
        try {
            // Preparar datos del conductor
            val idEstadoConductor = listaEstadosConductor[spinnerEstadoConductor.selectedItemPosition].id_estado_conductor
            val placaVehiculo = listaVehiculos[spinnerVehiculo.selectedItemPosition].placa
            val idTipoIdentificacion = listaTiposId[spinnerTipoId.selectedItemPosition].id_tipo_identificacion
            val identificacion = txtIdentificacion.text.toString()
            val nombre = txtNombre.text.toString()
            val direccion = txtDireccion.text.toString()
            val correo = txtCorreo.text.toString()
            val idGenero = listaGeneros[spinnerGenero.selectedItemPosition].id_genero
            val idPaisNacionalidad = listaPaises[spinnerNacionalidad.selectedItemPosition].id_pais
            val codigoPostal = listaCodigosPostales[spinnerCodigoPostal.selectedItemPosition].id_codigo_postal
            val urlFoto = txtUrlFoto.text.toString()

            // Crear JSON para enviar
            val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/create.php"
            val jsonObject = JSONObject().apply {
                put("id_estado_conductor", idEstadoConductor)
                put("placa_vehiculo", placaVehiculo)
                put("identificacion", identificacion)
                put("id_tipo_identificacion", idTipoIdentificacion)
                put("nombre", nombre)
                put("direccion", direccion)
                put("correo", correo)
                put("id_genero", idGenero)
                put("codigo_postal", codigoPostal)
                put("id_pais_nacionalidad", idPaisNacionalidad)
                put("url_foto", urlFoto)
            }

            var success = false

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    success = response.getString("success") == "1"
                    if (success) {
                        Toast.makeText(this@Crear_conductores, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                        // Navegar a la lista de conductores después de registro exitoso
                        val intent = Intent(this@Crear_conductores, Administrar_conductores::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Crear_conductores, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Toast.makeText(this@Crear_conductores, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
                    success = false
                }
            )

            Volley.newRequestQueue(this).add(request)
            return true // La petición se envió correctamente

        } catch (e: Exception) {
            Toast.makeText(this, "Error al preparar datos: ${e.message}", Toast.LENGTH_LONG).show()
            return false
        }
    }
}