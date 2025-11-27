package transportadora.Administrador.Codigos_postales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
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
import transportadora.Almacenados.Administrador.Pais_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import transportadora.Modelos.Administrador.Pais

class Crear_codigos_postales : AppCompatActivity() {

    private lateinit var spinnerPais: Spinner
    private lateinit var txtCodigo: EditText
    private lateinit var txtDepartamento: EditText
    private lateinit var txtCiudad: EditText
    private var listaPaises: List<Pais> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_codigos_postales)

        // Inicializar vistas
        spinnerPais = findViewById(R.id.spinner_pais)
        txtCodigo = findViewById(R.id.txt_codigo)
        txtDepartamento = findViewById(R.id.txt_departamento)
        txtCiudad = findViewById(R.id.txt_ciudad)

        // Cargar países en el spinner
        cargarPaises()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonGuardar)
        botonguardar.setOnClickListener {
            crearCodigoPostal()
        }
    }

    private fun cargarPaises() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaPaises = withContext(Dispatchers.IO) {
                    Pais_almacenados.obtenerPaises()
                }

                if (listaPaises.isNotEmpty()) {
                    val nombresPaises = listaPaises.map { it.nombre }

                    val adapter = ArrayAdapter(
                        this@Crear_codigos_postales,
                        android.R.layout.simple_spinner_item,
                        nombresPaises
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPais.adapter = adapter

                    // Opcional: mostrar mensaje de éxito
                    Toast.makeText(this@Crear_codigos_postales, "Países cargados: ${listaPaises.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_codigos_postales, "No hay países disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_codigos_postales, "Error al cargar países: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearCodigoPostal() {
        val codigo = txtCodigo.text.toString().trim()
        val departamento = txtDepartamento.text.toString().trim()
        val ciudad = txtCiudad.text.toString().trim()
        val posicionPais = spinnerPais.selectedItemPosition

        // Validaciones
        if (codigo.isEmpty()) {
            Toast.makeText(this, "El código postal es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (departamento.isEmpty()) {
            Toast.makeText(this, "El departamento es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (ciudad.isEmpty()) {
            Toast.makeText(this, "La ciudad es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaPaises.isEmpty() || posicionPais < 0) {
            Toast.makeText(this, "Debe seleccionar un país válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del país seleccionado
        val paisSeleccionado = listaPaises[posicionPais]
        val idPais = paisSeleccionado.id_pais

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/create.php"
        val jsonObject = JSONObject().apply {
            put("id_codigo_postal", codigo)
            put("id_pais", idPais)
            put("departamento", departamento)
            put("ciudad", ciudad)
        }

        // Enviar la petición
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    // Regresar a la actividad anterior
                    val intent = Intent(this, Administrar_codigos_postales::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear código postal: ${error.message}", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}