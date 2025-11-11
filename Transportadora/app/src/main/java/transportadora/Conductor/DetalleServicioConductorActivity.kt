package transportadora.Conductor

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class DetalleServicioConductorActivity : AppCompatActivity() {

    private lateinit var textViewEstado: TextView
    private var idServicio: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_servicio_conductor)

        textViewEstado = findViewById(R.id.detail_estado)
        val buttonIniciar = findViewById<Button>(R.id.buttonIniciarViaje)
        val buttonFinalizar = findViewById<Button>(R.id.buttonFinalizarViaje)

        val servicioJsonString = intent.getStringExtra("servicio_json")

        if (servicioJsonString == null) {
            Toast.makeText(this, "No se pudo cargar la información del servicio.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            val servicio = JSONObject(servicioJsonString)
            idServicio = servicio.getInt("id_servicio")

            findViewById<TextView>(R.id.detail_id_servicio).text = "Servicio #: $idServicio"
            findViewById<TextView>(R.id.detail_fecha).text = "Fecha: ${servicio.getString("fecha")}"
            findViewById<TextView>(R.id.detail_origen).text = "Origen: ${servicio.getString("origen")}"
            findViewById<TextView>(R.id.detail_destino).text = "Destino: ${servicio.getString("destino")}"
            textViewEstado.text = "Estado: ${servicio.getString("estado")}"

        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar los datos del servicio.", Toast.LENGTH_LONG).show()
            finish()
        }

        buttonIniciar.setOnClickListener {
            actualizarEstado(idServicio, "En Camino")
        }

        buttonFinalizar.setOnClickListener {
            actualizarEstado(idServicio, "Entregado")
        }
    }

    private fun actualizarEstado(idServicio: Int, nuevoEstado: String) {
        if (idServicio == -1) {
            Toast.makeText(this, "ID de servicio no válido.", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(this)
        val url = "${ApiConfig.BASE_URL}consultas/actualizar_estado_servicio.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    if (success) {
                        textViewEstado.text = "Estado: $nuevoEstado"
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta.", Toast.LENGTH_LONG).show()
                    Log.e("ActualizarEstado", "Error parsing JSON", e)
                }
            },
            { error ->
                Toast.makeText(this, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("ActualizarEstado", "Volley error", error)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_servicio"] = idServicio.toString()
                params["nuevo_estado"] = nuevoEstado
                return params
            }
        }
        queue.add(stringRequest)
    }
}
