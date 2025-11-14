package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.Cliente.Categorias_almacenados
import transportadora.Almacenados.Cliente.Ciudad_almacenados
import transportadora.Almacenados.Cliente.Departamento_almacenados
import transportadora.Almacenados.Cliente.Metodo_pago_almacenados
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Perfil_cliente_almacenados
import transportadora.Almacenados.Cliente.Tipo_servicio_almacenados
import transportadora.Login.R
import transportadora.Modelos.Cliente.*
import kotlin.collections.getOrNull

class Principal_cliente : AppCompatActivity() {

    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var perfilCliente: PerfilCliente? = null
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCategoriasCompleta: List<Categoria_servicio> = emptyList()
    private var listaMetodosPago: List<Metodo_pago> = emptyList()
    private var listaTiposServicio: List<Tipo_servicio> = emptyList()
    private var totalPagar: Double = 0.0

    private var id_cliente_actual: Int = -1
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()
    private var listaCiudadesDestino: List<Ciudad> = emptyList()

    private lateinit var txtKmRecorrido: TextView
    private lateinit var txtTotalPagar: TextView
    private lateinit var spinner_ciudades1: Spinner
    private lateinit var spinner_ciudades2: Spinner
    private lateinit var spinner_categoria: Spinner

    // EditTexts de pasajeros definidos como propiedad de clase
    private lateinit var txt_pasajeros: List<EditText>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_cliente)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo del usuario.", Toast.LENGTH_LONG).show()
        } else {
            // 1. Obtener el id_cliente por correo (nueva lógica)
            CoroutineScope(Dispatchers.Main).launch {
                id_cliente_actual = obtenerIdClientePorCorreo(userEmail)
                if (id_cliente_actual == -1) {
                    Toast.makeText(this@Principal_cliente, "Error: No se pudo obtener el ID del cliente. Verifica tu sesión.", Toast.LENGTH_LONG).show()
                }
            }
        }
