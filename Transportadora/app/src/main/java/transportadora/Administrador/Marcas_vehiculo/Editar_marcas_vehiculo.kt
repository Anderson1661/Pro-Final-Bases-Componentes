package transportadora.Administrador.Marcas_vehiculo // Ajusta el paquete si es necesario

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

class Editar_marcas_vehiculo : AppCompatActivity() {
    private lateinit var txtIdMarca: EditText
    private lateinit var txtNombreMarca: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idMarca: Int = 0
    private lateinit var datosOriginales: MarcaVehiculo

    // Modelo de datos para la marca de vehículo
    data class MarcaVehiculo(
        val id_marca: Int,
        val nombre_marca: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_marcas_vehiculo) // Usar el nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_marcas_vehiculo)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_marca pasado desde la actividad anterior
        idMarca = intent.getIntExtra("id_marca", 0)

        // Inicializar vistas
        txtIdMarca = findViewById(R.id.txt_id_marca)
        txtNombreMarca = findViewById(R.id.txt_nombre_marca)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdMarca.isEnabled = false

        // Cargar los datos de la marca de vehículo
        cargarMarcaVehiculo()

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

    private fun cargarMarcaVehiculo() {
        if (idMarca == 0) {
            Toast.makeText(this, "Error: ID de marca de vehículo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Usar la ruta del nuevo PHP de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/marca_vehiculo/consultar_marca_vehiculo.php"
        val jsonObject = JSONObject().apply {
            put("id_marca", idMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_marcas_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_marca")
                        val nombreMarca = datos.getString("nombre_marca")

                        // Guardar datos originales
                        datosOriginales = MarcaVehiculo(id, nombreMarca)

                        // Mostrar los datos en los campos
                        txtIdMarca.setText(id.toString())
                        txtNombreMarca.setText(nombreMarca)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_marcas_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_marcas_vehiculo", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valor del campo
        val nombreMarca = txtNombreMarca.text.toString().trim()

        // Validaciones básicas
        if (nombreMarca.isEmpty()) {
            Toast.makeText(this, "El nombre de la marca es requerido", Toast.LENGTH_SHORT).show()
            txtNombreMarca.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (nombreMarca == datosOriginales.nombre_marca) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Usar la ruta del nuevo update.php
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/marca_vehiculo/update.php"
        val jsonObject = JSONObject().apply {
            put("id_marca", idMarca)
            put("nombre_marca", nombreMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_marcas_vehiculo", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_marcas_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_marcas_vehiculo", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valor actual
        val nombreMarcaActual = txtNombreMarca.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        if (nombreMarcaActual != datosOriginales.nombre_marca) {
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