package transportadora.Administrador.Servicios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.*

class Crear_servicios : AppCompatActivity() {

    // Spinners
    private lateinit var spinnerCliente: Spinner
    private lateinit var spinnerConductor: Spinner
    private lateinit var spinnerCodigoPostalOrigen: Spinner
    private lateinit var spinnerCodigoPostalDestino: Spinner
    private lateinit var spinnerTipoServicio: Spinner
    private lateinit var spinnerEstadoServicio: Spinner
    private lateinit var spinnerCategoriaServicio: Spinner
    private lateinit var spinnerMetodoPago: Spinner

    // EditText
    private lateinit var txtDireccionOrigen: EditText
    private lateinit var txtDireccionDestino: EditText
    private lateinit var txtDistanciaKm: EditText
    private lateinit var txtFechaHoraReserva: EditText
    private lateinit var txtFechaHoraOrigen: EditText
    private lateinit var txtFechaHoraDestino: EditText

    // Listas de datos
    private var listaClientes: List<transportadora.Modelos.Administrador.ClienteSimple> = emptyList()
    private var listaConductores: List<transportadora.Modelos.Administrador.ConductorSimple> = emptyList()
    private var listaCodigosPostales: List<transportadora.Modelos.Administrador.Codigo_postal> = emptyList()
    private var listaTiposServicio: List<transportadora.Modelos.Administrador.Tipo_servicio> = emptyList()
    private var listaEstadosServicio: List<transportadora.Modelos.Administrador.Estado_servicio> = emptyList()
    private var listaCategoriasServicio: List<transportadora.Modelos.Administrador.Categoria> = emptyList()
    private var listaMetodosPago: List<transportadora.Modelos.Administrador.Metodo_pago> = emptyList()

    @SuppressLint("MissingInflatedId", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_servicios)

        // Inicializar vistas
        initVistas()

