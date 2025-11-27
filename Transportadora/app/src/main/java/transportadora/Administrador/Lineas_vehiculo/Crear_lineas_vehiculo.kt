package transportadora.Administrador.Lineas_vehiculo

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
import transportadora.Modelos.Administrador.Marca
import transportadora.Almacenados.Administrador.Marca_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_lineas_vehiculo : AppCompatActivity() {
    private lateinit var spinnerMarca: Spinner
    private lateinit var txtDescripcion: EditText
    private var listaMarcas: List<Marca> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_lineas_vehiculo)

        spinnerMarca = findViewById(R.id.txt_marca)
        txtDescripcion = findViewById(R.id.txt_descripcion)

        // Cargar marcas al iniciar la actividad
        cargarMarcas()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonGuardar)
        botonguardar.setOnClickListener {
            crearLineaVehiculo()
        }
    }

    private fun cargarMarcas() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener marcas desde el almacenado
                listaMarcas = withContext(Dispatchers.IO) {
                    Marca_almacenados.obtenerMarcas()
                }

                if (listaMarcas.isNotEmpty()) {
                    // Crear lista de nombres para el spinner
                    val nombresMarcas = listaMarcas.map { it.nombre_marca }

                    // Configurar el adapter del spinner
                    val adapter = ArrayAdapter(
                        this@Crear_lineas_vehiculo,
                        android.R.layout.simple_spinner_item,
                        nombresMarcas
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMarca.adapter = adapter

                    Toast.makeText(this@Crear_lineas_vehiculo, "Marcas cargadas: ${listaMarcas.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_lineas_vehiculo, "No hay marcas disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_lineas_vehiculo, "Error al cargar marcas: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearLineaVehiculo() {
        val descripcion = txtDescripcion.text.toString().trim()
        val posicionMarca = spinnerMarca.selectedItemPosition

        // Validaciones
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaMarcas.isEmpty() || posicionMarca < 0) {
            Toast.makeText(this, "Debe seleccionar una marca válida", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID de la marca seleccionada
        val marcaSeleccionada = listaMarcas[posicionMarca]
        val idMarca = marcaSeleccionada.id_marca

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/create.php"
        val jsonObject = JSONObject().apply {
            put("id_linea", descripcion)
            put("id_marca", idMarca)
        }

        // Enviar la petición
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    // Regresar a la actividad anterior
                    val intent = Intent(this, Administrar_lineas_vehiculo::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear línea de vehículo: ${error.message}", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}