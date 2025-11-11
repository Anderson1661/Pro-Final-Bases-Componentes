package transportadora.Conductor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import transportadora.Conductor.ServiciosAdapter
import transportadora.Login.R

class Principal_conductor : AppCompatActivity() {
    private lateinit var serviciosAdapter: ServiciosAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_conductor)
        // 1. Configurar RecyclerView
        val recyclerHistorial = findViewById<RecyclerView>(R.id.recyclerServicios_hoy)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        serviciosAdapter = ServiciosAdapter(emptyList())
        recyclerHistorial.adapter = serviciosAdapter
    }
}