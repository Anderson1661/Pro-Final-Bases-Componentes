package transportadora.Cliente

import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Cliente.Historial_servicio_almacenados
import transportadora.Login.R
// Importaciones necesarias para la lógica de red. Debes usar las tuyas.
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Historial_serv_cliente : AppCompatActivity() {

    private lateinit var historialAdapter: HistorialAdapter
    // 1. Convertir userEmail en una propiedad de la clase
    private var userEmail: String? = null
    private val client = OkHttpClient() // Cliente OkHttp para las peticiones de red

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_serv_cliente)

        // Capturar el email
        userEmail = intent.getStringExtra("USER_EMAIL")

        // 1. Configurar RecyclerView
        val recyclerHistorial = findViewById<RecyclerView>(R.id.recyclerHistorial)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)

        // 3. Inicializar Adapter con el listener de cancelación
        historialAdapter = HistorialAdapter(
            emptyList(),
            onCancelClick = { idRuta, metodoPago -> cancelarServicio(idRuta, metodoPago) },
            onDetailsClick = { idRuta -> verDetalles(idRuta) }
        )
        recyclerHistorial.adapter = historialAdapter

        // 2. Cargar Datos
        cargarHistorial()

        // El resto del código de menú lateral e inferior permanece igual
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        val txtcambiarcontra = findViewById<TextView>(R.id.cambiocontra)
        txtcambiarcontra.setOnClickListener {
            val intent = Intent(this@Historial_serv_cliente, transportadora.Compartido.Preg_seguridad::class.java)
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent = Intent(this@Historial_serv_cliente, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent = Intent(this@Historial_serv_cliente, transportadora.Compartido.Ayuda::class.java)
            startActivity(intent)
        }

        //menu inferior
        val txtmenu1 = findViewById<TextView>(R.id.menu1)
        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
        txtmenu1.setOnClickListener {
            val intent = Intent(this, Principal_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        val txtmenu2 = findViewById<TextView>(R.id.menu2)
        txtmenu2.setOnClickListener {
            val intent = Intent(this, Seguimiento_serv_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        val txtmenu3 = findViewById<TextView>(R.id.menu3)
        txtmenu3.setOnClickListener {
            scrollView.post {
                scrollView.smoothScrollTo(0, 0)
            }
        }
    }

    // 4. Implementación de la función de carga
    private fun cargarHistorial() {
        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val historial = withContext(Dispatchers.IO) {
                        Historial_servicio_almacenados.obtenerHistorial(userEmail!!)
                    }
                    if (historial.isNotEmpty()) {
                        historialAdapter.updateData(historial)
                    } else {
                        Toast.makeText(this@Historial_serv_cliente, "No tienes servicios en tu historial.", Toast.LENGTH_LONG).show()
                        historialAdapter.updateData(emptyList()) // Limpiar la lista si no hay datos
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Historial_serv_cliente, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // 5. Implementación de la función de cancelación
    private fun cancelarServicio(idRuta: Int, metodoPago: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val (success, message) = withContext(Dispatchers.IO) {
                llamarAPICancelar(idRuta)
            }

            Toast.makeText(this@Historial_serv_cliente, message, Toast.LENGTH_LONG).show()

            if (success) {
                // LÓGICA DE REEMBOLSO AÑADIDA AQUÍ
                if (metodoPago.lowercase() != "Efectivo") {
                    Toast.makeText(
                        this@Historial_serv_cliente,
                        "El dinero será reembolsado a tu cuenta en las próximas 24 horas.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                cargarHistorial()
            }
        }
    }

    //---------------------------------------------------------
    // FUNCIÓN DE RED PARA CANCELAR SERVICIO (Asumiendo OkHttp)
    //---------------------------------------------------------

    /**
     * Llama al script PHP para actualizar el estado del servicio a Cancelado.
     * @param idRuta El ID del servicio a cancelar.
     * @return Un par (Boolean, String) indicando éxito y el mensaje de la respuesta.
     */
    private fun llamarAPICancelar(idRuta: Int): Pair<Boolean, String> {
        val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/ruta/cancelar_ruta.php")

        val requestBody = FormBody.Builder()
            .add("id_ruta", idRuta.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return Pair(false, "Error de red: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Respuesta desconocida.")

                Pair(success, mensaje)
            }
        } catch (e: IOException) {
            Pair(false, "Error de conexión: ${e.message}")
        } catch (e: Exception) {
            Pair(false, "Error al procesar la respuesta.")
        }
    }

    private fun verDetalles(idRuta: Int) {
        val intent = Intent(this, Seguimiento_serv_cliente::class.java)
        // Pasamos el email (por si el seguimiento lo necesita) y el ID del servicio
        intent.putExtra("USER_EMAIL", userEmail)
        intent.putExtra("ID_SERVICIO_DETALLE", idRuta.toString()) // Enviamos el ID como String
        startActivity(intent)
    }
}