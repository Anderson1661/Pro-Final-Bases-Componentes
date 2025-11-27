package transportadora.Administrador.Respuestas_seguridad

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
import transportadora.Almacenados.Administrador.PreguntaSimple
import transportadora.Almacenados.Administrador.Pregunta_almacenados
import transportadora.Almacenados.Administrador.UsuarioSimple
import transportadora.Almacenados.Administrador.Usuario_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_respuestas_seguridad : AppCompatActivity() {
    private lateinit var spinnerPregunta: Spinner
    private lateinit var spinnerUsuario: Spinner
    private lateinit var txtRespuesta: EditText
    private var listaPreguntas: List<PreguntaSimple> = emptyList()
    private var listaUsuarios: List<UsuarioSimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_respuestas_seguridad)

        spinnerPregunta = findViewById(R.id.txt_pregunta)
        spinnerUsuario = findViewById(R.id.txt_id)
        txtRespuesta = findViewById(R.id.txt_contra)

        cargarDatos()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            crearRespuestaSeguridad()
        }
    }

    private fun cargarDatos() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaPreguntas = withContext(Dispatchers.IO) {
                    Pregunta_almacenados.obtenerPreguntas()
                }
                if (listaPreguntas.isNotEmpty()) {
                    val descripcionesPreguntas = listaPreguntas.map { it.descripcion }
                    spinnerPregunta.adapter = ArrayAdapter(
                        this@Crear_respuestas_seguridad,
                        android.R.layout.simple_spinner_item,
                        descripcionesPreguntas
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }

                listaUsuarios = withContext(Dispatchers.IO) {
                    Usuario_almacenados.obtenerUsuarios()
                }
                if (listaUsuarios.isNotEmpty()) {
                    val correosUsuarios = listaUsuarios.map { it.correo }
                    spinnerUsuario.adapter = ArrayAdapter(
                        this@Crear_respuestas_seguridad,
                        android.R.layout.simple_spinner_item,
                        correosUsuarios
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_respuestas_seguridad, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearRespuestaSeguridad() {
        val respuesta = txtRespuesta.text.toString().trim()
        val posicionPregunta = spinnerPregunta.selectedItemPosition
        val posicionUsuario = spinnerUsuario.selectedItemPosition

        if (respuesta.isEmpty()) {
            Toast.makeText(this, "La respuesta es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaPreguntas.isEmpty() || posicionPregunta < 0) {
            Toast.makeText(this, "Debe seleccionar una pregunta", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaUsuarios.isEmpty() || posicionUsuario < 0) {
            Toast.makeText(this, "Debe seleccionar un usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val idPregunta = listaPreguntas[posicionPregunta].id_pregunta
        val idUsuario = listaUsuarios[posicionUsuario].id_usuario

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/create.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPregunta)
            put("id_usuario", idUsuario)
            put("respuesta_pregunta", respuesta)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_respuestas_seguridad::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear respuesta: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}