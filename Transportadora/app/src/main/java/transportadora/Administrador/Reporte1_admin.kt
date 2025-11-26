package transportadora.Administrador

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Reporte1_admin : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvMensajeVacio: TextView
    private lateinit var adapter: ReporteAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte1_admin)
        initViews()
        setupRecyclerView()
        cargarDatosReporte()

        val txtVolver = findViewById<TextView>(R.id.txt_volver)
        txtVolver.setOnClickListener { finish() }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvReporte)
        progressBar = findViewById(R.id.progressBar)
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio)
    }

    private fun setupRecyclerView() {
        adapter = ReporteAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun cargarDatosReporte() {
        mostrarCarga(true)

        val jsonObject = JSONObject()
        // No se necesitan parámetros para este reporte

        val url = ApiConfig.BASE_URL + "consultas/administrador/reportes/consultar_reporte1.php"
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                mostrarCarga(false)

                if (response.getString("success") == "1") {
                    val datosArray = response.getJSONArray("datos")
                    val listaReporte = mutableListOf<DatoReporte>()

                    for (i in 0 until datosArray.length()) {
                        val item = datosArray.getJSONObject(i)
                        listaReporte.add(
                            DatoReporte(
                                tipoServicio = item.getString("tipo_servicio"),
                                categoriaServicio = item.getString("categoria_servicio"),
                                cantidadServicios = item.getString("cantidad_servicios"),
                                valorTotal = item.getString("valor_total")
                            )
                        )
                    }

                    if (listaReporte.isNotEmpty()) {
                        adapter.actualizarDatos(listaReporte)
                        recyclerView.visibility = View.VISIBLE
                        tvMensajeVacio.visibility = View.GONE
                    } else {
                        mostrarMensajeVacio()
                    }
                } else {
                    mostrarMensajeVacio()
                }
            },
            { error ->
                mostrarCarga(false)
                mostrarMensajeVacio()
                // Aquí podrías mostrar un Toast con el error
                // Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarCarga(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        recyclerView.visibility = if (mostrar) View.GONE else View.VISIBLE
    }

    private fun mostrarMensajeVacio() {
        recyclerView.visibility = View.GONE
        tvMensajeVacio.visibility = View.VISIBLE
    }

    // Data class para los datos del reporte
    data class DatoReporte(
        val tipoServicio: String,
        val categoriaServicio: String,
        val cantidadServicios: String,
        val valorTotal: String
    )

    // Adapter para el RecyclerView
    inner class ReporteAdapter(private var datos: List<DatoReporte>) :
        RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder>() {

        inner class ReporteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTipoServicio: TextView = itemView.findViewById(R.id.tvTipoServicio)
            val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
            val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
            val tvValorTotal: TextView = itemView.findViewById(R.id.tvValorTotal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reporte1, parent, false)
            return ReporteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
            val dato = datos[position]

            holder.tvTipoServicio.text = dato.tipoServicio
            holder.tvCategoria.text = dato.categoriaServicio
            holder.tvCantidad.text = dato.cantidadServicios
            holder.tvValorTotal.text = "$${dato.valorTotal}"
        }

        override fun getItemCount(): Int = datos.size

        fun actualizarDatos(nuevosDatos: List<DatoReporte>) {
            datos = nuevosDatos
            notifyDataSetChanged()
        }
    }
}