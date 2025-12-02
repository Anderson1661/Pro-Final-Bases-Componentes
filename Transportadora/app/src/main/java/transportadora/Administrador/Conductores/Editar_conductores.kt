package transportadora.Administrador.Conductores

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

class Editar_conductores : AppCompatActivity() {
    // Declarar todos los EditText según los campos de la tabla
    private lateinit var txtId: EditText
    private lateinit var txtEstadoConductor: EditText
    private lateinit var txtPlacaVehiculo: EditText
    private lateinit var txtIdentificacion: EditText
    private lateinit var txtTipoIdentificacion: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtDireccion: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtGenero: EditText
    private lateinit var txtCodigoPostal: EditText
    private lateinit var txtPaisNacionalidad: EditText
    private lateinit var txtUrlFoto: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idConductor: Int = 0
    private lateinit var datosOriginales: Conductor

    // Modelo de datos
    data class Conductor(
        val id_conductor: Int,
        val id_estado_conductor: Int,
        val placa_vehiculo: String,
        val identificacion: String,
        val id_tipo_identificacion: Int,
        val nombre: String,
        val direccion: String,
        val correo: String,
        val id_genero: Int,
        val codigo_postal: String,
        val id_pais_nacionalidad: Int,
        val url_foto: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_conductores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_conductores)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el id_conductor pasado desde la actividad anterior
        idConductor = intent.getIntExtra("id_conductor", 0)

