package transportadora.Administrador.Servicios // Ajusta el paquete si es necesario

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

class Editar_servicios : AppCompatActivity() {

    // 1. Declaración de todos los EditText
    private lateinit var txtIdRuta: EditText
    private lateinit var txtDireccionOrigen: EditText
    private lateinit var txtDireccionDestino: EditText
    private lateinit var txtIdCodigoPostalOrigen: EditText
    private lateinit var txtIdCodigoPostalDestino: EditText
    private lateinit var txtDistanciaKm: EditText
    private lateinit var txtFechaHoraReserva: EditText
    private lateinit var txtFechaHoraOrigen: EditText // Nullable
    private lateinit var txtFechaHoraDestino: EditText // Nullable
    private lateinit var txtIdConductor: EditText // Nullable FK
    private lateinit var txtIdTipoServicio: EditText
    private lateinit var txtIdCliente: EditText
    private lateinit var txtIdEstadoServicio: EditText
    private lateinit var txtIdCategoriaServicio: EditText
    private lateinit var txtIdMetodoPago: EditText
    private lateinit var txtTotal: EditText // No editable (solo display)
    private lateinit var txtPagoConductor: EditText // No editable (solo display)

    private lateinit var btnGuardar: Button
    private lateinit var btnDescartar: Button

    private var idRutaOriginal: Int = 0
    private lateinit var datosOriginales: RutaData

    // Data Class para almacenar los datos originales
    data class RutaData(
        val id_ruta: Int,
        val direccion_origen: String,
        val direccion_destino: String,
        val id_codigo_postal_origen: String,
        val id_codigo_postal_destino: String,
        val distancia_km: String,
        val fecha_hora_reserva: String,
        val fecha_hora_origen: String,
        val fecha_hora_destino: String,
        val id_conductor: String,
        val id_tipo_servicio: Int,
        val id_cliente: Int,
        val id_estado_servicio: Int,
        val id_categoria_servicio: Int,
        val id_metodo_pago: Int,
        val total: String, // Usamos String para los decimales
        val pago_conductor: String
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_servicios)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editar_servicio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el ID de la ruta
        idRutaOriginal = intent.getIntExtra("id_ruta", 0)

        // Inicializar vistas
        inicializarVistas()

        // Configuración de campos no editables
        txtIdRuta.isEnabled = false
        txtTotal.isEnabled = false
        txtPagoConductor.isEnabled = false

