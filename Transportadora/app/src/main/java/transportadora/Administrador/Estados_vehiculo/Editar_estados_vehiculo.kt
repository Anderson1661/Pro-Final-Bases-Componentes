package transportadora.Administrador.Estados_vehiculo

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

class Editar_estados_vehiculo : AppCompatActivity() {
    private lateinit var txtIdEstadoVehiculo: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idEstadoVehiculo: Int = 0
    private lateinit var datosOriginales: EstadoVehiculo

    // Modelo de datos para el estado del vehículo
    data class EstadoVehiculo(
        val id_estado_vehiculo: Int,
        val descripcion: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_estados_vehiculo) // Usar el nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_estados_vehiculo)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_estado_vehiculo pasado desde la actividad anterior
        idEstadoVehiculo = intent.getIntExtra("id_estado_vehiculo", 0)

        // Inicializar vistas
        txtIdEstadoVehiculo = findViewById(R.id.txt_id_estado_vehiculo)
        txtDescripcion = findViewById(R.id.txt_descripcion_vehiculo)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdEstadoVehiculo.isEnabled = false

        // Cargar los datos del estado de vehículo
        cargarEstadoVehiculo()

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

    private fun cargarEstadoVehiculo() {
        if (idEstadoVehiculo == 0) {
            Toast.makeText(this, "Error: ID de estado de vehículo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Usar la ruta del nuevo PHP de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_vehiculo/consultar_estado_vehiculo.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_estados_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_estado_vehiculo")
                        val descripcion = datos.getString("descripcion")

                        // Guardar datos originales
                        datosOriginales = EstadoVehiculo(id, descripcion)

                        // Mostrar los datos en los campos
                        txtIdEstadoVehiculo.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_estados_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_estados_vehiculo", "Volley error: ${error.message}")
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

        // Usar la ruta del nuevo update.php
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_vehiculo/update.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_vehiculo", idEstadoVehiculo)
            put("descripcion", descripcion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_estados_vehiculo", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_estados_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_estados_vehiculo", "Volley error: ${error.message}")
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