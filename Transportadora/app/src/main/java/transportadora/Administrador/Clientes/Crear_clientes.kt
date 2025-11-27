package transportadora.Administrador.Clientes

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
import transportadora.Login.R

class Crear_clientes : AppCompatActivity() {

    // Spinners datos personales
    private lateinit var spinnerTipoId: Spinner
    private lateinit var spinnerGenero: Spinner
    private lateinit var spinnerNacionalidad: Spinner
    private lateinit var spinnerCodigoPostal: Spinner

    // EditText datos personales
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtDireccion: EditText

    // Listas de datos
    private var listaTiposId: List<transportadora.Modelos.Administrador.Tipo_identificacion> = emptyList()
    private var listaGeneros: List<transportadora.Modelos.Administrador.Genero> = emptyList()
    private var listaPaises: List<transportadora.Modelos.Administrador.Pais> = emptyList()
    private var listaCodigosPostales: List<transportadora.Modelos.Administrador.Codigo_postal> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_clientes)

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
            crearCliente()
        }
    }

    private fun initVistas() {
        spinnerTipoId = findViewById(R.id.txt_tipo_id)
        spinnerGenero = findViewById(R.id.txt_genero)
        spinnerNacionalidad = findViewById(R.id.txt_nacionalidad)
        spinnerCodigoPostal = findViewById(R.id.txt_codigo_postal)

        txtIdentificacion = findViewById(R.id.txt_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtCorreo = findViewById(R.id.txt_email)
        txtDireccion = findViewById(R.id.txt_dir)
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

            } catch (e: Exception) {
                Toast.makeText(this@Crear_clientes, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, datos: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun crearCliente() {
        // Validar campos obligatorios
        if (!validarCampos()) return

        // Registrar cliente
        val exito = registrarClienteCompleto()

        if (exito) {
            Toast.makeText(
                this@Crear_clientes,
                "Registro exitoso. Tu contraseña inicial es tu número de documento.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this@Crear_clientes, Administrar_clientes::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this@Crear_clientes, "Error al registrar cliente", Toast.LENGTH_LONG).show()
        }
    }

    private fun validarCampos(): Boolean {
        // Datos personales
        if (txtIdentificacion.text.isEmpty() || txtNombre.text.isEmpty() ||
            txtCorreo.text.isEmpty() || txtDireccion.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos personales obligatorios", Toast.LENGTH_LONG).show()
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

        return true
    }

    private fun registrarClienteCompleto(): Boolean {
        try {
            // Preparar datos personales
            val idTipoIdentificacion = listaTiposId[spinnerTipoId.selectedItemPosition].id_tipo_identificacion
            val identificacion = txtIdentificacion.text.toString()
            val nombre = txtNombre.text.toString()
            val direccion = txtDireccion.text.toString()
            val correo = txtCorreo.text.toString()
            val idGenero = listaGeneros[spinnerGenero.selectedItemPosition].id_genero
            val idPaisNacionalidad = listaPaises[spinnerNacionalidad.selectedItemPosition].id_pais
            val codigoPostal = listaCodigosPostales[spinnerCodigoPostal.selectedItemPosition].id_codigo_postal

            // Crear JSON para enviar
            val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/cliente/create.php"
            val jsonObject = JSONObject().apply {
                put("identificacion", identificacion)
                put("id_tipo_identificacion", idTipoIdentificacion)
                put("nombre", nombre)
                put("direccion", direccion)
                put("correo", correo)
                put("id_genero", idGenero)
                put("id_pais_nacionalidad", idPaisNacionalidad)
                put("codigo_postal", codigoPostal)
            }

            var success = false

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    success = response.getString("success") == "1"
                    if (success) {
                        Toast.makeText(this@Crear_clientes, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@Crear_clientes, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Toast.makeText(this@Crear_clientes, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
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