        // Cargar datos iniciales
        cargarDatosIniciales()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearServicio()
        }

        // Listener para calcular total automáticamente
        txtDistanciaKm.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                calcularTotal()
            }
        }

        // Listener para cambios en categoría y distancia
        spinnerCategoriaServicio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                calcularTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initVistas() {
        // Spinners
        spinnerCliente = findViewById(R.id.spinner_cliente)
        spinnerConductor = findViewById(R.id.spinner_condcutor)
        spinnerCodigoPostalOrigen = findViewById(R.id.spinner_codigo_postal_origen)
        spinnerCodigoPostalDestino = findViewById(R.id.spinner_codigo_postal_destino)
        spinnerTipoServicio = findViewById(R.id.spinner_tipo_servicio)
        spinnerEstadoServicio = findViewById(R.id.spinner_estado_servicio)
        spinnerCategoriaServicio = findViewById(R.id.spinner_categoria_servicio)
        spinnerMetodoPago = findViewById(R.id.spinner_metodo_pago)

        // EditText
        txtDireccionOrigen = findViewById(R.id.txt_direccion_origen)
        txtDireccionDestino = findViewById(R.id.txt_direccion_entrega)
        txtDistanciaKm = findViewById(R.id.txt_km_recorrido)
        txtFechaHoraReserva = findViewById(R.id.txt_fecha_envio)
        txtFechaHoraOrigen = findViewById(R.id.txt_fecha_origen)
        txtFechaHoraDestino = findViewById(R.id.txt_fecha_destino)

        // Hacer campos de fecha no enfocables y configurar date pickers
        setupDatePickers()
    }

    private fun setupDatePickers() {
        // Configurar campo de Fecha de Reserva
        setupDatePicker(txtFechaHoraReserva, "Fecha de Reserva del Servicio")

        // Configurar campo de Fecha de Origen
        setupDatePicker(txtFechaHoraOrigen, "Fecha de Origen del Servicio")

        // Configurar campo de Fecha de Destino
        setupDatePicker(txtFechaHoraDestino, "Fecha de Entrega del Servicio")
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupDatePicker(editText: EditText, title: String) {
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        try {
            editText.showSoftInputOnFocus = false
        } catch (e: Exception) {
            // non-critical
        }

        editText.setOnClickListener {
            val now = java.util.Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(this, { _, year, month, day ->
                val timePicker = android.app.TimePickerDialog(this, { _, hour, minute ->
                    val cal = java.util.Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }
                    // Usar el mismo formato que en Principal_cliente: "yyyy-MM-dd HH:mm"
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                    editText.setText(sdf.format(cal.time))
                }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true)
                timePicker.setTitle("Selecciona la hora para $title")
                timePicker.show()
            }, now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH))
            datePicker.setTitle("Selecciona la fecha para $title")
            datePicker.show()
        }
    }

    private fun cargarDatosIniciales() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Cargar clientes
                val clientes = withContext(Dispatchers.IO) {
                    Cliente_simple_almacenados.obtenerClientes()
                }
                if (clientes.isNotEmpty()) {
                    listaClientes = clientes
                    val nombresClientes = clientes.map { "${it.nombre} - ${it.correo}" }
                    setupSpinner(spinnerCliente, nombresClientes)
                    Toast.makeText(this@Crear_servicios, "Clientes cargados: ${clientes.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron clientes", Toast.LENGTH_SHORT).show()
                }

                // Cargar conductores
                val conductores = withContext(Dispatchers.IO) {
                    Conductor_simple_almacenados.obtenerConductores()
                }
                if (conductores.isNotEmpty()) {
                    listaConductores = conductores
                    val nombresConductores = conductores.map { "${it.nombre} - ${it.correo}" }
                    // Agregar opción "Sin asignar"
                    val opcionesConductor = mutableListOf("Sin asignar")
                    opcionesConductor.addAll(nombresConductores)
                    setupSpinner(spinnerConductor, opcionesConductor)
                    Toast.makeText(this@Crear_servicios, "Conductores cargados: ${conductores.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron conductores", Toast.LENGTH_SHORT).show()
                }

                // Cargar códigos postales
                val codigosPostales = withContext(Dispatchers.IO) {
                    Codigos_postales_almacenados.obtenerCodigosPostales()
                }
                if (codigosPostales.isNotEmpty()) {
                    listaCodigosPostales = codigosPostales
                    val descripcionesCodigos = codigosPostales.map {
                        "${it.id_codigo_postal} - ${it.ciudad}, ${it.departamento}"
                    }
                    setupSpinner(spinnerCodigoPostalOrigen, descripcionesCodigos)
                    setupSpinner(spinnerCodigoPostalDestino, descripcionesCodigos)
                    Toast.makeText(this@Crear_servicios, "Códigos postales cargados: ${codigosPostales.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron códigos postales", Toast.LENGTH_SHORT).show()
                }

                // Cargar tipos de servicio
                val tiposServicio = withContext(Dispatchers.IO) {
                    Tipo_servicio_almacenados.obtenerTiposServicio()
                }
                if (tiposServicio.isNotEmpty()) {
                    listaTiposServicio = tiposServicio
                    val nombresTipos = tiposServicio.map { it.descripcion }
                    setupSpinner(spinnerTipoServicio, nombresTipos)
                    Toast.makeText(this@Crear_servicios, "Tipos de servicio cargados: ${tiposServicio.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron tipos de servicio", Toast.LENGTH_SHORT).show()
                }

                // Cargar estados de servicio - CORREGIDO el nombre de la función
                val estadosServicio = withContext(Dispatchers.IO) {
                    Estados_servicio_almacenados.obtenerEstadosServicio()
                }
                if (estadosServicio.isNotEmpty()) {
                    listaEstadosServicio = estadosServicio
                    val nombresEstados = estadosServicio.map { it.descripcion }
                    setupSpinner(spinnerEstadoServicio, nombresEstados)
                    Toast.makeText(this@Crear_servicios, "Estados de servicio cargados: ${estadosServicio.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron estados de servicio", Toast.LENGTH_SHORT).show()
                }

                // Cargar categorías de servicio
                val categoriasServicio = withContext(Dispatchers.IO) {
                    Categoria_servicio_almacenados.obtenerCategoriasServicio()
                }
                if (categoriasServicio.isNotEmpty()) {
                    listaCategoriasServicio = categoriasServicio
                    val nombresCategorias = categoriasServicio.map { "${it.descripcion} - $${it.valor_km}/km" }
                    setupSpinner(spinnerCategoriaServicio, nombresCategorias)
                    Toast.makeText(this@Crear_servicios, "Categorías cargadas: ${categoriasServicio.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron categorías de servicio", Toast.LENGTH_SHORT).show()
                }

                // Cargar métodos de pago
                val metodosPago = withContext(Dispatchers.IO) {
                    Metodo_pago_almacenados.obtenerMetodosPago()
                }
                if (metodosPago.isNotEmpty()) {
                    listaMetodosPago = metodosPago
                    val nombresMetodos = metodosPago.map { it.descripcion }
                    setupSpinner(spinnerMetodoPago, nombresMetodos)
                    Toast.makeText(this@Crear_servicios, "Métodos de pago cargados: ${metodosPago.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_servicios, "No se encontraron métodos de pago", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Crear_servicios, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, datos: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, datos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun calcularTotal() {
        try {
            if (spinnerCategoriaServicio.selectedItemPosition >= 0 && txtDistanciaKm.text.isNotEmpty()) {
                val categoria = listaCategoriasServicio[spinnerCategoriaServicio.selectedItemPosition]
                val distancia = txtDistanciaKm.text.toString().toDouble()
                val total = distancia * categoria.valor_km

                // Mostrar el total calculado
                Toast.makeText(this, "Total calculado: $${String.format("%.2f", total)}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Ignorar errores en cálculo
        }
    }

    private fun crearServicio() {
        // Validar campos obligatorios
        if (!validarCampos()) return

        // Registrar servicio
        registrarServicioCompleto()
    }

    private fun validarCampos(): Boolean {
        // Campos obligatorios
        if (txtDireccionOrigen.text.isEmpty() || txtDireccionDestino.text.isEmpty() ||
            txtDistanciaKm.text.isEmpty() || txtFechaHoraReserva.text.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar selecciones de spinners
        if (spinnerCliente.selectedItemPosition < 0 || listaClientes.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerCodigoPostalOrigen.selectedItemPosition < 0 || listaCodigosPostales.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un código postal de origen", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerCodigoPostalDestino.selectedItemPosition < 0 || listaCodigosPostales.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un código postal de destino", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerTipoServicio.selectedItemPosition < 0 || listaTiposServicio.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un tipo de servicio", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerEstadoServicio.selectedItemPosition < 0 || listaEstadosServicio.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un estado de servicio", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerCategoriaServicio.selectedItemPosition < 0 || listaCategoriasServicio.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar una categoría de servicio", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerMetodoPago.selectedItemPosition < 0 || listaMetodosPago.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un método de pago", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar distancia
        try {
            val distancia = txtDistanciaKm.text.toString().toDouble()
            if (distancia <= 0) {
                Toast.makeText(this, "La distancia debe ser mayor a 0", Toast.LENGTH_LONG).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "La distancia debe ser un número válido", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun registrarServicioCompleto() {
        try {
            // Preparar datos del servicio
            val direccionOrigen = txtDireccionOrigen.text.toString()
            val direccionDestino = txtDireccionDestino.text.toString()
            val idCodigoPostalOrigen = listaCodigosPostales[spinnerCodigoPostalOrigen.selectedItemPosition].id_codigo_postal
            val idCodigoPostalDestino = listaCodigosPostales[spinnerCodigoPostalDestino.selectedItemPosition].id_codigo_postal
            val distanciaKm = txtDistanciaKm.text.toString().toDouble()

            // Convertir fechas al formato que espera la base de datos
            val fechaHoraReserva = convertirFormatoFecha(txtFechaHoraReserva.text.toString())
            val fechaHoraOrigen = if (txtFechaHoraOrigen.text.isNotEmpty())
                convertirFormatoFecha(txtFechaHoraOrigen.text.toString()) else ""
            val fechaHoraDestino = if (txtFechaHoraDestino.text.isNotEmpty())
                convertirFormatoFecha(txtFechaHoraDestino.text.toString()) else ""

            // Conductor (opcional - puede ser null)
            val idConductor = if (spinnerConductor.selectedItemPosition > 0) {
                listaConductores[spinnerConductor.selectedItemPosition - 1].id_conductor
            } else {
                null
            }

            val idTipoServicio = listaTiposServicio[spinnerTipoServicio.selectedItemPosition].id_tipo_servicio
            val idCliente = listaClientes[spinnerCliente.selectedItemPosition].id_cliente
            val idEstadoServicio = listaEstadosServicio[spinnerEstadoServicio.selectedItemPosition].id_estado_servicio
            val idCategoriaServicio = listaCategoriasServicio[spinnerCategoriaServicio.selectedItemPosition].id_categoria_servicio
            val idMetodoPago = listaMetodosPago[spinnerMetodoPago.selectedItemPosition].id_metodo_pago

            // Crear JSON para enviar
            val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/create.php"
            val jsonObject = JSONObject().apply {
                put("direccion_origen", direccionOrigen)
                put("direccion_destino", direccionDestino)
                put("id_codigo_postal_origen", idCodigoPostalOrigen)
                put("id_codigo_postal_destino", idCodigoPostalDestino)
                put("distancia_km", distanciaKm)
                put("fecha_hora_reserva", fechaHoraReserva)
                put("fecha_hora_origen", fechaHoraOrigen)
                put("fecha_hora_destino", fechaHoraDestino)
                put("id_conductor", idConductor ?: JSONObject.NULL)
                put("id_tipo_servicio", idTipoServicio)
                put("id_cliente", idCliente)
                put("id_estado_servicio", idEstadoServicio)
                put("id_categoria_servicio", idCategoriaServicio)
                put("id_metodo_pago", idMetodoPago)
            }

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    val success = response.getString("success") == "1"
                    if (success) {
                        Toast.makeText(this@Crear_servicios, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                        // Navegar a la lista de servicios después de registro exitoso
                        val intent = Intent(this@Crear_servicios, Administrar_servicios::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Crear_servicios, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Toast.makeText(this@Crear_servicios, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )

            Volley.newRequestQueue(this).add(request)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al preparar datos: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertirFormatoFecha(fechaHora: String): String {
        return try {
            // Convertir de "yyyy-MM-dd HH:mm" a "yyyy-MM-dd HH:mm:ss"
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaHora)
            formatoSalida.format(fecha)
        } catch (e: Exception) {
            // Si ya está en el formato correcto o hay error, devolver el original
            fechaHora
        }
    }
}