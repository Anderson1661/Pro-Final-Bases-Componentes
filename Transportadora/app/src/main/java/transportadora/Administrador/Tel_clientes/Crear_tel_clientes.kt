package transportadora.Administrador.Tel_clientes

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
import transportadora.Almacenados.Administrador.ClienteSimple
import transportadora.Almacenados.Administrador.Cliente_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_clientes : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var spinnerCliente: Spinner
    private lateinit var txtTelefono: EditText
    private var listaClientes: List<ClienteSimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_clientes)

        spinnerCliente = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

        cargarClientes()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearTelefonoCliente()
        }
    }

    private fun cargarClientes() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaClientes = withContext(Dispatchers.IO) {
                    Cliente_almacenados.obtenerClientes()
                }
                if (listaClientes.isNotEmpty()) {
                    val nombresClientes = listaClientes.map { it.nombre }
                    spinnerCliente.adapter = ArrayAdapter(
                        this@Crear_tel_clientes,
                        android.R.layout.simple_spinner_item,
                        nombresClientes
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_tel_clientes, "No hay clientes disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_clientes, "Error al cargar clientes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearTelefonoCliente() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionCliente = spinnerCliente.selectedItemPosition

        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaClientes.isEmpty() || posicionCliente < 0) {
            Toast.makeText(this, "Debe seleccionar un cliente", Toast.LENGTH_SHORT).show()
            return
        }

        val idCliente = listaClientes[posicionCliente].id_cliente

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_cliente/create.php"
        val jsonObject = JSONObject().apply {
            put("id_cliente", idCliente)
            put("telefono", telefono)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_tel_clientes::class.java)
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