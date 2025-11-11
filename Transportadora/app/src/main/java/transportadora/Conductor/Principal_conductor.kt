package transportadora.Conductor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Conductor.Servicios_conductor_almacenados
import transportadora.Login.R

class Principal_conductor : AppCompatActivity() {
    private lateinit var serviciosAdapter: ServiciosAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_conductor)
        // 1. Configurar RecyclerView
        val recyclerServicios = findViewById<RecyclerView>(R.id.recyclerServicios_hoy)
        recyclerServicios.layoutManager = LinearLayoutManager(this)
        serviciosAdapter = ServiciosAdapter(emptyList())
        recyclerServicios.adapter = serviciosAdapter

        // 2. Cargar Datos
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val servicios = withContext(Dispatchers.IO) {
                        Servicios_conductor_almacenados.obtenerServiciosConductor(userEmail)
                    }
                    if (servicios.isNotEmpty()) {
                        serviciosAdapter.updateData(servicios)
                    } else {
                        Toast.makeText(this@Principal_conductor, "No tienes servicios asignados para hoy.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Principal_conductor, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}