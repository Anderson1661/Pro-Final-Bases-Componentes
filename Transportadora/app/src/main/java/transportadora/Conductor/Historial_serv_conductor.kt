package transportadora.Conductor

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Conductor.Pagos_almacenados
import transportadora.Login.R

class Historial_serv_conductor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PagosAdapter
    private lateinit var txtTotalRecibido: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_serv_conductor)

        recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PagosAdapter(emptyList())
        recyclerView.adapter = adapter

        txtTotalRecibido = findViewById(R.id.txt_total_recibido)

        // Suponiendo que el correo viene del Intent
        val correo = intent.getStringExtra("USER_EMAIL") ?: return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pagos = withContext(Dispatchers.IO) {
                    Pagos_almacenados.obtenerPagos(correo)
                }

                if (pagos.isNotEmpty()) {
                    adapter.updateData(pagos)
                    recyclerView.adapter = adapter

                    val totalRecibido = pagos.sumOf { it.pago_conductor }
                    txtTotalRecibido.text = "$${String.format("%,.0f", totalRecibido)}"
                } else {
                    Toast.makeText(
                        this@Historial_serv_conductor,
                        "No hay pagos registrados aún.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@Historial_serv_conductor,
                    "Error al cargar pagos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
