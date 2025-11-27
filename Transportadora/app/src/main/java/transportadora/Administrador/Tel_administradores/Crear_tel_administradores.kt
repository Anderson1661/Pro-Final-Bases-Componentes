package transportadora.Administrador.Tel_administradores

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
import transportadora.Modelos.Administrador.AdministradorSimple
import transportadora.Almacenados.Administrador.Administrador_simple_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_administradores : AppCompatActivity() {

    private lateinit var spinnerAdministrador: Spinner
    private lateinit var txtTelefono: EditText
    private var listaAdministradores: List<AdministradorSimple> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_administradores)

        // Inicializar vistas
        spinnerAdministrador = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

        // Cargar administradores en el spinner
        cargarAdministradores()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearTelefonoAdministrador()
        }
    }

    private fun cargarAdministradores() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener administradores desde el almacenado
                listaAdministradores = withContext(Dispatchers.IO) {
                    Administrador_simple_almacenados.obtenerAdministradores()
                }

                if (listaAdministradores.isNotEmpty()) {
                    // Crear lista de nombres para mostrar en el spinner
                    val nombresAdministradores = listaAdministradores.map {
                        "${it.nombre} - ${it.correo}"
                    }

                    // Configurar el adapter del spinner
                    val adapter = ArrayAdapter(
                        this@Crear_tel_administradores,
                        android.R.layout.simple_spinner_item,
                        nombresAdministradores
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAdministrador.adapter = adapter

                    // Opcional: mostrar mensaje de éxito
                    Toast.makeText(this@Crear_tel_administradores, "Administradores cargados: ${listaAdministradores.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_tel_administradores, "No hay administradores disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_administradores, "Error al cargar administradores: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearTelefonoAdministrador() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionAdmin = spinnerAdministrador.selectedItemPosition

        // Validaciones
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAdministradores.isEmpty() || posicionAdmin < 0) {
            Toast.makeText(this, "Debe seleccionar un administrador válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del administrador seleccionado
        val adminSeleccionado = listaAdministradores[posicionAdmin]
        val idAdministrador = adminSeleccionado.id_administrador

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/create.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
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
                        val intent = Intent(this, Administrar_tel_administradores::class.java)
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
        cargarAdministradores()
    }
}