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
import transportadora.Modelos.Administrador.ClienteSimple
import transportadora.Almacenados.Administrador.Cliente_simple_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_tel_clientes : AppCompatActivity() {

    private lateinit var spinnerCliente: Spinner
    private lateinit var txtTelefono: EditText
    private var listaClientes: List<ClienteSimple> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_clientes)

        // Inicializar vistas
        spinnerCliente = findViewById(R.id.txt_id)
        txtTelefono = findViewById(R.id.txt_descripcion)

        // Cargar clientes en el spinner
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
                // Obtener clientes desde el almacenado
                listaClientes = withContext(Dispatchers.IO) {
                    Cliente_simple_almacenados.obtenerClientes()
                }

                if (listaClientes.isNotEmpty()) {
                    // Crear lista de nombres para mostrar en el spinner
                    val nombresClientes = listaClientes.map {
                        "${it.nombre} - ${it.correo}"
                    }

                    // Configurar el adapter del spinner
                    val adapter = ArrayAdapter(
                        this@Crear_tel_clientes,
                        android.R.layout.simple_spinner_item,
                        nombresClientes
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCliente.adapter = adapter

                    // Opcional: mostrar mensaje de éxito
                    Toast.makeText(this@Crear_tel_clientes, "Clientes cargados: ${listaClientes.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_tel_clientes, "No hay clientes disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_tel_clientes, "Error al cargar clientes: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearTelefonoCliente() {
        val telefono = txtTelefono.text.toString().trim()
        val posicionCliente = spinnerCliente.selectedItemPosition

        // Validaciones
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaClientes.isEmpty() || posicionCliente < 0) {
            Toast.makeText(this, "Debe seleccionar un cliente válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del cliente seleccionado
        val clienteSeleccionado = listaClientes[posicionCliente]
        val idCliente = clienteSeleccionado.id_cliente

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_cliente/create.php"
        val jsonObject = JSONObject().apply {
            put("id_cliente", idCliente)
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
                        val intent = Intent(this, Administrar_tel_clientes::class.java)
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
        cargarClientes()
    }
}