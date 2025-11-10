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
import transportadora.Login.R

class Principal_cliente : AppCompatActivity() {

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

        // Departamentos
        val departamentos = listOf("Cundinamarca", "Meta")
        val adapterDepartamentos = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner_departamento1.adapter = adapterDepartamentos
        spinner_departamento2.adapter = adapterDepartamentos

        // Ciudades
        val ciudades = listOf("Bogota", "Villavicencio")
        val adapterCiudades = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudades).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner_ciudades1.adapter = adapterCiudades
        spinner_ciudades2.adapter = adapterCiudades

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

        // Paises
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    val listapaises = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, listapaises).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar paises: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

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

        findViewById<TextView>(R.id.btn_continuar).setOnClickListener {
            startActivity(Intent(this, Transferencia::class.java))
        }

        // ==============================
        // LISTENERS DE SPINNERS

        // Listener: selección de dirección
        spinner_direcciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val seleccion = parent.getItemAtPosition(position).toString()
                val habilitar = seleccion == "Otra direccion"
                txtDireccionOrigen.isEnabled = habilitar
                spinner_departamento1.isEnabled = habilitar
                spinner_ciudades1.isEnabled = habilitar
                txtDireccionOrigen.hint = if (habilitar) "Ingresa otra dirección (Ej: Calle 45 #10-23)" else "Usando tu dirección de residencia"
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
    }
}