// FIN DE LA SECCIÓN MODIFICADA

        val txtFechaEnvio = findViewById<EditText>(R.id.txt_fecha_envio)
        txtFechaEnvio.isFocusable = false
        txtFechaEnvio.isFocusableInTouchMode = false
        try {
            txtFechaEnvio.showSoftInputOnFocus = false
        } catch (e: Exception) {
            // non-critical
        }

        txtFechaEnvio.setOnClickListener {
            val now = java.util.Calendar.getInstance()
            val datePicker = android.app.DatePickerDialog(this, { _, year, month, day ->
                val timePicker = android.app.TimePickerDialog(this, { _, hour, minute ->
                    val cal = java.util.Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                    txtFechaEnvio.setText(sdf.format(cal.time))
                }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true)
                timePicker.setTitle("Selecciona la hora")
                timePicker.show()
            }, now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH))
            datePicker.setTitle("Selecciona la fecha")
            datePicker.show()
        }

        val spinner_direcciones = findViewById<Spinner>(R.id.spinner_origen_tipo)
        val spinner_paises = findViewById<Spinner>(R.id.spinner_pais_destino)
        val spinner_departamento1 = findViewById<Spinner>(R.id.spinner_depto_origen)
        val spinner_departamento2 = findViewById<Spinner>(R.id.spinner_depto_destino)
        spinner_ciudades1 = findViewById(R.id.spinner_ciudad_origen)
        spinner_ciudades2 = findViewById(R.id.spinner_ciudad_destino)
        val txtDireccionOrigen = findViewById<EditText>(R.id.txt_direccion_origen)
        val spinner_pasajeros = findViewById<Spinner>(R.id.spinner_cantidad_pasajeros)
        val spinner_tipos = findViewById<Spinner>(R.id.spinner_tipo_servicio)
        spinner_categoria = findViewById(R.id.spinner_categoria_servicio)
        val spinner_pago = findViewById<Spinner>(R.id.spinner_metodo_pago)
        txtKmRecorrido = findViewById(R.id.txt_km_recorrido)
        txtTotalPagar = findViewById(R.id.txt_total_pagar)

        val btnContinuar = findViewById<TextView>(R.id.btn_continuar)

        val pasajero_1 = findViewById<EditText>(R.id.txt_pasajero1)
        val pasajero_2 = findViewById<EditText>(R.id.txt_pasajero2)
        val pasajero_3 = findViewById<EditText>(R.id.txt_pasajero3)
        val pasajero_4 = findViewById<EditText>(R.id.txt_pasajero4)
        txt_pasajeros = listOf(pasajero_1, pasajero_2, pasajero_3, pasajero_4) // Asignación a la propiedad de la clase

        val direcciones = listOf("Mi direccion", "Otra direccion")
        spinner_direcciones.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, direcciones).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val cantidad_pasajeros = listOf("1", "2", "3", "4")
        spinner_pasajeros.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cantidad_pasajeros).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        txtDireccionOrigen.isEnabled = false
        spinner_departamento1.isEnabled = false
        spinner_ciudades1.isEnabled = false
        spinner_pasajeros.isEnabled = false
        txt_pasajeros.forEach {
            it.isEnabled = false
            it.hint = "No disponible"
        }

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    perfilCliente = withContext(Dispatchers.IO) { Perfil_cliente_almacenados.obtenerPerfil(userEmail) }
                    spinner_direcciones.setSelection(0)
                } catch (e: Exception) {
                    Toast.makeText(this@Principal_cliente, "Error al cargar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this@Principal_cliente, "No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar paises: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        cargarDepartamentos(1, spinner_departamento1, true)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tipos = withContext(Dispatchers.IO) { Tipo_servicio_almacenados.obtener_tipo_servicio() }
                if (tipos.isNotEmpty()) {
                    listaTiposServicio = tipos
                    val listaTiposNombres = tipos.map { it.descripcion }
                    spinner_tipos.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaTiposNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron tipos de servicio", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar tipos de servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val categorias = withContext(Dispatchers.IO) { Categorias_almacenados.obtener_categoria_servicio() }
                if (categorias.isNotEmpty()) {
                    listaCategoriasCompleta = categorias
                    val listaCategoriasNombres = categorias.map { "${it.descripcion} — $${it.valor_km}/km" }
                    spinner_categoria.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaCategoriasNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron categorías de servicio", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar categorías: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val metodos = withContext(Dispatchers.IO) { Metodo_pago_almacenados.obtener_metodo_pago() }
                if (metodos.isNotEmpty()) {
                    listaMetodosPago = metodos
                    val listametodosNombres = metodos.map { it.descripcion }
                    spinner_pago.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listametodosNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron metodos de pago", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar metodos de pago: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<TextView>(R.id.editarperfil).setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.cambiocontra).setOnClickListener { startActivity(Intent(this, transportadora.Compartido.Preg_seguridad::class.java)) }
        findViewById<TextView>(R.id.cerrarsesion).setOnClickListener {
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            prefs.edit().clear().apply()
            startActivity(Intent(this, transportadora.Compartido.Main::class.java))
            finish()
        }
        findViewById<TextView>(R.id.ayuda).setOnClickListener { startActivity(Intent(this, transportadora.Compartido.Ayuda::class.java)) }

        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
        findViewById<TextView>(R.id.menu1).setOnClickListener { scrollView.post { scrollView.smoothScrollTo(0, 0) } }
        findViewById<TextView>(R.id.menu2).setOnClickListener {
            val intent = Intent(this, Seguimiento_serv_cliente::class.java)
            intent.putExtra("USER_ID", id_cliente_actual)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.menu3).setOnClickListener {
            val intent = Intent(this, Historial_serv_cliente::class.java)
            // Puedes enviar tanto el email como el ID, aunque el ID del cliente ya se utiliza
            intent.putExtra("USER_ID", id_cliente_actual)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        spinner_direcciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seleccion = parent.getItemAtPosition(position).toString()
                if (seleccion == "Mi direccion") {
                    txtDireccionOrigen.isEnabled = false
                    spinner_departamento1.isEnabled = false
                    spinner_ciudades1.isEnabled = false
                    perfilCliente?.let { perfil ->
                        txtDireccionOrigen.setText(perfil.direccion)
                        txtDireccionOrigen.hint = "Usando tu dirección del perfil"
                        cargarDepartamentos(1, spinner_departamento1, true)
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(300)
                            val deptoIndex = departamentosOrigen.indexOf(perfil.departamento)
                            if (deptoIndex != -1) {
                                spinner_departamento1.setSelection(deptoIndex)
                            }
                            cargarCiudades(1, perfil.departamento, spinner_ciudades1, true, perfil.ciudad)
                        }
                    } ?: Toast.makeText(this@Principal_cliente, "No se pudo obtener tu dirección del perfil.", Toast.LENGTH_SHORT).show()
                } else if (seleccion == "Otra direccion") {
                    txtDireccionOrigen.isEnabled = true
                    spinner_departamento1.isEnabled = true
                    spinner_ciudades1.isEnabled = true
                    txtDireccionOrigen.hint = "Ingresa otra dirección (Ej: Calle 45 #10-23)"
                    txtDireccionOrigen.text.clear()
                    cargarDepartamentos(1, spinner_departamento1, true)
                }
                calcularYActualizarTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_tipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seleccion = parent.getItemAtPosition(position).toString()
                val esPasajeros = seleccion == "Pasajeros"
                spinner_pasajeros.isEnabled = esPasajeros
                if (esPasajeros) {
                    pasajero_1.isEnabled = true
                    pasajero_1.hint = "Nombre completo"
                } else {
                    txt_pasajeros.forEach {
                        it.isEnabled = false
                        it.hint = "No disponible"
                        it.text.clear()
                    }
                    spinner_pasajeros.setSelection(0)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_pasajeros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cantidad = parent.getItemAtPosition(position).toString().toIntOrNull() ?: 1
                txt_pasajeros.forEachIndexed { index, campo ->
                    val activo = index < cantidad
                    campo.isEnabled = activo
                    campo.hint = if (activo) "Nombre completo" else "No disponible"
                    if (!activo) campo.text.clear()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_paises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                listaPaisesCompleta.getOrNull(position)?.let {
                    cargarDepartamentos(it.id_pais, spinner_departamento2)
                }
                calcularYActualizarTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_departamento1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (spinner_direcciones.selectedItem.toString() == "Otra direccion") {
                    val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                    cargarCiudades(1, deptoSeleccionado, spinner_ciudades1, true)
                }
                calcularYActualizarTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner_departamento2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                val posPais = spinner_paises.selectedItemPosition
                listaPaisesCompleta.getOrNull(posPais)?.let {
                    cargarCiudades(it.id_pais, deptoSeleccionado, spinner_ciudades2, false)
                }
                calcularYActualizarTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val recalculateListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { calcularYActualizarTotal() }
            override fun onNothingSelected(p0: AdapterView<*>?) { calcularYActualizarTotal() }
        }
        spinner_ciudades1.onItemSelectedListener = recalculateListener
        spinner_ciudades2.onItemSelectedListener = recalculateListener
        spinner_categoria.onItemSelectedListener = recalculateListener

        btnContinuar.setOnClickListener {
            val tipoDireccionOrigen = spinner_direcciones.selectedItem.toString()
            val direccionOrigen: String
            var isDireccionOrigenValid = true

            if (tipoDireccionOrigen == "Mi direccion") {
                direccionOrigen = perfilCliente?.direccion ?: ""
                if (perfilCliente == null || direccionOrigen.isEmpty()) {
                    isDireccionOrigenValid = false
                    Toast.makeText(this, "Error: No se pudo obtener tu dirección del perfil. Asegúrate de que tu perfil esté completo.", Toast.LENGTH_LONG).show()
                }
            } else { // "Otra direccion"
                direccionOrigen = findViewById<EditText>(R.id.txt_direccion_origen).text.toString().trim()
                if (direccionOrigen.isEmpty()) {
                    isDireccionOrigenValid = false
                }
            }

            val txtDireccionDestino = findViewById<EditText>(R.id.txt_direccion_entrega)
            val direccionDestino = txtDireccionDestino.text.toString().trim()

            val ciudadDestinoNombre = spinner_ciudades2.selectedItem?.toString() ?: ""
            val fechaEnvioTexto = txtFechaEnvio.text.toString().trim()

            android.util.Log.d("Principal_cliente", "Validation Check 1:")
            android.util.Log.d("Principal_cliente", "direccionOrigen: '$direccionOrigen' (Valid: $isDireccionOrigenValid)")
            android.util.Log.d("Principal_cliente", "direccionDestino: '$direccionDestino'")
            android.util.Log.d("Principal_cliente", "ciudadDestinoNombre: '$ciudadDestinoNombre'")
            android.util.Log.d("Principal_cliente", "fechaEnvioTexto: '$fechaEnvioTexto'")

            if (!isDireccionOrigenValid || direccionDestino.isEmpty() || ciudadDestinoNombre.isEmpty() || fechaEnvioTexto.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos requeridos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // CÓDIGO REEMPLAZADO DENTRO DE btnContinuar.setOnClickListener
            if (id_cliente_actual == -1) { // <-- Usa la variable de clase
                Toast.makeText(this, "Error: No se encontró el ID del cliente. Recarga la pantalla.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Se usa "yyyy-MM-dd HH:mm" para entrada y se formatea a "yyyy-MM-dd HH:mm:ss" para la base de datos MySQL
            val formatterSalida = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val fechaReserva = try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                val date = sdf.parse(fechaEnvioTexto)
                formatterSalida.format(date)
            } catch (e: Exception) {
                Toast.makeText(this, "Formato de fecha inválido. Usa: yyyy-MM-dd HH:mm", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val posMetodoPago = spinner_pago.selectedItemPosition
            val posCategoria = spinner_categoria.selectedItemPosition
            val posTipoServicio = spinner_tipos.selectedItemPosition
            val posCiudadOrigen = spinner_ciudades1.selectedItemPosition
            val posCiudadDestino = spinner_ciudades2.selectedItemPosition

            android.util.Log.d("Principal_cliente", "Validation Check 2 (Spinner Positions):")
            android.util.Log.d("Principal_cliente", "posMetodoPago: $posMetodoPago")
            android.util.Log.d("Principal_cliente", "posCategoria: $posCategoria")
            android.util.Log.d("Principal_cliente", "posTipoServicio: $posTipoServicio")
            android.util.Log.d("Principal_cliente", "posCiudadOrigen: $posCiudadOrigen")
            android.util.Log.d("Principal_cliente", "posCiudadDestino: $posCiudadDestino")

            if (posMetodoPago < 0 || posCategoria < 0 || posTipoServicio < 0 || posCiudadOrigen < 0 || posCiudadDestino < 0) {
                Toast.makeText(this, "Asegúrate de seleccionar todas las opciones.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idMetodoPago = posMetodoPago + 1
            val idCategoria = posCategoria + 1
            val idTipoServicio = posTipoServicio + 1
            val idCodigoPostalOrigen = listaCiudadesOrigen.getOrNull(posCiudadOrigen)?.id_codigo_postal ?: "-1"
            val idCodigoPostalDestino = listaCiudadesDestino.getOrNull(posCiudadDestino)?.id_codigo_postal ?: "-1"


            android.util.Log.d("Principal_cliente", "Validation Check 3 (IDs):")
            android.util.Log.d("Principal_cliente", "idMetodoPago: $idMetodoPago")
            android.util.Log.d("Principal_cliente", "idCategoria: $idCategoria")
            android.util.Log.d("Principal_cliente", "idTipoServicio: $idTipoServicio")
            android.util.Log.d("Principal_cliente", "idCodigoPostalOrigen: '$idCodigoPostalOrigen'")
            android.util.Log.d("Principal_cliente", "idCodigoPostalDestino: '$idCodigoPostalDestino'")

            if (idMetodoPago == -1 || idCategoria == -1 || idTipoServicio == -1 || idCodigoPostalOrigen == "-1" || idCodigoPostalDestino == "-1") {
                Toast.makeText(this, "Error al obtener IDs. Revisa las selecciones.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idEstadoServicio = 1
            val distanciaStr = txtKmRecorrido.text.toString().replace(" km", "").trim()
            val distancia = distanciaStr.toDoubleOrNull() ?: 0.0

            // Verificar si es servicio de pasajeros y recolectar nombres
            val tipoServicioSeleccionado = listaTiposServicio.getOrNull(posTipoServicio)?.descripcion ?: ""
            val esPasajeros = tipoServicioSeleccionado.equals("Pasajeros", ignoreCase = true)
            val pasajeros = mutableListOf<String>()

            if (esPasajeros) {
                txt_pasajeros.forEach { editText ->
                    val name = editText.text.toString().trim()
                    if (editText.isEnabled && name.isNotEmpty()) {
                        pasajeros.add(name)
                    }
                }
                if (pasajeros.isEmpty()) {
                    Toast.makeText(this, "Debe ingresar al menos un pasajero para el servicio de Pasajeros.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }


            // Chequeo final de listas de datos
            if (listaMetodosPago.isEmpty() || listaCategoriasCompleta.isEmpty() || listaTiposServicio.isEmpty() ||
                listaCiudadesOrigen.isEmpty() || listaCiudadesDestino.isEmpty()) {
                Toast.makeText(this, "Aún se están cargando los datos. Por favor, espera un momento y vuelve a intentarlo.", Toast.LENGTH_LONG).show()
                android.util.Log.e("Principal_cliente", "Error: Data lists not fully loaded.")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/ruta/crear_ruta.php")

                    // CORRECCIÓN: Se envía fecha_hora_reserva y total
                    val params = "direccion_origen=${java.net.URLEncoder.encode(direccionOrigen, "UTF-8")}" +
                            "&direccion_destino=${java.net.URLEncoder.encode(direccionDestino, "UTF-8")}" +
                            "&id_codigo_postal_origen=$idCodigoPostalOrigen" +
                            "&id_codigo_postal_destino=$idCodigoPostalDestino" +
                            "&distancia_km=$distancia" +
                            "&id_tipo_servicio=$idTipoServicio" +
                            "&id_cliente=$id_cliente_actual" +
                            "&id_categoria_servicio=$idCategoria" +
                            "&id_metodo_pago=$idMetodoPago" +
                            "&id_estado_servicio=$idEstadoServicio" +
                            "&fecha_hora_reserva=${java.net.URLEncoder.encode(fechaReserva, "UTF-8")}" +
                            "&total=$totalPagar" // Se incluye el total

                    android.util.Log.d("Principal_cliente", "Sending params: $params")

                    val connection = url.openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.doOutput = true
                    connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    android.util.Log.d("Principal_cliente", "Received response: $response")

                    withContext(Dispatchers.Main) {
                        val json = org.json.JSONObject(response)
                        if (json.getString("success") == "1") {
                            val idRuta = json.optInt("id_ruta", -1) // Se extrae el ID creado del JSON

                            var registroPasajerosExitoso = true
                            if (esPasajeros && idRuta != -1) {
                                // Ejecutar la lógica de registro de pasajeros
                                registroPasajerosExitoso = withContext(Dispatchers.IO) {
                                    registrarPasajeros(idRuta, pasajeros)
                                }
                            }

                            if (registroPasajerosExitoso) {
                                Toast.makeText(this@Principal_cliente, "Ruta creada correctamente", Toast.LENGTH_LONG).show()
                                val metodoPagoSeleccionado = listaMetodosPago.getOrNull(posMetodoPago)?.descripcion ?: ""
                                if (metodoPagoSeleccionado.equals("Efectivo", ignoreCase = true)) {
                                    Toast.makeText(this@Principal_cliente, "Debes entregarle el efectivo una vez que llegue el conductor", Toast.LENGTH_LONG).show()
                                    val intent1 = Intent(this@Principal_cliente, Historial_serv_cliente::class.java)
                                    intent1.putExtra("USER_ID", id_cliente_actual)
                                    intent1.putExtra("USER_EMAIL", userEmail)
                                    startActivity(intent1)
                                } else {
                                    val intent2 = Intent(this@Principal_cliente, Transferencia::class.java)
                                    intent2.putExtra("TOTAL_PAGAR", totalPagar)
                                    intent2.putExtra("USER_EMAIL", userEmail) // <-- ¡LÍNEA AÑADIDA!
                                    startActivity(intent2)
                                }
                            } else if (esPasajeros) {
                                // Si la ruta se creó pero falló el registro de pasajeros
                                Toast.makeText(this@Principal_cliente, "Ruta creada, pero **falló el registro de pasajeros**. Contacta a soporte.", Toast.LENGTH_LONG).show()
                                val intentHistorial = Intent(this@Principal_cliente, Historial_serv_cliente::class.java)
                                // Puedes enviar tanto el email como el ID, aunque el ID del cliente ya se utiliza
                                intentHistorial.putExtra("USER_ID", id_cliente_actual)
                                intentHistorial.putExtra("USER_EMAIL", userEmail)
                                startActivity(intentHistorial)                            }

                        } else {
                            Toast.makeText(this@Principal_cliente, "Error al crear ruta: ${json.getString("mensaje")}", Toast.LENGTH_LONG).show()
                        }
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.util.Log.e("Principal_cliente", "Error al registrar ruta: ${e.message}", e)
                        Toast.makeText(this@Principal_cliente, "Error de conexión o servidor: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun calcularYActualizarTotal() {
        val origenPosition = spinner_ciudades1.selectedItemPosition
        val destinoPosition = spinner_ciudades2.selectedItemPosition
        val categoriaPosition = spinner_categoria.selectedItemPosition

        if (origenPosition != AdapterView.INVALID_POSITION &&
            destinoPosition != AdapterView.INVALID_POSITION &&
            categoriaPosition != AdapterView.INVALID_POSITION &&
            listaCategoriasCompleta.isNotEmpty()) {

            val categoriaSeleccionada = listaCategoriasCompleta[categoriaPosition]
            val valorKm = categoriaSeleccionada.valor_km

            // La distancia es un valor random para la demostración
            val distancia = (10..100).random()
            totalPagar = distancia * valorKm

            txtKmRecorrido.text = "$distancia km"
            txtTotalPagar.text = String.format("$%,.2f", totalPagar)

        } else {
            totalPagar = 0.0
            txtKmRecorrido.text = "0 km"
            txtTotalPagar.text = "$0"
        }
    }

    // Función para registrar pasajeros en la tabla secundaria
    private suspend fun registrarPasajeros(idRuta: Int, pasajeros: List<String>): Boolean {
        var allSuccess = true
        for (nombrePasajero in pasajeros) {
            try {
                // Endpoint para manejar la tabla pasajero_ruta
                val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/ruta/crear_pasajero_ruta.php")
                val params = "id_ruta=$idRuta" +
                        "&nombre_pasajero=${java.net.URLEncoder.encode(nombrePasajero, "UTF-8")}"

                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                val json = org.json.JSONObject(response)
                if (json.getString("success") != "1") {
                    android.util.Log.e("Principal_cliente", "Fallo al registrar pasajero '$nombrePasajero': ${json.getString("mensaje")}")
                    allSuccess = false
                }
            } catch (e: Exception) {
                android.util.Log.e("Principal_cliente", "Error de red al registrar pasajero: ${e.message}", e)
                allSuccess = false
            }
        }
        return allSuccess
    }

    private fun cargarDepartamentos(idPais: Int, spinner: Spinner, esOrigen: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val departamentos = withContext(Dispatchers.IO) { Departamento_almacenados.obtenerDepartamentos(idPais) }
                val listaNombres = departamentos.map { it.nombre }
                if (esOrigen) {
                    departamentosOrigen = listaNombres
                }
                spinner.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar departamentos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner, esOrigen: Boolean, ciudadSeleccionada: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                if (esOrigen) {
                    listaCiudadesOrigen = ciudades
                } else {
                    listaCiudadesDestino = ciudades
                }

                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                ciudadSeleccionada?.let {
                    val ciudadIndex = listaNombres.indexOf(it)
                    if (ciudadIndex != -1) {
                        spinner.setSelection(ciudadIndex)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar ciudades: ${e.message}", Toast.LENGTH_LONG).show()
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
                android.util.Log.d("Principal_cliente", "ID Cliente obtenido: $idCliente")
            } else {
                android.util.Log.e("Principal_cliente", "Error PHP al obtener ID: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Principal_cliente", "Error de red/JSON al obtener ID: ${e.message}", e)
        }
        return@withContext idCliente
    }
}