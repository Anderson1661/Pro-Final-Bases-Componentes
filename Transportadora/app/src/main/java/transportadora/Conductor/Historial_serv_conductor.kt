package transportadora.Conductor

import android.content.Intent
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
        setContentView(R.layout.activity_historial_pagos_conductor)

        // Recycler del XML
        recyclerView = findViewById(R.id.recyclerPagos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PagosAdapter(emptyList())
        recyclerView.adapter = adapter

        txtTotalRecibido = findViewById(R.id.txt_total_recibido)

        // Obtener correo
        val correo = intent.getStringExtra("USER_EMAIL")
        if (correo == null) {
            Toast.makeText(this, "Error: No se recibió el correo del conductor.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar pagos
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pagos = withContext(Dispatchers.IO) {
                    Pagos_almacenados.obtenerPagos(correo)
                }

                if (pagos.isNotEmpty()) {
                    adapter.updateData(pagos)

                    val totalRecibido = pagos.sumOf { it.pago_conductor }
                    txtTotalRecibido.text = "$${String.format("%,.0f", totalRecibido)}"
                } else {
                    Toast.makeText(this@Historial_serv_conductor, "No hay pagos para mostrar.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Historial_serv_conductor, "Error al cargar pagos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // MENU EVENTOS
        findViewById<TextView>(R.id.menu1).setOnClickListener {
            startActivity(Intent(this, Principal_conductor::class.java))
        }

        findViewById<TextView>(R.id.editarperfil).setOnClickListener {
            startActivity(Intent(this, Perfil_conductor::class.java))
        }

        findViewById<TextView>(R.id.cambiocontra).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Preg_seguridad::class.java))
        }

        findViewById<TextView>(R.id.cerrarsesion).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Main::class.java))
        }

        findViewById<TextView>(R.id.ayuda).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Ayuda::class.java))
        }
    }
}
