package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.*
import transportadora.Modelos.*
import transportadora.Login.R
import kotlin.collections.getOrNull

class Principal_cliente : AppCompatActivity() {

    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var perfilCliente: PerfilCliente? = null
    private var departamentosOrigen: List<String> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_cliente)

        // ==============================
        // DEFINICIÓN DE VISTAS


        val spinner_direcciones = findViewById<Spinner>(R.id.spinner_origen_tipo)
        val spinner_paises = findViewById<Spinner>(R.id.spinner_pais_destino)
        val spinner_departamento1 = findViewById<Spinner>(R.id.spinner_depto_origen)
        val spinner_departamento2 = findViewById<Spinner>(R.id.spinner_depto_destino)
        val spinner_ciudades1 = findViewById<Spinner>(R.id.spinner_ciudad_origen)
        val spinner_ciudades2 = findViewById<Spinner>(R.id.spinner_ciudad_destino)
        val txtDireccionOrigen = findViewById<EditText>(R.id.txt_direccion_origen)
        val spinner_pasajeros = findViewById<Spinner>(R.id.spinner_cantidad_pasajeros)
        val spinner_tipos = findViewById<Spinner>(R.id.spinner_tipo_servicio)
        val spinner_categoria = findViewById<Spinner>(R.id.spinner_categoria_servicio)
        val spinner_pago = findViewById<Spinner>(R.id.spinner_metodo_pago)

        val btnContinuar = findViewById<TextView>(R.id.btn_continuar)

        val pasajero_1 = findViewById<EditText>(R.id.txt_pasajero1)
        val pasajero_2 = findViewById<EditText>(R.id.txt_pasajero2)
        val pasajero_3 = findViewById<EditText>(R.id.txt_pasajero3)
        val pasajero_4 = findViewById<EditText>(R.id.txt_pasajero4)
        val txt_pasajeros = listOf(pasajero_1, pasajero_2, pasajero_3, pasajero_4)

        // ==============================
        // CONFIGURACIÓN INICIAL DE SPINNERS


        // Dirección de origen
        val direcciones = listOf("Mi direccion", "Otra direccion")
        spinner_direcciones.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, direcciones).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Cantidad de pasajeros
        val cantidad_pasajeros = listOf("1", "2", "3", "4")
        spinner_pasajeros.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cantidad_pasajeros).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // ==============================
        // ESTADO INICIAL DE CAMPOS

        txtDireccionOrigen.isEnabled = false
        spinner_departamento1.isEnabled = false
        spinner_ciudades1.isEnabled = false
        spinner_pasajeros.isEnabled = false
        spinner_direcciones.setSelection(0)
        spinner_tipos.setSelection(0)

        txt_pasajeros.forEach {
            it.isEnabled = false
            it.hint = "No disponible"
        }

        // ==============================
        // CARGA DE DATOS DESDE BD

        // Perfil del Cliente
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    perfilCliente = withContext(Dispatchers.IO) { Perfil_cliente_almacenados.obtenerPerfil(userEmail) }
                    // Una vez cargado el perfil, la selección inicial del spinner de dirección lo usará.
                    spinner_direcciones.setSelection(0) // Dispara el listener con los datos ya cargados
                } catch (e: Exception) {
                    Toast.makeText(this@Principal_cliente, "Error al cargar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this@Principal_cliente, "No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            // Aquí podrías redirigir al login
        }


        // Paises
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

        // Departamentos Origen (Colombia ID 1) - Se carga para tener la lista disponible
        cargarDepartamentos(1, spinner_departamento1, true)


        // Tipos de servicio
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tipos = withContext(Dispatchers.IO) { Tipo_servicio_almacenados.obtener_tipo_servicio() }
                if (tipos.isNotEmpty()) {
                    val listaTipos = tipos.map { it.descripcion }
                    spinner_tipos.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaTipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron tipos de servicio", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar tipos de servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Categorías de servicio
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val categorias = withContext(Dispatchers.IO) { Categorias_almacenados.obtener_categoria_servicio() }
                if (categorias.isNotEmpty()) {
                    val listaCategorias = categorias.map { "${it.descripcion} — $${it.valor_km}/km" }
                    spinner_categoria.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaCategorias).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron categorías de servicio", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar categorías: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Metodos de pago
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val metodos = withContext(Dispatchers.IO) { Metodo_pago_almacenados.obtener_metodo_pago() }
                if (metodos.isNotEmpty()) {
                    val listametodos = metodos.map { it.descripcion }
                    spinner_pago.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listametodos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron metodos de pago", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar metodos de pago: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // ==============================
        // MENÚ LATERAL

        findViewById<TextView>(R.id.editarperfil).setOnClickListener {
            startActivity(Intent(this, Perfil_cliente::class.java))
        }

        findViewById<TextView>(R.id.cambiocontra).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Preg_seguridad::class.java))
        }

        findViewById<TextView>(R.id.cerrarsesion).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Main::class.java))
        }

        findViewById<TextView>(R.id.ayuda).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Ayuda::class.java))
        }

        // ==============================
        // MENÚ INFERIOR

        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)

        findViewById<TextView>(R.id.menu1).setOnClickListener {
            scrollView.post { scrollView.smoothScrollTo(0, 0) }
        }

        findViewById<TextView>(R.id.menu2).setOnClickListener {
            startActivity(Intent(this, Seguimiento_serv_cliente::class.java))
        }

        findViewById<TextView>(R.id.menu3).setOnClickListener {
            startActivity(Intent(this, Historial_serv_cliente::class.java))
        }

        // ==============================
        // LISTENERS DE SPINNERS

        // Listener: selección de dirección
        spinner_direcciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seleccion = parent.getItemAtPosition(position).toString()
                if (seleccion == "Mi direccion") {
                    // Usar dirección del perfil
                    txtDireccionOrigen.isEnabled = false
                    spinner_departamento1.isEnabled = false
                    spinner_ciudades1.isEnabled = false

                    perfilCliente?.let { perfil ->
                        txtDireccionOrigen.hint = "Usando tu dirección (${perfil.direccion})"
                        
                        // Seleccionar departamento del perfil
                        val deptoIndex = departamentosOrigen.indexOf(perfil.departamento)
                        if (deptoIndex != -1) {
                            spinner_departamento1.setSelection(deptoIndex)
                            // Cargar ciudades y luego seleccionar la del perfil
                            cargarCiudades(1, perfil.departamento, spinner_ciudades1, perfil.ciudad)
                        }
                    }
                } else {
                    // Usar "Otra direccion"
                    txtDireccionOrigen.isEnabled = true
                    spinner_departamento1.isEnabled = true
                    spinner_ciudades1.isEnabled = true
                    txtDireccionOrigen.hint = "Ingresa otra dirección (Ej: Calle 45 #10-23)"
                    txtDireccionOrigen.text.clear()
                    
                    // Cargar la lista completa de departamentos de Colombia si no está ya
                    spinner_departamento1.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, departamentosOrigen).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener: tipo de servicio
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

        // Listener: cantidad de pasajeros
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

        // Listener: País Destino
        spinner_paises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val paisSeleccionado = listaPaisesCompleta.getOrNull(position)
                paisSeleccionado?.let {
                    cargarDepartamentos(it.id_pais, spinner_departamento2)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener: Departamento Origen
        spinner_departamento1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (spinner_direcciones.selectedItem.toString() == "Otra direccion") {
                    val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                    cargarCiudades(1, deptoSeleccionado, spinner_ciudades1)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener: Departamento Destino
        spinner_departamento2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val deptoSeleccionado = parent.getItemAtPosition(position).toString()
                val posPais = spinner_paises.selectedItemPosition
                val paisSeleccionado = listaPaisesCompleta.getOrNull(posPais)
                paisSeleccionado?.let {
                    cargarCiudades(it.id_pais, deptoSeleccionado, spinner_ciudades2)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        //Listener boton de continuar
        btnContinuar.setOnClickListener {
            val metodoPagoSeleccionado = spinner_pago.selectedItem?.toString() ?: ""

            if (metodoPagoSeleccionado.equals("Efectivo", ignoreCase = true)) {
                Toast.makeText(
                    this,
                    "Debes entregarle el efectivo una vez que llegue el conductor",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val intent = Intent(this, Transferencia::class.java)
                startActivity(intent)
            }
        }
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

    private fun cargarCiudades(idPais: Int, depto: String, spinner: Spinner, ciudadSeleccionada: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val ciudades = withContext(Dispatchers.IO) { Ciudad_almacenados.obtenerCiudades(idPais, depto) }
                val listaNombres = ciudades.map { it.nombre }
                spinner.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listaNombres).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                // Si se pasó una ciudad para seleccionar, la buscamos y la seleccionamos
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
}

