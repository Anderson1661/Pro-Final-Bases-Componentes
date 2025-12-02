package transportadora.Administrador.Usuarios

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

class Editar_usuarios : AppCompatActivity() {

    private lateinit var txtIdUsuario: EditText
    private lateinit var txtIdTipoUsuario: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtContrasenia: EditText // Campo de contraseña (opcional)
    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idUsuario: Int = 0
    private lateinit var datosOriginales: Usuario

    // Modelo de datos
    data class Usuario(
        val id_usuario: Int,
        val id_tipo_usuario: Int,
        val correo: String,
        val contrasenia: String // Se mantiene el original para verificar cambios
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_usuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_usuario pasado desde la actividad anterior
        idUsuario = intent.getIntExtra("id_usuario", 0)

        // Inicializar vistas
        txtIdUsuario = findViewById(R.id.txt_id_usuario)
        txtIdTipoUsuario = findViewById(R.id.txt_id_tipo_usuario)
        txtCorreo = findViewById(R.id.txt_correo)
        txtContrasenia = findViewById(R.id.txt_contrasenia)
        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtIdUsuario.isEnabled = false

        // Cargar los datos del Usuario
        cargarUsuario()

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

    private fun cargarUsuario() {
        if (idUsuario == 0) {
            Toast.makeText(this, "Error: ID de Usuario no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ruta adaptada para el nuevo script
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/consultar_usuario.php"
        val jsonObject = JSONObject().apply {
            put("id_usuario", idUsuario)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_usuarios", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer datos
                        val id = datos.getInt("id_usuario")
                        val idTipoUsuario = datos.getInt("id_tipo_usuario")
                        val correo = datos.getString("correo")
                        val contraseniaOriginal = datos.getString("contrasenia")

                        // Guardar datos originales
                        datosOriginales = Usuario(id, idTipoUsuario, correo, contraseniaOriginal)

                        // Mostrar los datos en los campos
                        txtIdUsuario.setText(id.toString())
                        txtIdTipoUsuario.setText(idTipoUsuario.toString())
                        txtCorreo.setText(correo)
                        // NO cargar la contraseña en el campo de texto por seguridad: txtContrasenia.setText(contraseniaOriginal)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_usuarios", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_usuarios", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // 1. Obtener valores
        val idTipoUsuarioStr = txtIdTipoUsuario.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val contrasenia = txtContrasenia.text.toString() // Puede estar vacío, no se hace trim para la contraseña

        // 2. Validaciones
        if (idTipoUsuarioStr.isEmpty()) {
            Toast.makeText(this, "El ID de Tipo de Usuario es requerido", Toast.LENGTH_SHORT).show()
            txtIdTipoUsuario.requestFocus()
            return
        }
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo electrónico inválido o vacío", Toast.LENGTH_SHORT).show()
            txtCorreo.requestFocus()
            return
        }

        // Convertir FK a Int
        val idTipoUsuario: Int
        try {
            idTipoUsuario = idTipoUsuarioStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "ID de Tipo de Usuario debe ser un número", Toast.LENGTH_SHORT).show()
            txtIdTipoUsuario.requestFocus()
            return
        }

        // 3. Verificar si hubo cambios (excluyendo la contraseña original que no se compara directamente)
        val huboCambios = idTipoUsuario != datosOriginales.id_tipo_usuario ||
                correo != datosOriginales.correo ||
                contrasenia.isNotEmpty() // Si el campo de contraseña no está vacío, es un cambio

        if (!huboCambios) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Construir JSON y enviar actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/update.php"
        val jsonObject = JSONObject().apply {
            put("id_usuario", idUsuario)
            put("id_tipo_usuario", idTipoUsuario)
            put("correo", correo)

            // Incluir contraseña solo si se ha proporcionado una nueva
            if (contrasenia.isNotEmpty()) {
                put("contrasenia", contrasenia)
            }
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_usuarios", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_usuarios", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_usuarios", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales para verificar cambios
        val idTipoUsuarioStr = txtIdTipoUsuario.text.toString().trim()
        val correoActual = txtCorreo.text.toString().trim()
        val contraseniaActual = txtContrasenia.text.toString()

        val idTipoUsuarioActual = try {
            idTipoUsuarioStr.toInt()
        } catch (e: NumberFormatException) {
            0
        }

        val hayCambios = idTipoUsuarioActual != datosOriginales.id_tipo_usuario ||
                correoActual != datosOriginales.correo ||
                contraseniaActual.isNotEmpty()

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