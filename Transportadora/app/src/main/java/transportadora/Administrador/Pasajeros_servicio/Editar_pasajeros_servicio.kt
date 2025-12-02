package transportadora.Administrador.Pasajeros_servicio

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

class Editar_pasajeros_servicio : AppCompatActivity() {
    private lateinit var txtIdRuta: EditText
    private lateinit var txtNombrePasajero: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    // Clave primaria original para identificar el registro a actualizar/eliminar
    private var idRutaOriginal: Int = 0
    private lateinit var nombrePasajeroOriginal: String
    private lateinit var datosOriginales: PasajeroRuta // Para verificar si hubo cambios

    // Modelo de datos simplificado
    data class PasajeroRuta(
        val id_ruta: Int,
        val nombre_pasajero: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_pasajeros_servicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_pasajero_ruta)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener la clave primaria compuesta original de la actividad anterior
        idRutaOriginal = intent.getIntExtra("id_ruta", 0)
        nombrePasajeroOriginal = intent.getStringExtra("nombre_pasajero") ?: ""

        // Inicializar vistas
        txtIdRuta = findViewById(R.id.txt_id_ruta)
        txtNombrePasajero = findViewById(R.id.txt_nombre_pasajero)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar inputTypes
        txtIdRuta.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtNombrePasajero.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // Cargar los datos del pasajero en ruta
        cargarPasajeroRuta()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
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

    private fun cargarPasajeroRuta() {
        if (idRutaOriginal == 0 || nombrePasajeroOriginal.isEmpty()) {
            Toast.makeText(this, "Error: Clave de ruta/pasajero no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // URL del nuevo script de consulta
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/consultar_pasajero_ruta.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRutaOriginal)
            put("nombre_pasajero", nombrePasajeroOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarPasajeroRuta", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer los datos
                        val idRuta = datos.getInt("id_ruta")
                        val nombrePasajero = datos.getString("nombre_pasajero")

                        // Guardar datos originales (que deben coincidir con idRutaOriginal y nombrePasajeroOriginal)
                        datosOriginales = PasajeroRuta(idRuta, nombrePasajero)

                        // Mostrar los datos en los campos
                        txtIdRuta.setText(idRuta.toString())
                        txtNombrePasajero.setText(nombrePasajero)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("EditarPasajeroRuta", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("EditarPasajeroRuta", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valores NUEVOS de los campos
        val idRutaNuevoStr = txtIdRuta.text.toString().trim()
        val nombrePasajeroNuevo = txtNombrePasajero.text.toString().trim()

        // Validaciones básicas
        if (idRutaNuevoStr.isEmpty()) {
            Toast.makeText(this, "El ID de ruta es requerido", Toast.LENGTH_SHORT).show()
            txtIdRuta.requestFocus()
            return
        }
        if (nombrePasajeroNuevo.isEmpty()) {
            Toast.makeText(this, "El nombre del pasajero es requerido", Toast.LENGTH_SHORT).show()
            txtNombrePasajero.requestFocus()
            return
        }

        // Convertir valores numéricos
        val idRutaNuevo: Int
        try {
            idRutaNuevo = idRutaNuevoStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El ID de ruta debe ser un número", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si hubo cambios
        if (idRutaNuevo == datosOriginales.id_ruta && nombrePasajeroNuevo == datosOriginales.nombre_pasajero) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // URL del nuevo script de actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/update.php"

        // Crear JSON con la CLAVE ORIGINAL y los NUEVOS VALORES
        val jsonObject = JSONObject().apply {
            put("id_ruta_original", datosOriginales.id_ruta)
            put("nombre_pasajero_original", datosOriginales.nombre_pasajero)

            put("id_ruta_nuevo", idRutaNuevo)
            put("nombre_pasajero_nuevo", nombrePasajeroNuevo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarPasajeroRuta", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditarPasajeroRuta", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("EditarPasajeroRuta", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales de los campos de texto
        val idRutaActualStr = txtIdRuta.text.toString().trim()
        val nombrePasajeroActual = txtNombrePasajero.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        try {
            val idRutaActual = idRutaActualStr.toInt()

            if (idRutaActual != datosOriginales.id_ruta || nombrePasajeroActual != datosOriginales.nombre_pasajero) {
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
            // Si hay error en la conversión o datos originales no cargados, asumir que puede haber cambios
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