package transportadora.Administrador.Tel_conductores

import android.annotation.SuppressLint
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
import transportadora.Modelos.Administrador.ConductorSimple
import transportadora.Almacenados.Administrador.Conductor_simple_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_conductores : AppCompatActivity() {

    private lateinit var spinnerConductor: Spinner
    private lateinit var txtTelefono: EditText
    private var listaConductores: List<ConductorSimple> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_conductores)

        // Inicializar vistas
        spinnerConductor = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

        // Cargar conductores en el spinner
        cargarConductores()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearTelefonoConductor()
        }
    }

    private fun cargarConductores() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener conductores desde el almacenado
                listaConductores = withContext(Dispatchers.IO) {
                    Conductor_simple_almacenados.obtenerConductores()
                }

                if (listaConductores.isNotEmpty()) {
                    // Crear lista de nombres para mostrar en el spinner
                    val nombresConductores = listaConductores.map {
                        "${it.nombre} - ${it.correo}"
                    }

                    // Configurar el adapter del spinner
                    val adapter = ArrayAdapter(
                        this@Crear_tel_conductores,
                        android.R.layout.simple_spinner_item,
                        nombresConductores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerConductor.adapter = adapter

                    // Opcional: mostrar mensaje de éxito
                    Toast.makeText(this@Crear_tel_conductores, "Conductores cargados: ${listaConductores.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_tel_conductores, "No hay conductores disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_conductores, "Error al cargar conductores: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearTelefonoConductor() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionConductor = spinnerConductor.selectedItemPosition

        // Validaciones
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaConductores.isEmpty() || posicionConductor < 0) {
            Toast.makeText(this, "Debe seleccionar un conductor válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del conductor seleccionado
        val conductorSeleccionado = listaConductores[posicionConductor]
        val idConductor = conductorSeleccionado.id_conductor

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_conductor/create.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
            put("telefono", telefono)
        }

        // Enviar la petición
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Regresar a la actividad anterior
                        val intent = Intent(this, Administrar_tel_conductores::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear teléfono: ${error.message}", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos si es necesario al volver a la actividad
        cargarConductores()
    }
}