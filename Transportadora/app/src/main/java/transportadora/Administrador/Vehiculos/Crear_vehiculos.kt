package transportadora.Administrador.Vehiculos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Almacenados.Administrador.Color
import transportadora.Almacenados.Administrador.Color_almacenados
import transportadora.Almacenados.Administrador.EstadoVehiculo
import transportadora.Almacenados.Administrador.Estado_vehiculo_almacenados
import transportadora.Almacenados.Administrador.LineaSimple
import transportadora.Almacenados.Administrador.Linea_por_marca_almacenados
import transportadora.Almacenados.Administrador.Marca
import transportadora.Almacenados.Administrador.Marca_almacenados
import transportadora.Almacenados.Administrador.TipoServicio
import transportadora.Almacenados.Administrador.Tipo_servicio_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_vehiculos : AppCompatActivity() {
    private lateinit var txtPlaca: EditText
    private lateinit var spinnerMarca: Spinner
    private lateinit var spinnerLinea: Spinner
    private lateinit var txtModelo: EditText
    private lateinit var spinnerColor: Spinner
    private lateinit var spinnerTipoServicio: Spinner
    private lateinit var spinnerEstadoVehiculo: Spinner
    
    private var listaMarcas: List<Marca> = emptyList()
    private var listaLineas: List<LineaSimple> = emptyList()
    private var listaColores: List<Color> = emptyList()
    private var listaTiposServicio: List<TipoServicio> = emptyList()
    private var listaEstadosVehiculo: List<EstadoVehiculo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_vehiculos)

        txtPlaca = findViewById(R.id.txt_placa)
        spinnerMarca = findViewById(R.id.txt_marca)
        spinnerLinea = findViewById(R.id.txt_linea)
        txtModelo = findViewById(R.id.txt_modelo)
        spinnerColor = findViewById(R.id.txt_color)
        spinnerTipoServicio = findViewById(R.id.txt_tipo)
        // Nota: Si falta spinnerEstadoVehiculo en el layout, necesitarás agregarlo

        cargarDatos()
        
        spinnerMarca.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                if (position >= 0 && position < listaMarcas.size) {
                    cargarLineasPorMarca(listaMarcas[position].id_marca)
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonCrear)
        botonguardar.setOnClickListener {
            crearVehiculo()
        }
    }

    private fun cargarDatos() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaMarcas = withContext(Dispatchers.IO) {
                    Marca_almacenados.obtenerMarcas()
                }
                if (listaMarcas.isNotEmpty()) {
                    val nombresMarcas = listaMarcas.map { it.nombre_marca }
                    spinnerMarca.adapter = ArrayAdapter(
                        this@Crear_vehiculos,
                        android.R.layout.simple_spinner_item,
                        nombresMarcas
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }

                listaColores = withContext(Dispatchers.IO) {
                    Color_almacenados.obtenerColores()
                }
                if (listaColores.isNotEmpty()) {
                    val descripcionesColores = listaColores.map { it.descripcion }
                    spinnerColor.adapter = ArrayAdapter(
                        this@Crear_vehiculos,
                        android.R.layout.simple_spinner_item,
                        descripcionesColores
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }

                listaTiposServicio = withContext(Dispatchers.IO) {
                    Tipo_servicio_almacenados.obtenerTiposServicio()
                }
                if (listaTiposServicio.isNotEmpty()) {
                    val descripcionesTipos = listaTiposServicio.map { it.descripcion }
                    spinnerTipoServicio.adapter = ArrayAdapter(
                        this@Crear_vehiculos,
                        android.R.layout.simple_spinner_item,
                        descripcionesTipos
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_vehiculos, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarLineasPorMarca(idMarca: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaLineas = withContext(Dispatchers.IO) {
                    Linea_por_marca_almacenados.obtenerLineasPorMarca(idMarca)
                }
                if (listaLineas.isNotEmpty()) {
                    val nombresLineas = listaLineas.map { it.id_linea }
                    spinnerLinea.adapter = ArrayAdapter(
                        this@Crear_vehiculos,
                        android.R.layout.simple_spinner_item,
                        nombresLineas
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    spinnerLinea.adapter = ArrayAdapter(
                        this@Crear_vehiculos,
                        android.R.layout.simple_spinner_item,
                        listOf("No hay líneas disponibles")
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_vehiculos, "Error al cargar líneas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearVehiculo() {
        val placa = txtPlaca.text.toString().trim()
        val modelo = txtModelo.text.toString().trim()
        val posicionMarca = spinnerMarca.selectedItemPosition
        val posicionLinea = spinnerLinea.selectedItemPosition
        val posicionColor = spinnerColor.selectedItemPosition
        val posicionTipoServicio = spinnerTipoServicio.selectedItemPosition
        val posicionEstado = if (::spinnerEstadoVehiculo.isInitialized && spinnerEstadoVehiculo.selectedItemPosition >= 0) {
            spinnerEstadoVehiculo.selectedItemPosition
        } else {
            0 // Usar el primer estado por defecto
        }

        if (placa.isEmpty()) {
            Toast.makeText(this, "La placa es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (modelo.isEmpty()) {
            Toast.makeText(this, "El modelo es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        val modeloInt = try {
            modelo.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El modelo debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (modeloInt < 2010) {
            Toast.makeText(this, "El modelo debe ser 2010 o superior", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaMarcas.isEmpty() || posicionMarca < 0) {
            Toast.makeText(this, "Debe seleccionar una marca", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaLineas.isEmpty() || posicionLinea < 0) {
            Toast.makeText(this, "Debe seleccionar una línea", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaColores.isEmpty() || posicionColor < 0) {
            Toast.makeText(this, "Debe seleccionar un color", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaTiposServicio.isEmpty() || posicionTipoServicio < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de servicio", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaEstadosVehiculo.isEmpty() || posicionEstado < 0) {
            Toast.makeText(this, "Debe seleccionar un estado", Toast.LENGTH_SHORT).show()
            return
        }

        val idMarca = listaMarcas[posicionMarca].id_marca
        val lineaVehiculo = listaLineas[posicionLinea].id_linea
        val idColor = listaColores[posicionColor].id_color
        val idTipoServicio = listaTiposServicio[posicionTipoServicio].id_tipo_servicio
        val idEstadoVehiculo = listaEstadosVehiculo[posicionEstado].id_estado_vehiculo

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/create.php"
        val jsonObject = JSONObject().apply {
            put("placa", placa)
            put("linea_vehiculo", lineaVehiculo)
            put("modelo", modeloInt)
            put("id_color", idColor)
            put("id_marca", idMarca)
            put("id_tipo_servicio", idTipoServicio)
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

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
}