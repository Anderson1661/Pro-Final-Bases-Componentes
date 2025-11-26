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
import java.text.SimpleDateFormat
import java.util.Locale

class Reporte6_admin : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvMensajeVacio: TextView
    private lateinit var adapter: ReporteAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte6_admin)
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

        val url = ApiConfig.BASE_URL + "consultas/administrador/reportes/consultar_reporte6.php"
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
                                anioMes = item.getString("anio_mes"),
                                totalIngresos = item.getString("total_ingresos"),
                                totalServicios = item.getString("total_servicios"),
                                promedioPorServicio = item.getString("promedio_por_servicio"),
                                ingresosConductor = item.getString("ingresos_conductor"),
                                ingresosEmpresa = item.getString("ingresos_empresa"),
                                ultimaActualizacion = formatearFecha(item.getString("ultima_actualizacion"))
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

    private fun formatearFecha(fechaOriginal: String): String {
        return try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaOriginal)
            formatoSalida.format(fecha)
        } catch (e: Exception) {
            fechaOriginal
        }
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
        val anioMes: String,
        val totalIngresos: String,
        val totalServicios: String,
        val promedioPorServicio: String,
        val ingresosConductor: String,
        val ingresosEmpresa: String,
        val ultimaActualizacion: String
    )

    // Adapter para el RecyclerView
    inner class ReporteAdapter(private var datos: List<DatoReporte>) :
        RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder>() {

        inner class ReporteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvPeriodo: TextView = itemView.findViewById(R.id.tvPeriodo)
            val tvTotalIngresos: TextView = itemView.findViewById(R.id.tvTotalIngresos)
            val tvTotalServicios: TextView = itemView.findViewById(R.id.tvTotalServicios)
            val tvPromedioServicio: TextView = itemView.findViewById(R.id.tvPromedioServicio)
            val tvIngresosConductores: TextView = itemView.findViewById(R.id.tvIngresosConductores)
            val tvIngresosEmpresa: TextView = itemView.findViewById(R.id.tvIngresosEmpresa)
            val tvUltimaActualizacion: TextView = itemView.findViewById(R.id.tvUltimaActualizacion)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reporte6, parent, false)
            return ReporteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
            val dato = datos[position]

            holder.tvPeriodo.text = dato.anioMes
            holder.tvTotalIngresos.text = "$${dato.totalIngresos}"
            holder.tvTotalServicios.text = dato.totalServicios
            holder.tvPromedioServicio.text = "$${dato.promedioPorServicio}"
            holder.tvIngresosConductores.text = "$${dato.ingresosConductor}"
            holder.tvIngresosEmpresa.text = "$${dato.ingresosEmpresa}"
            holder.tvUltimaActualizacion.text = dato.ultimaActualizacion
        }

        override fun getItemCount(): Int = datos.size

        fun actualizarDatos(nuevosDatos: List<DatoReporte>) {
            datos = nuevosDatos
            notifyDataSetChanged()
        }
    }
}