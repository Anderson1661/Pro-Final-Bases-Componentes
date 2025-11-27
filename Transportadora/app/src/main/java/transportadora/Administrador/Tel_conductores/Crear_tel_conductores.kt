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
import transportadora.Almacenados.Administrador.ConductorSimple
import transportadora.Almacenados.Administrador.Conductor_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_conductores : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var spinnerConductor: Spinner
    private lateinit var txtTelefono: EditText
    private var listaConductores: List<ConductorSimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_conductores)

        spinnerConductor = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

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
                listaConductores = withContext(Dispatchers.IO) {
                    Conductor_almacenados.obtenerConductores()
                }
                if (listaConductores.isNotEmpty()) {
                    val nombresConductores = listaConductores.map { it.nombre }
                    spinnerConductor.adapter = ArrayAdapter(
                        this@Crear_tel_conductores,
                        android.R.layout.simple_spinner_item,
                        nombresConductores
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_tel_conductores, "No hay conductores disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_conductores, "Error al cargar conductores: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearTelefonoConductor() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionConductor = spinnerConductor.selectedItemPosition

        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaConductores.isEmpty() || posicionConductor < 0) {
            Toast.makeText(this, "Debe seleccionar un conductor", Toast.LENGTH_SHORT).show()
            return
        }

        val idConductor = listaConductores[posicionConductor].id_conductor

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_conductor/create.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
            put("telefono", telefono)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_tel_conductores::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear teléfono: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}