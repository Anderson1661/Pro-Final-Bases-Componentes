package transportadora.Administrador.Estados_conductor // Ajusta el paquete según tu estructura

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

class Editar_estados_conductor : AppCompatActivity() {
    private lateinit var txtIdEstado: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idEstadoConductor: Int = 0
    private lateinit var datosOriginales: EstadoConductor

    // Modelo de datos para el estado del conductor
    data class EstadoConductor(
        val id_estado_conductor: Int,
        val descripcion: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_estados_conductor) // Usar el nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_estados_conductor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_estado_conductor pasado desde la actividad anterior
        idEstadoConductor = intent.getIntExtra("id_estado_conductor", 0)

        // Inicializar vistas
        txtIdEstado = findViewById(R.id.txt_id_estado)
        txtDescripcion = findViewById(R.id.txt_descripcion)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdEstado.isEnabled = false

        // Cargar los datos del estado de conductor
        cargarEstadoConductor()

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

    private fun cargarEstadoConductor() {
        if (idEstadoConductor == 0) {
            Toast.makeText(this, "Error: ID de estado de conductor no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/consultar_estado_conductor.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_conductor", idEstadoConductor)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_estados_conductor", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_estado_conductor")
                        val descripcion = datos.getString("descripcion")

                        // Guardar datos originales
                        datosOriginales = EstadoConductor(id, descripcion)

                        // Mostrar los datos en los campos
                        txtIdEstado.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_estados_conductor", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_estados_conductor", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor del campo
        val descripcion = txtDescripcion.text.toString().trim()

        // Validaciones básicas
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

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/update.php" // Ruta del nuevo update.php
        val jsonObject = JSONObject().apply {
            put("id_estado_conductor", idEstadoConductor)
            put("descripcion", descripcion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_estados_conductor", "Respuesta actualización: $response")

                    // Nota: Si el PHP devuelve '1' aunque no haya cambios (línea 61 del update.php), se sigue considerando un éxito.
                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_estados_conductor", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_estados_conductor", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valor actual
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