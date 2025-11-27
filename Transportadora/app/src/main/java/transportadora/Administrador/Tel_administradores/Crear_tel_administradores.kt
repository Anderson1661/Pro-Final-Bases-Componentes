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
import transportadora.Almacenados.Administrador.AdministradorSimple
import transportadora.Almacenados.Administrador.Administrador_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_administradores : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var spinnerAdministrador: Spinner
    private lateinit var txtTelefono: EditText
    private var listaAdministradores: List<AdministradorSimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_administradores)

        spinnerAdministrador = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

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
                listaAdministradores = withContext(Dispatchers.IO) {
                    Administrador_almacenados.obtenerAdministradores()
                }
                if (listaAdministradores.isNotEmpty()) {
                    val nombresAdministradores = listaAdministradores.map { it.nombre }
                    spinnerAdministrador.adapter = ArrayAdapter(
                        this@Crear_tel_administradores,
                        android.R.layout.simple_spinner_item,
                        nombresAdministradores
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_tel_administradores, "No hay administradores disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_administradores, "Error al cargar administradores: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearTelefonoAdministrador() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionAdmin = spinnerAdministrador.selectedItemPosition

        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaAdministradores.isEmpty() || posicionAdmin < 0) {
            Toast.makeText(this, "Debe seleccionar un administrador", Toast.LENGTH_SHORT).show()
            return
        }

        val idAdministrador = listaAdministradores[posicionAdmin].id_administrador

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/create.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
            put("telefono", telefono)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_tel_administradores::class.java)
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