        // Cargar los datos
        cargarRuta()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        btnVolver.setOnClickListener { mostrarDialogoDescartar() }
        btnDescartar.setOnClickListener { mostrarDialogoDescartar() }
        btnGuardar.setOnClickListener { guardarCambios() }
    }

    private fun inicializarVistas() {
        txtIdRuta = findViewById(R.id.txt_id_ruta)
        txtDireccionOrigen = findViewById(R.id.txt_direccion_origen)
        txtDireccionDestino = findViewById(R.id.txt_direccion_destino)
        txtIdCodigoPostalOrigen = findViewById(R.id.txt_id_codigo_postal_origen)
        txtIdCodigoPostalDestino = findViewById(R.id.txt_id_codigo_postal_destino)
        txtDistanciaKm = findViewById(R.id.txt_distancia_km)
        txtFechaHoraReserva = findViewById(R.id.txt_fecha_hora_reserva)
        txtFechaHoraOrigen = findViewById(R.id.txt_fecha_hora_origen)
        txtFechaHoraDestino = findViewById(R.id.txt_fecha_hora_destino)
        txtIdConductor = findViewById(R.id.txt_id_conductor)
        txtIdTipoServicio = findViewById(R.id.txt_id_tipo_servicio)
        txtIdCliente = findViewById(R.id.txt_id_cliente)
        txtIdEstadoServicio = findViewById(R.id.txt_id_estado_servicio)
        txtIdCategoriaServicio = findViewById(R.id.txt_id_categoria_servicio)
        txtIdMetodoPago = findViewById(R.id.txt_id_metodo_pago)
        txtTotal = findViewById(R.id.txt_total)
        txtPagoConductor = findViewById(R.id.txt_pago_conductor)

        btnGuardar = findViewById(R.id.buttonGuardar)
        btnDescartar = findViewById(R.id.buttonDescartar)
    }

    private fun cargarRuta() {
        if (idRutaOriginal == 0) {
            Toast.makeText(this, "Error: ID de ruta no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/consultar_ruta.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRutaOriginal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datos = response.getJSONObject("datos")

                        // Mapear datos originales
                        datosOriginales = RutaData(
                            id_ruta = datos.getInt("id_ruta"),
                            direccion_origen = datos.getString("direccion_origen"),
                            direccion_destino = datos.getString("direccion_destino"),
                            id_codigo_postal_origen = datos.getString("id_codigo_postal_origen"),
                            id_codigo_postal_destino = datos.getString("id_codigo_postal_destino"),
                            distancia_km = datos.getString("distancia_km"),
                            fecha_hora_reserva = datos.getString("fecha_hora_reserva"),
                            fecha_hora_origen = datos.getString("fecha_hora_origen"),
                            fecha_hora_destino = datos.getString("fecha_hora_destino"),
                            id_conductor = datos.getString("id_conductor"),
                            id_tipo_servicio = datos.getString("id_tipo_servicio").toInt(),
                            id_cliente = datos.getString("id_cliente").toInt(),
                            id_estado_servicio = datos.getString("id_estado_servicio").toInt(),
                            id_categoria_servicio = datos.getString("id_categoria_servicio").toInt(),
                            id_metodo_pago = datos.getString("id_metodo_pago").toInt(),
                            total = datos.getString("total"),
                            pago_conductor = datos.getString("pago_conductor")
                        )

                        // Rellenar campos
                        txtIdRuta.setText(datosOriginales.id_ruta.toString())
                        txtDireccionOrigen.setText(datosOriginales.direccion_origen)
                        txtDireccionDestino.setText(datosOriginales.direccion_destino)
                        txtIdCodigoPostalOrigen.setText(datosOriginales.id_codigo_postal_origen)
                        txtIdCodigoPostalDestino.setText(datosOriginales.id_codigo_postal_destino)
                        txtDistanciaKm.setText(datosOriginales.distancia_km)
                        txtFechaHoraReserva.setText(datosOriginales.fecha_hora_reserva)
                        txtFechaHoraOrigen.setText(datosOriginales.fecha_hora_origen)
                        txtFechaHoraDestino.setText(datosOriginales.fecha_hora_destino)
                        txtIdConductor.setText(datosOriginales.id_conductor)
                        txtIdTipoServicio.setText(datosOriginales.id_tipo_servicio.toString())
                        txtIdCliente.setText(datosOriginales.id_cliente.toString())
                        txtIdEstadoServicio.setText(datosOriginales.id_estado_servicio.toString())
                        txtIdCategoriaServicio.setText(datosOriginales.id_categoria_servicio.toString())
                        txtIdMetodoPago.setText(datosOriginales.id_metodo_pago.toString())
                        txtTotal.setText(datosOriginales.total)
                        txtPagoConductor.setText(datosOriginales.pago_conductor)

                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("EditarServicios", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            { error ->
                Log.e("EditarServicios", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
                finish()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun validarCambios(): Boolean {
        // Validaciones de campos NOT NULL (11 campos)
        if (txtDireccionOrigen.text.isNullOrEmpty() ||
            txtDireccionDestino.text.isNullOrEmpty() ||
            txtIdCodigoPostalOrigen.text.isNullOrEmpty() ||
            txtIdCodigoPostalDestino.text.isNullOrEmpty() ||
            txtDistanciaKm.text.isNullOrEmpty() ||
            txtFechaHoraReserva.text.isNullOrEmpty() ||
            txtIdTipoServicio.text.isNullOrEmpty() ||
            txtIdCliente.text.isNullOrEmpty() ||
            txtIdEstadoServicio.text.isNullOrEmpty() ||
            txtIdCategoriaServicio.text.isNullOrEmpty() ||
            txtIdMetodoPago.text.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos obligatorios.", Toast.LENGTH_LONG).show()
            return false
        }

        // Validación de formato de distancia (DECIMAL)
        try {
            if (txtDistanciaKm.text.toString().toDouble() <= 0) {
                Toast.makeText(this, "La distancia debe ser mayor a 0.", Toast.LENGTH_LONG).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "La distancia debe ser un número válido.", Toast.LENGTH_LONG).show()
            return false
        }

        // **Nota:** En una aplicación real, se deben validar formatos de fecha/hora y que los IDs de FKs sean números enteros.

        return true
    }

    private fun guardarCambios() {
        if (!::datosOriginales.isInitialized) {
            Toast.makeText(this, "Error: Datos originales no cargados.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validarCambios()) return

        // Mapear los datos actuales
        val nuevosDatos = RutaData(
            id_ruta = idRutaOriginal,
            direccion_origen = txtDireccionOrigen.text.toString().trim(),
            direccion_destino = txtDireccionDestino.text.toString().trim(),
            id_codigo_postal_origen = txtIdCodigoPostalOrigen.text.toString().trim(),
            id_codigo_postal_destino = txtIdCodigoPostalDestino.text.toString().trim(),
            distancia_km = txtDistanciaKm.text.toString().trim(),
            fecha_hora_reserva = txtFechaHoraReserva.text.toString().trim(),
            fecha_hora_origen = txtFechaHoraOrigen.text.toString().trim(),
            fecha_hora_destino = txtFechaHoraDestino.text.toString().trim(),
            id_conductor = txtIdConductor.text.toString().trim(),
            id_tipo_servicio = txtIdTipoServicio.text.toString().toIntOrNull() ?: 0,
            id_cliente = txtIdCliente.text.toString().toIntOrNull() ?: 0,
            id_estado_servicio = txtIdEstadoServicio.text.toString().toIntOrNull() ?: 0,
            id_categoria_servicio = txtIdCategoriaServicio.text.toString().toIntOrNull() ?: 0,
            id_metodo_pago = txtIdMetodoPago.text.toString().toIntOrNull() ?: 0,
            total = datosOriginales.total, // No editable
            pago_conductor = datosOriginales.pago_conductor // No editable
        )

        // 2. Verificar si hubo cambios en alguno de los 14 campos editables
        if (nuevosDatos.direccion_origen == datosOriginales.direccion_origen &&
            nuevosDatos.direccion_destino == datosOriginales.direccion_destino &&
            nuevosDatos.id_codigo_postal_origen == datosOriginales.id_codigo_postal_origen &&
            nuevosDatos.id_codigo_postal_destino == datosOriginales.id_codigo_postal_destino &&
            nuevosDatos.distancia_km == datosOriginales.distancia_km &&
            nuevosDatos.fecha_hora_reserva == datosOriginales.fecha_hora_reserva &&
            nuevosDatos.fecha_hora_origen == datosOriginales.fecha_hora_origen &&
            nuevosDatos.fecha_hora_destino == datosOriginales.fecha_hora_destino &&
            nuevosDatos.id_conductor == datosOriginales.id_conductor &&
            nuevosDatos.id_tipo_servicio == datosOriginales.id_tipo_servicio &&
            nuevosDatos.id_cliente == datosOriginales.id_cliente &&
            nuevosDatos.id_estado_servicio == datosOriginales.id_estado_servicio &&
            nuevosDatos.id_categoria_servicio == datosOriginales.id_categoria_servicio &&
            nuevosDatos.id_metodo_pago == datosOriginales.id_metodo_pago) {

            Toast.makeText(this, "No se realizaron cambios", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Crear JSON para la actualización
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/update.php"

        val jsonObject = JSONObject().apply {
            put("id_ruta", idRutaOriginal)
            put("direccion_origen", nuevosDatos.direccion_origen)
            put("direccion_destino", nuevosDatos.direccion_destino)
            put("id_codigo_postal_origen", nuevosDatos.id_codigo_postal_origen)
            put("id_codigo_postal_destino", nuevosDatos.id_codigo_postal_destino)
            put("distancia_km", nuevosDatos.distancia_km)
            put("fecha_hora_reserva", nuevosDatos.fecha_hora_reserva)
            put("fecha_hora_origen", nuevosDatos.fecha_hora_origen.ifEmpty { null }) // Enviar null si está vacío
            put("fecha_hora_destino", nuevosDatos.fecha_hora_destino.ifEmpty { null }) // Enviar null si está vacío
            put("id_conductor", nuevosDatos.id_conductor.ifEmpty { null }) // Enviar null si está vacío
            put("id_tipo_servicio", nuevosDatos.id_tipo_servicio)
            put("id_cliente", nuevosDatos.id_cliente)
            put("id_estado_servicio", nuevosDatos.id_estado_servicio)
            put("id_categoria_servicio", nuevosDatos.id_categoria_servicio)
            put("id_metodo_pago", nuevosDatos.id_metodo_pago)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("EditarServicios", "Respuesta actualización: $response")

                    if (response.getString("success") == "1") {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("EditarServicios", "Error al procesar la respuesta: ${e.message}")
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("EditarServicios", "Volley error: ${error.message}")
                Toast.makeText(this, "Error de conexión al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoDescartar() {
        // Simple chequeo de inicialización, la verificación completa está en guardarCambios
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

    private fun hayCambios(): Boolean {
        return !(txtDireccionOrigen.text.toString().trim() == datosOriginales.direccion_origen &&
                txtDireccionDestino.text.toString().trim() == datosOriginales.direccion_destino &&
                txtIdCodigoPostalOrigen.text.toString().trim() == datosOriginales.id_codigo_postal_origen &&
                txtIdCodigoPostalDestino.text.toString().trim() == datosOriginales.id_codigo_postal_destino &&
                txtDistanciaKm.text.toString().trim() == datosOriginales.distancia_km &&
                txtFechaHoraReserva.text.toString().trim() == datosOriginales.fecha_hora_reserva &&
                txtFechaHoraOrigen.text.toString().trim() == datosOriginales.fecha_hora_origen &&
                txtFechaHoraDestino.text.toString().trim() == datosOriginales.fecha_hora_destino &&
                txtIdConductor.text.toString().trim() == datosOriginales.id_conductor &&
                txtIdTipoServicio.text.toString().toIntOrNull() == datosOriginales.id_tipo_servicio &&
                txtIdCliente.text.toString().toIntOrNull() == datosOriginales.id_cliente &&
                txtIdEstadoServicio.text.toString().toIntOrNull() == datosOriginales.id_estado_servicio &&
                txtIdCategoriaServicio.text.toString().toIntOrNull() == datosOriginales.id_categoria_servicio &&
                txtIdMetodoPago.text.toString().toIntOrNull() == datosOriginales.id_metodo_pago)
    }

    override fun onBackPressed() {
        mostrarDialogoDescartar()
    }
}