        // Inicializar vistas - usando IDs del XML
        txtId = findViewById(R.id.txt_pk)
        txtEstadoConductor = findViewById(R.id.txt_estado_conductor)
        txtPlacaVehiculo = findViewById(R.id.txt_placa)
        txtIdentificacion = findViewById(R.id.txt_id)
        txtTipoIdentificacion = findViewById(R.id.txt_tipo_id)
        txtNombre = findViewById(R.id.txt_nombre)
        txtDireccion = findViewById(R.id.txt_dir)
        txtCorreo = findViewById(R.id.txt_email)
        txtGenero = findViewById(R.id.txt_genero)
        txtCodigoPostal = findViewById(R.id.txt_codigo_postal)
        txtPaisNacionalidad = findViewById(R.id.txt_pais_nacionalidad)
        txtUrlFoto = findViewById(R.id.txt_url_foto)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)

        // Configurar el campo ID como no editable
        txtId.isEnabled = false

        // Configurar inputTypes apropiados
        txtEstadoConductor.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtPlacaVehiculo.inputType = android.text.InputType.TYPE_CLASS_TEXT
        txtIdentificacion.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtTipoIdentificacion.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtGenero.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtPaisNacionalidad.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        txtCorreo.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        txtUrlFoto.inputType = android.text.InputType.TYPE_CLASS_TEXT

        // Cargar los datos del conductor
        cargarConductor()

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

    private fun cargarConductor() {
        if (idConductor == 0) {
            Toast.makeText(this, "Error: ID de conductor no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/consultar_conductor.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_conductores", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Extraer todos los datos
                        val id = datos.getInt("id_conductor")
                        val estadoConductor = datos.getInt("id_estado_conductor")
                        val placaVehiculo = datos.getString("placa_vehiculo")
                        val identificacion = datos.getString("identificacion")
                        val tipoIdentificacion = datos.getInt("id_tipo_identificacion")
                        val nombre = datos.getString("nombre")
                        val direccion = datos.getString("direccion")
                        val correo = datos.getString("correo")
                        val genero = datos.getInt("id_genero")
                        val codigoPostal = datos.getString("codigo_postal")
                        val paisNacionalidad = datos.getInt("id_pais_nacionalidad")
                        val urlFoto = datos.getString("url_foto")

                        // Guardar datos originales
                        datosOriginales = Conductor(
                            id, estadoConductor, placaVehiculo, identificacion, tipoIdentificacion,
                            nombre, direccion, correo, genero, codigoPostal, paisNacionalidad, urlFoto
                        )

                        // Mostrar los datos en los campos
                        txtId.setText(id.toString())
                        txtEstadoConductor.setText(estadoConductor.toString())
                        txtPlacaVehiculo.setText(placaVehiculo)
                        txtIdentificacion.setText(identificacion)
                        txtTipoIdentificacion.setText(tipoIdentificacion.toString())
                        txtNombre.setText(nombre)
                        txtDireccion.setText(direccion)
                        txtCorreo.setText(correo)
                        txtGenero.setText(genero.toString())
                        txtCodigoPostal.setText(codigoPostal)
                        txtPaisNacionalidad.setText(paisNacionalidad.toString())
                        txtUrlFoto.setText(urlFoto)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_conductores", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("Editar_conductores", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun guardarCambios() {
        // Obtener valores de todos los campos
        val estadoConductorStr = txtEstadoConductor.text.toString().trim()
        val placaVehiculo = txtPlacaVehiculo.text.toString().trim()
        val identificacion = txtIdentificacion.text.toString().trim()
        val tipoIdentificacionStr = txtTipoIdentificacion.text.toString().trim()
        val nombre = txtNombre.text.toString().trim()
        val direccion = txtDireccion.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()
        val generoStr = txtGenero.text.toString().trim()
        val codigoPostal = txtCodigoPostal.text.toString().trim()
        val paisNacionalidadStr = txtPaisNacionalidad.text.toString().trim()
        val urlFoto = txtUrlFoto.text.toString().trim()

        // Validaciones básicas de campos requeridos
        val camposRequeridos = listOf(
            Pair(estadoConductorStr, "El estado del conductor"),
            Pair(placaVehiculo, "La placa del vehículo"),
            Pair(identificacion, "La identificación"),
            Pair(tipoIdentificacionStr, "El tipo de identificación"),
            Pair(nombre, "El nombre"),
            Pair(direccion, "La dirección"),
            Pair(correo, "El correo"),
            Pair(generoStr, "El género"),
            Pair(codigoPostal, "El código postal"),
            Pair(paisNacionalidadStr, "El país de nacionalidad"),
            Pair(urlFoto, "La URL de la foto")
        )

        for ((valor, nombreCampo) in camposRequeridos) {
            if (valor.isEmpty()) {
                Toast.makeText(this, "$nombreCampo es requerido", Toast.LENGTH_SHORT).show()
                when (nombreCampo) {
                    "El estado del conductor" -> txtEstadoConductor.requestFocus()
                    "La placa del vehículo" -> txtPlacaVehiculo.requestFocus()
                    "La identificación" -> txtIdentificacion.requestFocus()
                    "El tipo de identificación" -> txtTipoIdentificacion.requestFocus()
                    "El nombre" -> txtNombre.requestFocus()
                    "La dirección" -> txtDireccion.requestFocus()
                    "El correo" -> txtCorreo.requestFocus()
                    "El género" -> txtGenero.requestFocus()
                    "El código postal" -> txtCodigoPostal.requestFocus()
                    "El país de nacionalidad" -> txtPaisNacionalidad.requestFocus()
                    "La URL de la foto" -> txtUrlFoto.requestFocus()
                }
                return
            }
        }

        // Convertir valores numéricos
        val estadoConductor: Int
        val tipoIdentificacion: Int
        val genero: Int
        val paisNacionalidad: Int

        try {
            estadoConductor = estadoConductorStr.toInt()
            tipoIdentificacion = tipoIdentificacionStr.toInt()
            genero = generoStr.toInt()
            paisNacionalidad = paisNacionalidadStr.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Los campos numéricos deben contener números válidos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar longitudes máximas
        if (placaVehiculo.length > 20) {
            Toast.makeText(this, "La placa no debe exceder 20 caracteres", Toast.LENGTH_SHORT).show()
            txtPlacaVehiculo.requestFocus()
            return
        }

        if (identificacion.length > 20) {
            Toast.makeText(this, "La identificación no debe exceder 20 caracteres", Toast.LENGTH_SHORT).show()
            txtIdentificacion.requestFocus()
            return
        }

        if (nombre.length > 100) {
            Toast.makeText(this, "El nombre no debe exceder 100 caracteres", Toast.LENGTH_SHORT).show()
            txtNombre.requestFocus()
            return
        }

        if (direccion.length > 100) {
            Toast.makeText(this, "La dirección no debe exceder 100 caracteres", Toast.LENGTH_SHORT).show()
            txtDireccion.requestFocus()
            return
        }

        if (correo.length > 100) {
            Toast.makeText(this, "El correo no debe exceder 100 caracteres", Toast.LENGTH_SHORT).show()
            txtCorreo.requestFocus()
            return
        }

        if (codigoPostal.length > 10) {
            Toast.makeText(this, "El código postal no debe exceder 10 caracteres", Toast.LENGTH_SHORT).show()
            txtCodigoPostal.requestFocus()
            return
        }

        if (urlFoto.length > 255) {
            Toast.makeText(this, "La URL de la foto no debe exceder 255 caracteres", Toast.LENGTH_SHORT).show()
            txtUrlFoto.requestFocus()
            return
        }

        // Validar formato de correo básico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Formato de correo inválido", Toast.LENGTH_SHORT).show()
            txtCorreo.requestFocus()
            return
        }

        // Verificar si hubo cambios
        if (estadoConductor == datosOriginales.id_estado_conductor &&
            placaVehiculo == datosOriginales.placa_vehiculo &&
            identificacion == datosOriginales.identificacion &&
            tipoIdentificacion == datosOriginales.id_tipo_identificacion &&
            nombre == datosOriginales.nombre &&
            direccion == datosOriginales.direccion &&
            correo == datosOriginales.correo &&
            genero == datosOriginales.id_genero &&
            codigoPostal == datosOriginales.codigo_postal &&
            paisNacionalidad == datosOriginales.id_pais_nacionalidad &&
            urlFoto == datosOriginales.url_foto) {
            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/update.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
            put("id_estado_conductor", estadoConductor)
            put("placa_vehiculo", placaVehiculo)
            put("identificacion", identificacion)
            put("id_tipo_identificacion", tipoIdentificacion)
            put("nombre", nombre)
            put("direccion", direccion)
            put("correo", correo)
            put("id_genero", genero)
            put("codigo_postal", codigoPostal)
            put("id_pais_nacionalidad", paisNacionalidad)
            put("url_foto", urlFoto)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Editar_conductores", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        // Devolver resultado a la actividad anterior
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("Editar_conductores", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Editar_conductores", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Obtener valores actuales de todos los campos
        val estadoConductorActual = txtEstadoConductor.text.toString().trim()
        val placaVehiculoActual = txtPlacaVehiculo.text.toString().trim()
        val identificacionActual = txtIdentificacion.text.toString().trim()
        val tipoIdentificacionActual = txtTipoIdentificacion.text.toString().trim()
        val nombreActual = txtNombre.text.toString().trim()
        val direccionActual = txtDireccion.text.toString().trim()
        val correoActual = txtCorreo.text.toString().trim()
        val generoActual = txtGenero.text.toString().trim()
        val codigoPostalActual = txtCodigoPostal.text.toString().trim()
        val paisNacionalidadActual = txtPaisNacionalidad.text.toString().trim()
        val urlFotoActual = txtUrlFoto.text.toString().trim()

        // Solo mostrar diálogo si hay cambios
        try {
            val estadoNum = estadoConductorActual.toInt()
            val tipoIdNum = tipoIdentificacionActual.toInt()
            val generoNum = generoActual.toInt()
            val paisNum = paisNacionalidadActual.toInt()

            if (estadoNum != datosOriginales.id_estado_conductor ||
                placaVehiculoActual != datosOriginales.placa_vehiculo ||
                identificacionActual != datosOriginales.identificacion ||
                tipoIdNum != datosOriginales.id_tipo_identificacion ||
                nombreActual != datosOriginales.nombre ||
                direccionActual != datosOriginales.direccion ||
                correoActual != datosOriginales.correo ||
                generoNum != datosOriginales.id_genero ||
                codigoPostalActual != datosOriginales.codigo_postal ||
                paisNum != datosOriginales.id_pais_nacionalidad ||
                urlFotoActual != datosOriginales.url_foto) {
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