package transportadora.Administrador.Usuarios

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
import transportadora.Almacenados.Administrador.TipoUsuario
import transportadora.Almacenados.Administrador.Tipo_usuario_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_usuarios : AppCompatActivity() {
    private lateinit var spinnerTipoUsuario: Spinner
    private lateinit var txtCorreo: EditText
    private lateinit var txtContrasenia: EditText
    private var listaTiposUsuario: List<TipoUsuario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_usuarios)

        spinnerTipoUsuario = findViewById(R.id.txt_id)
        txtCorreo = findViewById(R.id.txt_correo)
        txtContrasenia = findViewById(R.id.txt_contra)

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
                listaTiposUsuario = withContext(Dispatchers.IO) {
                    Tipo_usuario_almacenados.obtenerTiposUsuario()
                }
                if (listaTiposUsuario.isNotEmpty()) {
                    val nombresTipos = listaTiposUsuario.map { it.descripcion }
                    spinnerTipoUsuario.adapter = ArrayAdapter(
                        this@Crear_usuarios,
                        android.R.layout.simple_spinner_item,
                        nombresTipos
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_usuarios, "No hay tipos de usuario disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_usuarios, "Error al cargar tipos de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearUsuario() {
        val correo = txtCorreo.text.toString().trim()
        val contrasenia = txtContrasenia.text.toString().trim()
        val posicionTipo = spinnerTipoUsuario.selectedItemPosition

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasenia.isEmpty()) {
            Toast.makeText(this, "La contraseña es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaTiposUsuario.isEmpty() || posicionTipo < 0) {
            Toast.makeText(this, "Debe seleccionar un tipo de usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val idTipoUsuario = listaTiposUsuario[posicionTipo].id_tipo_usuario

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/create.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_usuario", idTipoUsuario)
            put("correo", correo)
            put("contrasenia", contrasenia)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_usuarios::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}