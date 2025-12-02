package transportadora.Administrador.Administradores

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

class Editar_administradores : AppCompatActivity() {
    // Declarar todos los EditText según los campos de la tabla
    private lateinit var txtId: EditText
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtTipoIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtGenero: EditText
    private lateinit var txtCodigoPostal: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idAdministrador: Int = 0
    private lateinit var datosOriginales: Administrador

    // Modelo de datos
    data class Administrador(
        val id_administrador: Int,
        val identificacion: String,
        val id_tipo_identificacion: Int,
        val nombre: String,
        val direccion: String,
        val correo: String,
        val id_genero: Int,
        val codigo_postal: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_administradores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_administradores)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_administrador pasado desde la actividad anterior
        idAdministrador = intent.getIntExtra("id_administrador", 0)

        // Inicializar vistas - usando IDs del XML actual
        txtId = findViewById(R.id.txt_pk)
        txtIdentificacion = findViewById(R.id.txt_id)
        txtTipoIdentificacion = findViewById(R.id.txt_tipo_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtDireccion = findViewById(R.id.txt_dir)
        txtCorreo = findViewById(R.id.txt_email)
        txtGenero = findViewById(R.id.txt_genero)
        txtCodigoPostal = findViewById(R.id.txt_codigo_postal)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Configurar inputTypes apropiados
        txtIdentificacion.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtTipoIdentificacion.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtGenero.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtCodigoPostal.inputType = android.text.InputType.TYPE_CLASS_TEXT
        txtCorreo.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        // Cargar los datos del administrador
        cargarAdministrador()

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

    private fun cargarAdministrador() {
        if (idAdministrador == 0) {
            Toast.makeText(this, "Error: ID de administrador no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/administrador/consultar_administrador.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_administradores", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer todos los datos
                        val id = datos.getInt("id_administrador")
                        val identificacion = datos.getString("identificacion")
                        val tipoIdentificacion = datos.getInt("id_tipo_identificacion")
                        val nombre = datos.getString("nombre")
                        val direccion = datos.getString("direccion")
                        val correo = datos.getString("correo")
                        val genero = datos.getInt("id_genero")
                        val codigoPostal = datos.getString("codigo_postal")

                        // Guardar datos originales
                        datosOriginales = Administrador(
                            id, identificacion, tipoIdentificacion, nombre,
                            direccion, correo, genero, codigoPostal
                        )

                        // Mostrar los datos en los campos
                        txtId.setText(id.toString())
                        txtIdentificacion.setText(identificacion)
                        txtTipoIdentificacion.setText(tipoIdentificacion.toString())
                        txtNombre.setText(nombre)
                        txtDireccion.setText(direccion)
                        txtCorreo.setText(correo)
                        txtGenero.setText(genero.toString())
                        txtCodigoPostal.setText(codigoPostal)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_administradores", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_administradores", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valores de todos los campos
        val identificacion = txtIdentificacion.text.toString().trim()
        val tipoIdentificacionStr = txtTipoIdentificacion.text.toString().trim()
        val nombre = txtNombre.text.toString().trim()
        val direccion = txtDireccion.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val generoStr = txtGenero.text.toString().trim()
        val codigoPostal = txtCodigoPostal.text.toString().trim()

        // Validaciones básicas
        if (identificacion.isEmpty()) {
            Toast.makeText(this, "La identificación es requerida", Toast.LENGTH_SHORT).show()
            txtIdentificacion.requestFocus()
            return
        }

        if (tipoIdentificacionStr.isEmpty()) {
            Toast.makeText(this, "El tipo de identificación es requerido", Toast.LENGTH_SHORT).show()
            txtTipoIdentificacion.requestFocus()
            return
        }

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            txtNombre.requestFocus()
            return
        }

        if (direccion.isEmpty()) {
            Toast.makeText(this, "La dirección es requerida", Toast.LENGTH_SHORT).show()
            txtDireccion.requestFocus()
            return
        }

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo es requerido", Toast.LENGTH_SHORT).show()
            txtCorreo.requestFocus()
            return
        }

        if (generoStr.isEmpty()) {
            Toast.makeText(this, "El género es requerido", Toast.LENGTH_SHORT).show()
            txtGenero.requestFocus()
            return
        }

        if (codigoPostal.isEmpty()) {
            Toast.makeText(this, "El código postal es requerido", Toast.LENGTH_SHORT).show()
            txtCodigoPostal.requestFocus()
            return
        }

        // Convertir valores numéricos
        val tipoIdentificacion: Int
        val genero: Int

        try {
            tipoIdentificacion = tipoIdentificacionStr.toInt()
            genero = generoStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Tipo de identificación y género deben ser números", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de correo básico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Formato de correo inválido", Toast.LENGTH_SHORT).show()
            txtCorreo.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (identificacion == datosOriginales.identificacion &&
            tipoIdentificacion == datosOriginales.id_tipo_identificacion &&
            nombre == datosOriginales.nombre &&
            direccion == datosOriginales.direccion &&
            correo == datosOriginales.correo &&
            genero == datosOriginales.id_genero &&
            codigoPostal == datosOriginales.codigo_postal) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/administrador/update.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
            put("identificacion", identificacion)
            put("id_tipo_identificacion", tipoIdentificacion)
            put("nombre", nombre)
            put("direccion", direccion)
            put("correo", correo)
            put("id_genero", genero)
            put("codigo_postal", codigoPostal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_administradores", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_administradores", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_administradores", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales
        val identificacionActual = txtIdentificacion.text.toString().trim()
        val tipoIdentificacionActual = txtTipoIdentificacion.text.toString().trim()
        val nombreActual = txtNombre.text.toString().trim()
        val direccionActual = txtDireccion.text.toString().trim()
        val correoActual = txtCorreo.text.toString().trim()
        val generoActual = txtGenero.text.toString().trim()
        val codigoPostalActual = txtCodigoPostal.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        try {
            val tipoIdNum = tipoIdentificacionActual.toInt()
            val generoNum = generoActual.toInt()

            if (identificacionActual != datosOriginales.identificacion ||
                tipoIdNum != datosOriginales.id_tipo_identificacion ||
                nombreActual != datosOriginales.nombre ||
                direccionActual != datosOriginales.direccion ||
                correoActual != datosOriginales.correo ||
                generoNum != datosOriginales.id_genero ||
                codigoPostalActual != datosOriginales.codigo_postal) {
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