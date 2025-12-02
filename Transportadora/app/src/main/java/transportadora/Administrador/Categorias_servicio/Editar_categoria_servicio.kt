package transportadora.Administrador.Categorias_servicio

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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class Editar_categoria_servicio : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtDescripcion: EditText
    private lateinit var txtValorKm: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idCategoriaServicio: Int = 0
    private lateinit var datosOriginales: CategoriaServicio

    // Modelo de datos
    data class CategoriaServicio(
        val id_categoria_servicio: Int,
        val descripcion: String,
        val valor_km: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_categoria_servicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_categoria_servicio pasado desde la actividad anterior
        idCategoriaServicio = intent.getIntExtra("id_categoria_servicio", 0)

        // Inicializar vistas - CORREGIDO con los IDs correctos del XML
        txtId = findViewById(R.id.txt_id)
        txtDescripcion = findViewById(R.id.txt_detalles)  // Cambiado a txt_detalles según XML
        txtValorKm = findViewById(R.id.txt_km)           // Cambiado a txt_km según XML
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Configurar inputType para valor_km
        txtValorKm.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        // Cargar los datos de la categoría
        cargarCategoria()

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

    private fun cargarCategoria() {
        if (idCategoriaServicio == 0) {
            Toast.makeText(this, "Error: ID de categoría no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/consultar_categoria_servicio.php"
        val jsonObject = JSONObject().apply {
            put("id_categoria_servicio", idCategoriaServicio)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_categoria_servicio", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")
                        val id = datos.getInt("id_categoria_servicio")
                        val descripcion = datos.getString("descripcion")
                        val valorKm = datos.getDouble("valor_km")

                        // Guardar datos originales
                        datosOriginales = CategoriaServicio(id, descripcion, valorKm)

                        // Mostrar los datos en los campos
                        txtId.setText(id.toString())
                        txtDescripcion.setText(descripcion)

                        // Formatear el valor km para mostrar sin decimales innecesarios
                        val formatter = DecimalFormat("#,##0.00")
                        txtValorKm.setText(formatter.format(valorKm))

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_categoria_servicio", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_categoria_servicio", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        val descripcion = txtDescripcion.text.toString().trim()
        val valorKmStr = txtValorKm.text.toString().trim()

        // Validaciones
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            txtDescripcion.requestFocus()
            return
        }

        if (valorKmStr.isEmpty()) {
            Toast.makeText(this, "El valor por kilómetro es requerido", Toast.LENGTH_SHORT).show()
            txtValorKm.requestFocus()
            return
        }

        // Convertir y validar valor_km
        val valorKm: Double
        try {
            // Limpiar formato de moneda si existe
            val cleanedValue = valorKmStr.replace("[^\\d.]".toRegex(), "")
            valorKm = cleanedValue.toDouble()

            if (valorKm <= 0) {
                Toast.makeText(this, "El valor por kilómetro debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                txtValorKm.requestFocus()
                return
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El valor por kilómetro debe ser un número válido", Toast.LENGTH_SHORT).show()
            txtValorKm.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (descripcion == datosOriginales.descripcion &&
            Math.abs(valorKm - datosOriginales.valor_km) < 0.01) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/update.php"
        val jsonObject = JSONObject().apply {
            put("id_categoria_servicio", idCategoriaServicio)
            put("descripcion", descripcion)
            put("valor_km", valorKm)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_categoria_servicio", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_categoria_servicio", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_categoria_servicio", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        val descripcionActual = txtDescripcion.text.toString().trim()
        val valorKmActual = txtValorKm.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        try {
            val valorKmDouble = if (valorKmActual.isNotEmpty()) {
                valorKmActual.replace("[^\\d.]".toRegex(), "").toDouble()
            } else {
                0.0
            }

            if (descripcionActual != datosOriginales.descripcion ||
                Math.abs(valorKmDouble - datosOriginales.valor_km) >= 0.01) {
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
        } catch (e: Exception) {
            finish()
        }
    }

    override fun onBackPressed() {
        mostrarDialogoDescartar()
    }
}