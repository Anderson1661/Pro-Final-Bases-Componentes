package transportadora.Administrador.Tipos_identificacion

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

class Editar_tipos_identificacion : AppCompatActivity() {

    private lateinit var txtId: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idTipoIdentificacion: Int = 0
    private lateinit var datosOriginales: TipoIdentificacion

    // Modelo de datos simple
    data class TipoIdentificacion(
        val id_tipo_identificacion: Int,
        val descripcion: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_tipos_identificacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_tipo_identificacion pasado desde la actividad anterior
        idTipoIdentificacion = intent.getIntExtra("id_tipo_identificacion", 0)

        // Inicializar vistas
        txtId = findViewById(R.id.txt_id)
        txtDescripcion = findViewById(R.id.txt_descripcion)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Cargar los datos del Tipo de Identificación
        cargarTipoIdentificacion()

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

    private fun cargarTipoIdentificacion() {
        if (idTipoIdentificacion == 0) {
            Toast.makeText(this, "Error: ID de Tipo de Identificación no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ruta adaptada para el nuevo script
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_identificacion/consultar_tipo_identificacion.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_identificacion", idTipoIdentificacion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_tipos_identificacion", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_tipo_identificacion")
                        val descripcion = datos.getString("descripcion")

                        // Guardar datos originales
                        datosOriginales = TipoIdentificacion(id, descripcion)

                        // Mostrar los datos en los campos
                        txtId.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_tipos_identificacion", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_tipos_identificacion", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor del campo editable
        val descripcion = txtDescripcion.text.toString().trim()

        // Validaciones
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            txtDescripcion.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (descripcion == datosOriginales.descripcion) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Ruta adaptada para el nuevo script de actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_identificacion/update.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_identificacion", idTipoIdentificacion)
            put("descripcion", descripcion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_tipos_identificacion", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior (por ejemplo, recargar lista)
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_tipos_identificacion", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_tipos_identificacion", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener el valor actual
        val descripcionActual = txtDescripcion.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        if (descripcionActual != datosOriginales.descripcion) {
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