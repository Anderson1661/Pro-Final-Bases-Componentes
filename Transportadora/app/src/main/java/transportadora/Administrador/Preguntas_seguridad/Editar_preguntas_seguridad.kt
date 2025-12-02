package transportadora.Administrador.Preguntas_seguridad // Asume un paquete adecuado

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
import transportadora.Login.R // Asume que R.layout.activity_editar_preguntas_seguridad está aquí

class Editar_preguntas_seguridad : AppCompatActivity() {
    private lateinit var txtIdPregunta: EditText
    private lateinit var txtDescripcion: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idPreguntaOriginal: Int = 0
    private lateinit var descripcionOriginal: String // Para verificar si hubo cambios

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_preguntas_seguridad)

        // Ajuste de insets (siguiendo el patrón)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_pregunta_seguridad)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el ID de la pregunta de la actividad anterior
        idPreguntaOriginal = intent.getIntExtra("id_pregunta", 0)

        // Inicializar vistas
        txtIdPregunta = findViewById(R.id.txt_id_pregunta)
        txtDescripcion = findViewById(R.id.txt_descripcion)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configuración de campos
        txtIdPregunta.isEnabled = false // El ID no es editable
        txtIdPregunta.isFocusable = false
        txtDescripcion.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // Cargar los datos
        cargarPreguntaSeguridad()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2) // Usando ID del XML
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

    private fun cargarPreguntaSeguridad() {
        if (idPreguntaOriginal == 0) {
            Toast.makeText(this, "Error: ID de pregunta no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // URL del script de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/preguntas_seguridad/consultar_pregunta_seguridad.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPreguntaOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarPreguntaSeguridad", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer y guardar los datos
                        val idPregunta = datos.getInt("id_pregunta")
                        val descripcion = datos.getString("descripcion")

                        // Guardar la descripción original para comparación
                        descripcionOriginal = descripcion

                        // Mostrar los datos en los campos
                        txtIdPregunta.setText(idPregunta.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("EditarPreguntaSeguridad", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("EditarPreguntaSeguridad", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor NUEVO
        val descripcionNueva = txtDescripcion.text.toString().trim()

        // Validaciones básicas
        if (descripcionNueva.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            txtDescripcion.requestFocus()
            return
        }

        // Verificar si hubo cambios
        // Se asume que descripcionOriginal ha sido cargada
        if (::descripcionOriginal.isInitialized && descripcionNueva == descripcionOriginal) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // URL del script de actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/preguntas_seguridad/update.php"

        // Crear JSON con el ID y el nuevo valor
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPreguntaOriginal) // Clave primaria
            put("descripcion", descripcionNueva) // Nuevo valor a actualizar
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarPreguntaSeguridad", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditarPreguntaSeguridad", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("EditarPreguntaSeguridad", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        val descripcionActual = txtDescripcion.text.toString().trim()

        // Solo mostrar diálogo si hubo un cambio en la descripción y si la original está inicializada
        if (::descripcionOriginal.isInitialized && descripcionActual != descripcionOriginal) {
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