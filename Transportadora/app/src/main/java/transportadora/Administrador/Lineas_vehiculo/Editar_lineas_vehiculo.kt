package transportadora.Administrador.Lineas_vehiculo // Ajusta el paquete si es necesario

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

class Editar_lineas_vehiculo : AppCompatActivity() {
    private lateinit var txtIdLinea: EditText
    private lateinit var txtIdMarca: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    // IDs originales para identificar el registro a editar (recibidos por Intent)
    private var idLineaOriginal: String = ""
    private var idMarcaOriginal: Int = 0
    private lateinit var datosOriginales: LineaVehiculo

    // Modelo de datos para la línea de vehículo
    data class LineaVehiculo(
        val id_linea: String,
        val id_marca: Int
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_lineas_vehiculo) // Usar el nuevo layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_lineas_vehiculo)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener la clave primaria compuesta pasada desde la actividad anterior
        idLineaOriginal = intent.getStringExtra("id_linea") ?: ""
        idMarcaOriginal = intent.getIntExtra("id_marca", 0)

        // Inicializar vistas
        txtIdLinea = findViewById(R.id.txt_id_linea)
        txtIdMarca = findViewById(R.id.txt_id_marca)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Cargar los datos de la línea de vehículo
        cargarLineaVehiculo()

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

    private fun cargarLineaVehiculo() {
        if (idLineaOriginal.isEmpty() || idMarcaOriginal == 0) {
            Toast.makeText(this, "Error: IDs de línea o marca no válidos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/consultar_linea_vehiculo.php"
        val jsonObject = JSONObject().apply {
            put("id_linea", idLineaOriginal)
            put("id_marca", idMarcaOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_lineas_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val idLinea = datos.getString("id_linea")
                        val idMarca = datos.getInt("id_marca")

                        // Guardar datos originales
                        datosOriginales = LineaVehiculo(idLinea, idMarca)

                        // Mostrar los datos en los campos
                        txtIdLinea.setText(idLinea)
                        txtIdMarca.setText(idMarca.toString())

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_lineas_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_lineas_vehiculo", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener nuevos valores del campo
        val idLineaNuevo = txtIdLinea.text.toString().trim()
        val idMarcaStringNuevo = txtIdMarca.text.toString().trim()
        val idMarcaNuevo: Int

        // Validaciones de formato y nulidad
        if (idLineaNuevo.isEmpty()) {
            Toast.makeText(this, "El nombre de la línea es requerido", Toast.LENGTH_SHORT).show()
            txtIdLinea.requestFocus()
            return
        }
        if (idMarcaStringNuevo.isEmpty()) {
            Toast.makeText(this, "El ID de la marca es requerido", Toast.LENGTH_SHORT).show()
            txtIdMarca.requestFocus()
            return
        }

        try {
            idMarcaNuevo = idMarcaStringNuevo.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El ID de la marca debe ser un número entero", Toast.LENGTH_SHORT).show()
            txtIdMarca.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (idLineaNuevo == datosOriginales.id_linea && idMarcaNuevo == datosOriginales.id_marca) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/update.php" // Ruta del nuevo update.php

        // Se envían los IDs originales y los IDs nuevos
        val jsonObject = JSONObject().apply {
            put("id_linea_original", datosOriginales.id_linea)
            put("id_marca_original", datosOriginales.id_marca)
            put("id_linea_nuevo", idLineaNuevo)
            put("id_marca_nuevo", idMarcaNuevo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_lineas_vehiculo", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_lineas_vehiculo", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar la línea", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_lineas_vehiculo", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales
        val idLineaActual = txtIdLinea.text.toString().trim()
        val idMarcaStringActual = txtIdMarca.text.toString().trim()
        val idMarcaActual: Int

        try {
            idMarcaActual = if (idMarcaStringActual.isNotEmpty()) idMarcaStringActual.toInt() else -1
        } catch (e: NumberFormatException) {
            // Si no es un número válido, consideramos que hay cambios
            showDiscardDialogUnconditional()
            return
        }

        // Verificar si hubo cambios
        if (idLineaActual != datosOriginales.id_linea || idMarcaActual != datosOriginales.id_marca) {
            showDiscardDialogUnconditional()
        } else {
            finish()
        }
    }

    private fun showDiscardDialogUnconditional() {
        AlertDialog.Builder(this)
            .setTitle("Descartar cambios")
            .setMessage("¿Estás seguro de que quieres descartar los cambios realizados?")
            .setPositiveButton("Sí") { dialog, which ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onBackPressed() {
        mostrarDialogoDescartar()
    }
}