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
import transportadora.Almacenados.Historial_servicio_almacenados
import transportadora.Login.R

class Historial_serv_cliente : AppCompatActivity() {

    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_serv_cliente)

        // 1. Configurar RecyclerView
        val recyclerHistorial = findViewById<RecyclerView>(R.id.recyclerHistorial)
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        historialAdapter = HistorialAdapter(emptyList())
        recyclerHistorial.adapter = historialAdapter

        // 2. Cargar Datos
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val historial = withContext(Dispatchers.IO) {
                        Historial_servicio_almacenados.obtenerHistorial(userEmail)
                    }
                    if (historial.isNotEmpty()) {
                        historialAdapter.updateData(historial)
                    } else {
                        Toast.makeText(this@Historial_serv_cliente, "No tienes servicios en tu historial.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Historial_serv_cliente, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            finish()
        }


        //Menu lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
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
            startActivity(intent)
        }
        val txtmenu2 = findViewById<TextView>(R.id.menu2)
        txtmenu2.setOnClickListener {
            val intent = Intent(this, Seguimiento_serv_cliente::class.java)
            startActivity(intent)
        }
        val txtmenu3 = findViewById<TextView>(R.id.menu3)
        txtmenu3.setOnClickListener {
            scrollView.post {
                scrollView.smoothScrollTo(0, 0)
            }
        }
    }
}