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
import transportadora.Almacenados.Administrador.Marca
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
                listaMarcas = withContext(Dispatchers.IO) {
                    Marca_almacenados.obtenerMarcas()
                }
                if (listaMarcas.isNotEmpty()) {
                    val nombresMarcas = listaMarcas.map { it.nombre_marca }
                    spinnerMarca.adapter = ArrayAdapter(
                        this@Crear_lineas_vehiculo,
                        android.R.layout.simple_spinner_item,
                        nombresMarcas
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_lineas_vehiculo, "No hay marcas disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_lineas_vehiculo, "Error al cargar marcas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearLineaVehiculo() {
        val descripcion = txtDescripcion.text.toString().trim()
        val posicionMarca = spinnerMarca.selectedItemPosition

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaMarcas.isEmpty() || posicionMarca < 0) {
            Toast.makeText(this, "Debe seleccionar una marca", Toast.LENGTH_SHORT).show()
            return
        }

        val idMarca = listaMarcas[posicionMarca].id_marca

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/create.php"
        val jsonObject = JSONObject().apply {
            put("id_linea", descripcion)
            put("id_marca", idMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_lineas_vehiculo::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear línea de vehículo: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}