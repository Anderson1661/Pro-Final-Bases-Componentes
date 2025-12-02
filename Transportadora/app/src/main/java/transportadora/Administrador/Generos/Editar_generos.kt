package transportadora.Administrador.Generos

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
import transportadora.Login.R

class Editar_generos : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idGenero: Int = 0
    private lateinit var generoOriginal: Genero

    // Modelo de datos
    data class Genero(
        val id_genero: Int,
        val descripcion: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_generos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_genero pasado desde la actividad anterior
        idGenero = intent.getIntExtra("id_genero", 0)

        // Inicializar vistas
        txtId = findViewById(R.id.txt_id)
        txtDescripcion = findViewById(R.id.txt_descripcion)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Cargar los datos del género
        cargarGenero()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            mostrarDialogoDescartar()
        }

        // Botón Descartar cambios
        btnDescartar.setOnClickListener {
            mostrarDialogoDescartar()
        }

        // Botón Guardar cambios
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarGenero() {
        if (idGenero == 0) {
            Toast.makeText(this, "Error: ID de género no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/genero/consultar_genero.php"
        val jsonObject = JSONObject().apply {
            put("id_genero", idGenero)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_generos", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")
                        val id = datos.getInt("id_genero")
                        val descripcion = datos.getString("descripcion")

                        // Guardar datos originales
                        generoOriginal = Genero(id, descripcion)

                        // Mostrar los datos en los campos
                        txtId.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_generos", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_generos", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        val descripcion = txtDescripcion.text.toString().trim()

        // Validaciones
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            txtDescripcion.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (descripcion == generoOriginal.descripcion) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/genero/update.php"
        val jsonObject = JSONObject().apply {
            put("id_genero", idGenero)
            put("descripcion", descripcion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_generos", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_generos", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_generos", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        val descripcionActual = txtDescripcion.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        if (descripcionActual != generoOriginal.descripcion) {
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