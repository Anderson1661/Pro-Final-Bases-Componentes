package transportadora.Administrador.Respuestas_seguridad

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R // Asume tu ruta a R

class Editar_respuestas_seguridad : AppCompatActivity() {
    private lateinit var txtIdPregunta: EditText
    private lateinit var txtIdUsuario: EditText
    private lateinit var txtRespuestaPregunta: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    // Claves primarias compuestas originales
    private var idPreguntaOriginal: Int = 0
    private var idUsuarioOriginal: Int = 0
    private lateinit var respuestaOriginal: String // Para verificar si hubo cambios

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_respuestas_seguridad)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_respuesta_seguridad)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Obtener la clave compuesta del Intent
        idPreguntaOriginal = intent.getIntExtra("id_pregunta", 0)
        idUsuarioOriginal = intent.getIntExtra("id_usuario", 0)

        // 2. Inicializar vistas
        txtIdPregunta = findViewById(R.id.txt_id_pregunta)
        txtIdUsuario = findViewById(R.id.txt_id_usuario)
        txtRespuestaPregunta = findViewById(R.id.txt_respuesta_pregunta)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // 3. Configuración de campos (claves primarias NO EDITABLES)
        txtIdPregunta.isEnabled = false
        txtIdPregunta.isFocusable = false
        txtIdUsuario.isEnabled = false
        txtIdUsuario.isFocusable = false
        txtRespuestaPregunta.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // 4. Cargar los datos
        cargarRespuestaSeguridad()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        btnVolver.setOnClickListener {
            mostrarDialogoDescartar()
        }

        btnDescartar.setOnClickListener {
            mostrarDialogoDescartar()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarRespuestaSeguridad() {
        if (idPreguntaOriginal == 0 || idUsuarioOriginal == 0) {
            Toast.makeText(this, "Error: Clave de pregunta/usuario no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // URL del script de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/consultar_respuesta_seguridad.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPreguntaOriginal)
            put("id_usuario", idUsuarioOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarRespSeguridad", "Respuesta carga: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer y guardar los datos
                        val idPregunta = datos.getInt("id_pregunta")
                        val idUsuario = datos.getInt("id_usuario")
                        val respuesta = datos.getString("respuesta_pregunta")

                        // Guardar la respuesta original para comparación
                        respuestaOriginal = respuesta

                        // Mostrar los datos en los campos
                        txtIdPregunta.setText(idPregunta.toString())
                        txtIdUsuario.setText(idUsuario.toString())
                        txtRespuestaPregunta.setText(respuesta)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("EditarRespSeguridad", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("EditarRespSeguridad", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        val respuestaNueva = txtRespuestaPregunta.text.toString().trim()

        if (respuestaNueva.isEmpty()) {
            Toast.makeText(this, "La respuesta es requerida", Toast.LENGTH_SHORT).show()
            txtRespuestaPregunta.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (::respuestaOriginal.isInitialized && respuestaNueva == respuestaOriginal) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // URL del script de actualización (ruta base + /respuestas_seguridad/update.php)
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/update.php"

        // Crear JSON con la clave compuesta y el nuevo valor
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPreguntaOriginal) // Clave primaria 1
            put("id_usuario", idUsuarioOriginal)   // Clave primaria 2
            put("respuesta_pregunta", respuestaNueva) // Nuevo valor a actualizar
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarRespSeguridad", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditarRespSeguridad", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("EditarRespSeguridad", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        val respuestaActual = txtRespuestaPregunta.text.toString().trim()

        // Solo mostrar diálogo si hubo un cambio
        if (::respuestaOriginal.isInitialized && respuestaActual != respuestaOriginal) {
            AlertDialog.Builder(this)
                .setTitle("Descartar cambios")
                .setMessage("¿Estás seguro de que quieres descartar los cambios realizados?")
                .setPositiveButton("Sí") { dialog, which ->
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        mostrarDialogoDescartar()
    }
}