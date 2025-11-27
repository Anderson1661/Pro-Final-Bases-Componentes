package transportadora.Administrador.Usuarios

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
import transportadora.Almacenados.Administrador.Tipo_usuario_almacenado
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import transportadora.Modelos.Administrador.Tipo_usuario

class Crear_usuarios : AppCompatActivity() {

    private lateinit var spinnerTipoUsuario: Spinner
    private lateinit var txtCorreo: EditText
    private lateinit var txtContrasenia: EditText
    private var listaTiposUsuario: List<Tipo_usuario> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_usuarios)

        // Inicializar vistas
        spinnerTipoUsuario = findViewById(R.id.txt_id)
        txtCorreo = findViewById(R.id.txt_correo)
        txtContrasenia = findViewById(R.id.txt_contra)

        // Cargar tipos de usuario en el spinner
        cargarTiposUsuario()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearUsuario()
        }
    }

    private fun cargarTiposUsuario() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener tipos de usuario desde el almacenado
                listaTiposUsuario = withContext(Dispatchers.IO) {
                    Tipo_usuario_almacenado.obtenerTiposUsuario()
                }

                if (listaTiposUsuario.isNotEmpty()) {
                    // Crear lista de descripciones para mostrar en el spinner
                    val descripcionesTipos = listaTiposUsuario.map { it.descripcion }

                    // Configurar el adapter del spinner
                    val adapter = ArrayAdapter(
                        this@Crear_usuarios,
                        android.R.layout.simple_spinner_item,
                        descripcionesTipos
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerTipoUsuario.adapter = adapter

                    // Opcional: mostrar mensaje de éxito
                    Toast.makeText(this@Crear_usuarios, "Tipos de usuario cargados: ${listaTiposUsuario.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@Crear_usuarios, "No hay tipos de usuario disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_usuarios, "Error al cargar tipos de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun crearUsuario() {
        val correo = txtCorreo.text.toString().trim()
        val contrasenia = txtContrasenia.text.toString().trim()
        val posicionTipo = spinnerTipoUsuario.selectedItemPosition

        // Validaciones
        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo electrónico es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasenia.isEmpty()) {
            Toast.makeText(this, "La contraseña es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaTiposUsuario.isEmpty() || posicionTipo < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de usuario válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el ID del tipo de usuario seleccionado
        val tipoSeleccionado = listaTiposUsuario[posicionTipo]
        val idTipoUsuario = tipoSeleccionado.id_tipo_usuario

        // DEBUG: Mostrar los datos que se enviarán
        println("DEBUG - ID Tipo Usuario: $idTipoUsuario")
        println("DEBUG - Correo: $correo")
        println("DEBUG - Tipo seleccionado: ${tipoSeleccionado.descripcion}")

        // Crear el JSON para enviar
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/create.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_usuario", idTipoUsuario)
            put("correo", correo)
            put("contrasenia", contrasenia)
        }

        // DEBUG: Mostrar el JSON que se enviará
        println("DEBUG - JSON a enviar: $jsonObject")

        // Enviar la petición
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Regresar a la actividad anterior
                        val intent = Intent(this, Administrar_usuarios::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear usuario: ${error.message}", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos si es necesario al volver a la actividad
        cargarTiposUsuario()
    }
}