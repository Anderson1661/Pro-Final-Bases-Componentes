package transportadora.Administrador.Vehiculos

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Almacenados.Administrador.Color_almacenados
import transportadora.Almacenados.Administrador.Estados_vehiculo_almacenados
import transportadora.Almacenados.Administrador.Linea_por_marca_almacenados
import transportadora.Almacenados.Administrador.Marca_almacenados
import transportadora.Almacenados.Administrador.Tipo_servicio_almacenados
import transportadora.Modelos.Administrador.Tipo_servicio
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import android.view.View

class Crear_vehiculos : AppCompatActivity() {

    // Vistas
    private lateinit var txtPlaca: EditText
    private lateinit var spinnerMarca: Spinner
    private lateinit var spinnerLinea: Spinner
    private lateinit var txtModelo: EditText
    private lateinit var spinnerColor: Spinner
    private lateinit var spinnerTipoServicio: Spinner
    private lateinit var spinnerEstadoVehiculo: Spinner

    // Listas de datos
    private var listaMarcas: List<transportadora.Modelos.Administrador.Marca> = emptyList()
    private var listaLineas: List<transportadora.Modelos.Administrador.Linea_vehiculo> = emptyList()
    private var listaColores: List<transportadora.Modelos.Administrador.Color> = emptyList()
    private var listaTiposServicio: List<Tipo_servicio> = emptyList()
    private var listaEstadosVehiculo: List<transportadora.Modelos.Administrador.Estado_vehiculo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_vehiculos)

        // Inicializar vistas
        initVistas()

        // Cargar datos iniciales
        cargarDatosIniciales()

        // Configurar listeners
        configurarListeners()

        // Botones
        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        btnVolver.setOnClickListener { finish() }

        val btnCrear = findViewById<Button>(R.id.buttonCrear)
        btnCrear.setOnClickListener { crearVehiculo() }
    }

    private fun initVistas() {
        txtPlaca = findViewById(R.id.txt_placa)
        spinnerMarca = findViewById(R.id.txt_marca)
        spinnerLinea = findViewById(R.id.txt_linea)
        txtModelo = findViewById(R.id.txt_modelo)
        spinnerColor = findViewById(R.id.txt_color)
        spinnerTipoServicio = findViewById(R.id.txt_tipo)
        spinnerEstadoVehiculo = findViewById(R.id.txt_estado)
    }

    private fun configurarListeners() {
        // Listener para spinner de marcas (cargar líneas)
        spinnerMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                // Cargar marcas
                val marcas = withContext(Dispatchers.IO) { Marca_almacenados.obtenerMarcas() }
                if (marcas.isNotEmpty()) {
                    listaMarcas = marcas
                    val nombresMarcas = marcas.map { it.nombre_marca }
                    setupSpinner(spinnerMarca, nombresMarcas)

                    // Cargar líneas de la primera marca por defecto
                    if (marcas.isNotEmpty()) {
                        cargarLineas(marcas[0].id_marca)
                    }
                }

                // Cargar colores
                val colores = withContext(Dispatchers.IO) { Color_almacenados.obtenerColores() }
                if (colores.isNotEmpty()) {
                    listaColores = colores
                    val nombresColores = colores.map { it.descripcion }
                    setupSpinner(spinnerColor, nombresColores)
                }

                // Cargar tipos de servicio
                val tiposServicio = withContext(Dispatchers.IO) { Tipo_servicio_almacenados.obtenerTiposServicio() }
                if (tiposServicio.isNotEmpty()) {
                    listaTiposServicio = tiposServicio
                    val nombresTipos = tiposServicio.map { it.descripcion }
                    setupSpinner(spinnerTipoServicio, nombresTipos)
                }

                // Cargar estados de vehículo
                val estadosVehiculo = withContext(Dispatchers.IO) { Estados_vehiculo_almacenados.obtenerEstadosVehiculo() }
                if (estadosVehiculo.isNotEmpty()) {
                    listaEstadosVehiculo = estadosVehiculo
                    val nombresEstados = estadosVehiculo.map { it.descripcion }
                    setupSpinner(spinnerEstadoVehiculo, nombresEstados)
                }

            } catch (e: Exception) {
                Toast.makeText(this@Crear_vehiculos, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarLineas(idMarca: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val lineas = withContext(Dispatchers.IO) { Linea_por_marca_almacenados.obtenerLineas(idMarca) }
                listaLineas = lineas
                val nombresLineas = lineas.map { it.linea }
                setupSpinner(spinnerLinea, nombresLineas)
            } catch (e: Exception) {
                Toast.makeText(this@Crear_vehiculos, "Error al cargar líneas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, datos: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun crearVehiculo() {
        // Validar campos obligatorios
        if (!validarCampos()) return

        // Obtener datos del formulario
        val placa = txtPlaca.text.toString().trim()
        val modelo = txtModelo.text.toString().trim().toInt()

        // Obtener IDs de los elementos seleccionados
        val posMarca = spinnerMarca.selectedItemPosition
        val idMarca = if (posMarca >= 0 && listaMarcas.isNotEmpty()) listaMarcas[posMarca].id_marca else -1
        val lineaVehiculo = if (spinnerLinea.selectedItemPosition >= 0 && listaLineas.isNotEmpty())
            listaLineas[spinnerLinea.selectedItemPosition].linea else ""

        val posColor = spinnerColor.selectedItemPosition
        val idColor = if (posColor >= 0 && listaColores.isNotEmpty()) listaColores[posColor].id_color else -1

        val posTipoServicio = spinnerTipoServicio.selectedItemPosition
        val idTipoServicio = if (posTipoServicio >= 0 && listaTiposServicio.isNotEmpty())
            listaTiposServicio[posTipoServicio].id_tipo_servicio else -1

        val posEstado = spinnerEstadoVehiculo.selectedItemPosition
        val idEstadoVehiculo = if (posEstado >= 0 && listaEstadosVehiculo.isNotEmpty())
            listaEstadosVehiculo[posEstado].id_estado_vehiculo else -1

        // Crear JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/create.php"
        val jsonObject = JSONObject().apply {
            put("placa", placa)
            put("linea_vehiculo", lineaVehiculo)
            put("modelo", modelo)
            put("id_color", idColor)
            put("id_marca", idMarca)
            put("id_tipo_servicio", idTipoServicio)
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

        // Enviar petición
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_vehiculos::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear vehículo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun validarCampos(): Boolean {
        // Validar placa
        if (txtPlaca.text.isEmpty()) {
            Toast.makeText(this, "La placa es requerida", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar modelo
        if (txtModelo.text.isEmpty()) {
            Toast.makeText(this, "El modelo es requerido", Toast.LENGTH_LONG).show()
            return false
        }

        val modelo = txtModelo.text.toString().toIntOrNull()
        if (modelo == null || modelo < 2010) {
            Toast.makeText(this, "El modelo debe ser 2010 o superior", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar selecciones de spinners
        if (spinnerMarca.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar una marca", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerLinea.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar una línea", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerColor.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un color", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerTipoServicio.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de servicio", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerEstadoVehiculo.selectedItemPosition < 0) {
            Toast.makeText(this, "Debe seleccionar un estado del vehículo", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}