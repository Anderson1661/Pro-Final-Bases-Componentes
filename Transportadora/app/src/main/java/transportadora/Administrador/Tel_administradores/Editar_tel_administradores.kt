package transportadora.Administrador.Tel_administradores // Asegúrate de que el paquete sea correcto

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
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R // Asume tu ruta a R

class Editar_tel_administradores : AppCompatActivity() {

    private lateinit var txtIdAdministrador: EditText
    private lateinit var txtTelefonoNuevo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idAdministradorOriginal: Int = 0
    private lateinit var telefonoOriginal: String // Usamos String para BIGINT

    // Data Class para almacenar los datos originales
    data class TelefonoAdminData(
        val id_administrador: Int,
        val telefono: String
    )
    private lateinit var datosOriginales: TelefonoAdminData

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_tel_administradores)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el ID y Teléfono originales pasados por el Intent
        idAdministradorOriginal = intent.getIntExtra("id_administrador", 0)
        telefonoOriginal = intent.getStringExtra("telefono") ?: ""

        // Inicializar vistas
        inicializarVistas()

        // Configuración de campos no editables
        txtIdAdministrador.isEnabled = false

        // Cargar los datos
        if (idAdministradorOriginal != 0 && telefonoOriginal.isNotEmpty()) {
            cargarTelefonoAdministrador()
        } else {
            Toast.makeText(this, "Error: Datos de clave primaria no válidos", Toast.LENGTH_SHORT).show()
            finish()
        }

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2) // Asumiendo el ID correcto
        btnVolver.setOnClickListener { mostrarDialogoDescartar() }
        btnDescartar.setOnClickListener { mostrarDialogoDescartar() }
        btnGuardar.setOnClickListener { guardarCambios() }
    }

    private fun inicializarVistas() {
        txtIdAdministrador = findViewById(R.id.txt_id_cliente)
        txtTelefonoNuevo = findViewById(R.id.txt_telefono)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)
    }

    private fun cargarTelefonoAdministrador() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/consultar_tel_administrador.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministradorOriginal)
            put("telefono", telefonoOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Mapear datos originales
                        datosOriginales = TelefonoAdminData(
                            id_administrador = datos.getInt("id_administrador"),
                            telefono = datos.getString("telefono") // Teléfono original
                        )

                        // Rellenar campos: ID original y Teléfono para ser editado
                        txtIdAdministrador.setText(datosOriginales.id_administrador.toString())
                        txtTelefonoNuevo.setText(datosOriginales.telefono)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("EditarTelAdmin", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("EditarTelAdmin", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun validarCambios(): Boolean {
        val telefonoNuevo = txtTelefonoNuevo.text.toString().trim()

        if (telefonoNuevo.isEmpty()) {
            Toast.makeText(this, "El campo Teléfono no puede estar vacío.", Toast.LENGTH_LONG).show()
            return false
        }

        // El tipo BIGINT en MySQL se mapea a Long en Kotlin, por lo que validamos el formato.
        if (telefonoNuevo.toLongOrNull() == null) {
            Toast.makeText(this, "El Teléfono debe ser un número válido.", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun guardarCambios() {
        if (!::datosOriginales.isInitialized) {
            Toast.makeText(this, "Error: Datos originales no cargados.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validarCambios()) return

        val telefonoNuevo = txtTelefonoNuevo.text.toString().trim()

        // Verificar si hubo cambios en el único campo editable (Teléfono)
        if (telefonoNuevo == datosOriginales.telefono) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear JSON para la actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/update.php"

        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministradorOriginal)
            put("telefono_original", telefonoOriginal)
            put("telefono_nuevo", telefonoNuevo) // El único campo modificado/nuevo
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarTelAdmin", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditarTelAdmin", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("EditarTelAdmin", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun hayCambios(): Boolean {
        // Comprobar si hay cambios solo en el teléfono
        return txtTelefonoNuevo.text.toString().trim() != (if (::datosOriginales.isInitialized) datosOriginales.telefono else "")
    }

    private fun mostrarDialogoDescartar() {
        if (::datosOriginales.isInitialized && hayCambios()) {
            AlertDialog.Builder(this)
                .setTitle("Descartar cambios")
                .setMessage("¿Estás seguro de que quieres descartar los cambios realizados?")
                .setPositiveButton("Sí") { _, _ -> finish() }
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