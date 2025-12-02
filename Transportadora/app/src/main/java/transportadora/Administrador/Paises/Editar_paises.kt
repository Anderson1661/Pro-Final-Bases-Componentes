package transportadora.Administrador.Paises // Ajusta el paquete si es necesario

import android.annotation.SuppressLint
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
import transportadora.Configuracion.ApiConfig // Asegúrate de que esta clase exista
import transportadora.Login.R // Asegúrate de que R apunte a tu proyecto principal

class Editar_paises : AppCompatActivity() {
    private lateinit var txtIdPais: EditText
    private lateinit var txtNombre: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idPais: Int = 0
    private lateinit var datosOriginales: Pais

    // Modelo de datos para el País
    data class Pais(
        val id_pais: Int,
        val nombre: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_paises) // Usar el layout de Paises
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_paises_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_pais pasado desde la actividad anterior
        idPais = intent.getIntExtra("id_pais", 0)

        // Inicializar vistas
        txtIdPais = findViewById(R.id.txt_id_pais)
        txtNombre = findViewById(R.id.txt_nombre_pais)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdPais.isEnabled = false

        // Cargar los datos del país
        cargarPais()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
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

    private fun cargarPais() {
        if (idPais == 0) {
            Toast.makeText(this, "Error: ID de país no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Usar la ruta del PHP de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pais/consultar_pais.php"
        val jsonObject = JSONObject().apply {
            put("id_pais", idPais)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_paises", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_pais")
                        val nombre = datos.getString("nombre")

                        // Guardar datos originales
                        datosOriginales = Pais(id, nombre)

                        // Mostrar los datos en los campos
                        txtIdPais.setText(id.toString())
                        txtNombre.setText(nombre)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_paises", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_paises", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor del campo
        val nombreActual = txtNombre.text.toString().trim()

        // Validaciones básicas
        if (nombreActual.isEmpty()) {
            Toast.makeText(this, "El nombre del país es requerido", Toast.LENGTH_SHORT).show()
            txtNombre.requestFocus()
            return
        }

        // La longitud máxima en la DB es 50 (VARCHAR(50))
        if (nombreActual.length > 50) {
            Toast.makeText(this, "El nombre del país no puede superar los 50 caracteres", Toast.LENGTH_LONG).show()
            return
        }

        // Verificar si hubo cambios
        if (::datosOriginales.isInitialized && nombreActual == datosOriginales.nombre) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Usar la ruta del update.php
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pais/update.php"
        val jsonObject = JSONObject().apply {
            put("id_pais", idPais)
            put("nombre", nombreActual)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_paises", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_paises", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_paises", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valor actual
        val nombreActual = txtNombre.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        if (::datosOriginales.isInitialized && nombreActual != datosOriginales.nombre) {
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