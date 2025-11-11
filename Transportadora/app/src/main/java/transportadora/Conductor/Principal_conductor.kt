package transportadora.Conductor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_conductor)

        recyclerView = findViewById(R.id.recyclerViewServicios)
        progressBar = findViewById(R.id.progressBar)
        textViewStatus = findViewById(R.id.textViewStatus)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val idConductor = intent.getIntExtra("id_usuario", -1)

        if (idConductor == -1) {
            Toast.makeText(this, "Error: ID de conductor no válido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchServicios(idConductor)
    }

    private fun fetchServicios(idConductor: Int) {
        showLoading("Cargando servicios...")

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
                        if (serviciosArray.length() > 0) {
                            adapter = ServicioConductorAdapter(serviciosArray)
                            recyclerView.adapter = adapter
                            showData()
                        } else {
                            showStatus("No tiene servicios asignados.")
                        }
                    } else {
                        val message = jsonResponse.getString("message")
                        showStatus("Error: $message")
                    }
                } catch (e: Exception) {
                    showStatus("Error al procesar la respuesta.")
                    Log.e("FetchServicios", "Error parsing JSON", e)
                }
            },
            { error ->
                showStatus("Error de red. Verifique su conexión.")
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

    private fun showLoading(message: String) {
        progressBar.visibility = View.VISIBLE
        textViewStatus.text = message
        textViewStatus.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showData() {
        progressBar.visibility = View.GONE
        textViewStatus.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showStatus(message: String) {
        progressBar.visibility = View.GONE
        textViewStatus.text = message
        textViewStatus.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
}
