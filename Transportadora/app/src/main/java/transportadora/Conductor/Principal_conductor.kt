package transportadora.Conductor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Principal_conductor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServicioConductorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_conductor)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // El ID del conductor debería pasarse desde la actividad de Login.
        // Usaremos un valor fijo (ej: 1) para esta implementación.
        val idConductor = intent.getIntExtra("id_usuario", 1)

        fetchServicios(idConductor)
    }

    private fun fetchServicios(idConductor: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "${ApiConfig.BASE_URL}consultas/servicios_conductor.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    if (success) {
                        val serviciosArray = jsonResponse.getJSONArray("servicios")
                        adapter = ServicioConductorAdapter(serviciosArray)
                        recyclerView.adapter = adapter
                    } else {
                        val message = jsonResponse.getString("message")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FetchServicios", "Error parsing JSON", e)
                }
            },
            { error ->
                Toast.makeText(this, "Error de red: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("FetchServicios", "Volley error", error)
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_conductor"] = idConductor.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }
}
