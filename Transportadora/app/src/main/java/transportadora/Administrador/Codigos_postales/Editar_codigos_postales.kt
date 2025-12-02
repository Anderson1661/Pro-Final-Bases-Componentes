package transportadora.Administrador.Codigos_postales

import android.annotation.SuppressLint
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

class Editar_codigos_postales : AppCompatActivity() {
    // Declarar todos los EditText según los campos de la tabla
    private lateinit var txtId: EditText
    private lateinit var txtPais: EditText
    private lateinit var txtDepartamento: EditText
    private lateinit var txtCiudad: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idCodigoPostal: String = ""
    private lateinit var datosOriginales: CodigoPostal

    // Modelo de datos
    data class CodigoPostal(
        val id_codigo_postal: String,
        val id_pais: Int,
        val departamento: String,
        val ciudad: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_codigos_postales)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_codigos_postales)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_codigo_postal pasado desde la actividad anterior
        idCodigoPostal = intent.getStringExtra("id_codigo_postal") ?: ""

        // Inicializar vistas - usando IDs del XML
        txtId = findViewById(R.id.txt_pk)
        txtPais = findViewById(R.id.txt_pais)
        txtDepartamento = findViewById(R.id.txt_departamento)
        txtCiudad = findViewById(R.id.txt_ciudad)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Configurar inputTypes apropiados
        txtPais.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtDepartamento.inputType = android.text.InputType.TYPE_CLASS_TEXT
        txtCiudad.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // Cargar los datos del código postal
        cargarCodigoPostal()

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

    private fun cargarCodigoPostal() {
        if (idCodigoPostal.isEmpty()) {
            Toast.makeText(this, "Error: ID de código postal no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/consultar_codigo_postal.php"
        val jsonObject = JSONObject().apply {
            put("id_codigo_postal", idCodigoPostal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_codigos_postales", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer todos los datos
                        val id = datos.getString("id_codigo_postal")
                        val pais = datos.getInt("id_pais")
                        val departamento = datos.getString("departamento")
                        val ciudad = datos.getString("ciudad")

                        // Guardar datos originales
                        datosOriginales = CodigoPostal(
                            id, pais, departamento, ciudad
                        )

                        // Mostrar los datos en los campos
                        txtId.setText(id)
                        txtPais.setText(pais.toString())
                        txtDepartamento.setText(departamento)
                        txtCiudad.setText(ciudad)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_codigos_postales", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_codigos_postales", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valores de todos los campos
        val paisStr = txtPais.text.toString().trim()
        val departamento = txtDepartamento.text.toString().trim()
        val ciudad = txtCiudad.text.toString().trim()

        // Validaciones básicas
        if (paisStr.isEmpty()) {
            Toast.makeText(this, "El ID del país es requerido", Toast.LENGTH_SHORT).show()
            txtPais.requestFocus()
            return
        }

        if (departamento.isEmpty()) {
            Toast.makeText(this, "El departamento es requerido", Toast.LENGTH_SHORT).show()
            txtDepartamento.requestFocus()
            return
        }

        if (ciudad.isEmpty()) {
            Toast.makeText(this, "La ciudad es requerida", Toast.LENGTH_SHORT).show()
            txtCiudad.requestFocus()
            return
        }

        // Convertir valores numéricos
        val pais: Int
        try {
            pais = paisStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El ID del país debe ser un número", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar longitud de texto
        if (departamento.length > 50) {
            Toast.makeText(this, "El departamento no debe exceder 50 caracteres", Toast.LENGTH_SHORT).show()
            txtDepartamento.requestFocus()
            return
        }

        if (ciudad.length > 50) {
            Toast.makeText(this, "La ciudad no debe exceder 50 caracteres", Toast.LENGTH_SHORT).show()
            txtCiudad.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (pais == datosOriginales.id_pais &&
            departamento == datosOriginales.departamento &&
            ciudad == datosOriginales.ciudad) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/update.php"
        val jsonObject = JSONObject().apply {
            put("id_codigo_postal", idCodigoPostal)
            put("id_pais", pais)
            put("departamento", departamento)
            put("ciudad", ciudad)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_codigos_postales", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_codigos_postales", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_codigos_postales", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales
        val paisActual = txtPais.text.toString().trim()
        val departamentoActual = txtDepartamento.text.toString().trim()
        val ciudadActual = txtCiudad.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        try {
            val paisNum = paisActual.toInt()

            if (paisNum != datosOriginales.id_pais ||
                departamentoActual != datosOriginales.departamento ||
                ciudadActual != datosOriginales.ciudad) {
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
            // Si hay error en la conversión, asumir que hay cambios
            AlertDialog.Builder(this)
                .setTitle("Descartar cambios")
                .setMessage("¿Estás seguro de que quieres descartar los cambios realizados?")
                .setPositiveButton("Sí") { dialog, which ->
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun onBackPressed() {
        mostrarDialogoDescartar()
    }
}