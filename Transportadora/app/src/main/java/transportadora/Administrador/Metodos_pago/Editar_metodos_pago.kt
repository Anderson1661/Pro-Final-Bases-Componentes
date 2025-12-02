package transportadora.Administrador.Metodos_pago // Ajusta el paquete si es necesario

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

class Editar_metodos_pago : AppCompatActivity() {
    private lateinit var txtIdMetodoPago: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idMetodoPago: Int = 0
    private lateinit var datosOriginales: MetodoPago

    // Modelo de datos para el método de pago
    data class MetodoPago(
        val id_metodo_pago: Int,
        val descripcion: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_metodos_pago) // Usar el nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_metodos_pago_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_metodo_pago pasado desde la actividad anterior
        idMetodoPago = intent.getIntExtra("id_metodo_pago", 0)

        // Inicializar vistas
        txtIdMetodoPago = findViewById(R.id.txt_id_metodo_pago)
        txtDescripcion = findViewById(R.id.txt_descripcion_metodo_pago)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdMetodoPago.isEnabled = false

        // Cargar los datos del método de pago
        cargarMetodoPago()

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

    private fun cargarMetodoPago() {
        if (idMetodoPago == 0) {
            Toast.makeText(this, "Error: ID de método de pago no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Usar la ruta del PHP de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/metodo_pago/consultar_metodo_pago.php"
        val jsonObject = JSONObject().apply {
            put("id_metodo_pago", idMetodoPago)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_metodos_pago", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_metodo_pago")
                        val descripcion = datos.getString("descripcion")

                        // Guardar datos originales
                        datosOriginales = MetodoPago(id, descripcion)

                        // Mostrar los datos en los campos
                        txtIdMetodoPago.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_metodos_pago", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_metodos_pago", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor del campo
        val descripcionActual = txtDescripcion.text.toString().trim()

        // Validaciones básicas
        if (descripcionActual.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            txtDescripcion.requestFocus()
            return
        }

        // La longitud máxima en la DB es 30 (VARCHAR(30))
        if (descripcionActual.length > 30) {
            Toast.makeText(this, "La descripción no puede superar los 30 caracteres", Toast.LENGTH_LONG).show()
            return
        }

        // Verificar si hubo cambios
        if (::datosOriginales.isInitialized && descripcionActual == datosOriginales.descripcion) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Usar la ruta del update.php
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/metodo_pago/update.php"
        val jsonObject = JSONObject().apply {
            put("id_metodo_pago", idMetodoPago)
            put("descripcion", descripcionActual)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_metodos_pago", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_metodos_pago", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_metodos_pago", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valor actual
        val descripcionActual = txtDescripcion.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        if (::datosOriginales.isInitialized && descripcionActual != datosOriginales.descripcion) {
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