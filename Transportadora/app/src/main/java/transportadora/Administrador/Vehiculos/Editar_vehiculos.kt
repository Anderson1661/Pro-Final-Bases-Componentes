package transportadora.Administrador.Vehiculos

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

class Editar_vehiculos : AppCompatActivity() {

    private lateinit var txtPlaca: EditText
    private lateinit var txtLineaVehiculo: EditText
    private lateinit var txtModelo: EditText
    private lateinit var txtIdColor: EditText
    private lateinit var txtIdMarca: EditText
    private lateinit var txtIdTipoServicio: EditText
    private lateinit var txtIdEstadoVehiculo: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private lateinit var placaOriginal: String
    private lateinit var datosOriginales: Vehiculo

    // Modelo de datos
    data class Vehiculo(
        val placa: String,
        val linea_vehiculo: String,
        val modelo: Int,
        val id_color: Int,
        val id_marca: Int,
        val id_tipo_servicio: Int,
        val id_estado_vehiculo: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_vehiculos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener la placa pasada desde la actividad anterior
        placaOriginal = intent.getStringExtra("placa") ?: ""

        // Inicializar vistas
        txtPlaca = findViewById(R.id.txt_placa)
        txtLineaVehiculo = findViewById(R.id.txt_linea_vehiculo)
        txtModelo = findViewById(R.id.txt_modelo)
        txtIdColor = findViewById(R.id.txt_id_color)
        txtIdMarca = findViewById(R.id.txt_id_marca)
        txtIdTipoServicio = findViewById(R.id.txt_id_tipo_servicio)
        txtIdEstadoVehiculo = findViewById(R.id.txt_id_estado_vehiculo)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        txtPlaca.isEnabled = false

        // Cargar los datos del Vehículo
        cargarVehiculo()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
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

    private fun cargarVehiculo() {
        if (placaOriginal.isEmpty()) {
            Toast.makeText(this, "Error: Placa de Vehículo no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/consultar_vehiculo.php"
        val jsonObject = JSONObject().apply {
            put("placa", placaOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_vehiculos", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val placa = datos.getString("placa")
                        val linea = datos.getString("linea_vehiculo")
                        val modelo = datos.getInt("modelo")
                        val idColor = datos.getInt("id_color")
                        val idMarca = datos.getInt("id_marca")
                        val idTipoServicio = datos.getInt("id_tipo_servicio")
                        val idEstado = datos.getInt("id_estado_vehiculo")

                        // Guardar datos originales
                        datosOriginales = Vehiculo(placa, linea, modelo, idColor, idMarca, idTipoServicio, idEstado)

                        // Mostrar los datos en los campos
                        txtPlaca.setText(placa)
                        txtLineaVehiculo.setText(linea)
                        txtModelo.setText(modelo.toString())
                        txtIdColor.setText(idColor.toString())
                        txtIdMarca.setText(idMarca.toString())
                        txtIdTipoServicio.setText(idTipoServicio.toString())
                        txtIdEstadoVehiculo.setText(idEstado.toString())

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_vehiculos", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_vehiculos", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // 1. Obtener valores
        val placaNueva = txtPlaca.text.toString().trim()
        val lineaVehiculo = txtLineaVehiculo.text.toString().trim()
        val modeloStr = txtModelo.text.toString().trim()
        val idColorStr = txtIdColor.text.toString().trim()
        val idMarcaStr = txtIdMarca.text.toString().trim()
        val idTipoServicioStr = txtIdTipoServicio.text.toString().trim()
        val idEstadoVehiculoStr = txtIdEstadoVehiculo.text.toString().trim()

        // 2. Validaciones básicas de no vacío
        if (placaNueva.isEmpty() || lineaVehiculo.isEmpty() || modeloStr.isEmpty() ||
            idColorStr.isEmpty() || idMarcaStr.isEmpty() || idTipoServicioStr.isEmpty() ||
            idEstadoVehiculoStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            return
        }

        // 3. Conversión y validación de formato/restricción
        val modelo: Int
        val idColor: Int
        val idMarca: Int
        val idTipoServicio: Int
        val idEstadoVehiculo: Int

        try {
            modelo = modeloStr.toInt()
            idColor = idColorStr.toInt()
            idMarca = idMarcaStr.toInt()
            idTipoServicio = idTipoServicioStr.toInt()
            idEstadoVehiculo = idEstadoVehiculoStr.toInt()

            if (modelo < 2010) {
                Toast.makeText(this, "El Modelo debe ser 2010 o posterior", Toast.LENGTH_LONG).show()
                txtModelo.requestFocus()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El Modelo y los IDs deben ser números válidos", Toast.LENGTH_LONG).show()
            return
        }

        // 4. Verificar si hubo cambios
        if (::datosOriginales.isInitialized &&
            placaNueva == datosOriginales.placa &&
            lineaVehiculo == datosOriginales.linea_vehiculo &&
            modelo == datosOriginales.modelo &&
            idColor == datosOriginales.id_color &&
            idMarca == datosOriginales.id_marca &&
            idTipoServicio == datosOriginales.id_tipo_servicio &&
            idEstadoVehiculo == datosOriginales.id_estado_vehiculo) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // 5. Construir JSON y enviar actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/update.php"
        val jsonObject = JSONObject().apply {
            put("placa_original", placaOriginal) // Se necesita la PK original para el WHERE en PHP
            put("placa_nueva", placaNueva)      // Se necesita la nueva placa para la actualización
            put("linea_vehiculo", lineaVehiculo)
            put("modelo", modelo)
            put("id_color", idColor)
            put("id_marca", idMarca)
            put("id_tipo_servicio", idTipoServicio)
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_vehiculos", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_vehiculos", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_vehiculos", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        if (!::datosOriginales.isInitialized) {
            finish()
            return
        }

        // Obtener valores actuales
        val placaActual = txtPlaca.text.toString().trim()
        val lineaVehiculoActual = txtLineaVehiculo.text.toString().trim()
        val modeloActual = txtModelo.text.toString().toIntOrNull() ?: 0
        val idColorActual = txtIdColor.text.toString().toIntOrNull() ?: 0
        val idMarcaActual = txtIdMarca.text.toString().toIntOrNull() ?: 0
        val idTipoServicioActual = txtIdTipoServicio.text.toString().toIntOrNull() ?: 0
        val idEstadoVehiculoActual = txtIdEstadoVehiculo.text.toString().toIntOrNull() ?: 0

        // Comparar con originales
        val hayCambios = placaActual != datosOriginales.placa ||
                lineaVehiculoActual != datosOriginales.linea_vehiculo ||
                modeloActual != datosOriginales.modelo ||
                idColorActual != datosOriginales.id_color ||
                idMarcaActual != datosOriginales.id_marca ||
                idTipoServicioActual != datosOriginales.id_tipo_servicio ||
                idEstadoVehiculoActual != datosOriginales.id_estado_vehiculo

        if (hayCambios) {
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