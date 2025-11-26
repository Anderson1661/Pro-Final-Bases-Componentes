package transportadora.Administrador

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import java.text.SimpleDateFormat
import java.util.*

class Reporte3_admin : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var tvMensajeVacio: TextView
    private lateinit var adapter: ReporteAdapter
    private lateinit var etFechaDesde: EditText
    private lateinit var etFechaHasta: EditText
    private lateinit var btnFiltrar: Button

    private var fechaDesdeSeleccionada: Calendar = Calendar.getInstance()
    private var fechaHastaSeleccionada: Calendar = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reporte3_admin)
        initViews()
        setupRecyclerView()
        setupDatePickers()
        setupFiltrarButton()

        val txtVolver = findViewById<TextView>(R.id.txt_volver)
        txtVolver.setOnClickListener { finish() }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rvReporte)
        progressBar = findViewById(R.id.progressBar)
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio)
        etFechaDesde = findViewById(R.id.etFechaDesde)
        etFechaHasta = findViewById(R.id.etFechaHasta)
        btnFiltrar = findViewById(R.id.btnFiltrar)
    }

    private fun setupRecyclerView() {
        adapter = ReporteAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupDatePickers() {
        // Configurar DatePicker para Fecha Desde
        etFechaDesde.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val timePicker = TimePickerDialog(this, { _, hour, minute ->
                    fechaDesdeSeleccionada.set(year, month, day, hour, minute, 0)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    etFechaDesde.setText(sdf.format(fechaDesdeSeleccionada.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
                timePicker.setTitle("Selecciona la hora inicial")
                timePicker.show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.setTitle("Selecciona la fecha inicial")
            datePicker.show()
        }

        // Configurar DatePicker para Fecha Hasta
        etFechaHasta.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val timePicker = TimePickerDialog(this, { _, hour, minute ->
                    fechaHastaSeleccionada.set(year, month, day, hour, minute, 0)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    etFechaHasta.setText(sdf.format(fechaHastaSeleccionada.time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
                timePicker.setTitle("Selecciona la hora final")
                timePicker.show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.setTitle("Selecciona la fecha final")
            datePicker.show()
        }
    }

    private fun setupFiltrarButton() {
        btnFiltrar.setOnClickListener {
            val fechaDesde = etFechaDesde.text.toString().trim()
            val fechaHasta = etFechaHasta.text.toString().trim()

            if (fechaDesde.isEmpty() || fechaHasta.isEmpty()) {
                Toast.makeText(this, "Por favor selecciona ambas fechas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que fecha hasta sea mayor o igual que fecha desde
            if (fechaHastaSeleccionada.before(fechaDesdeSeleccionada)) {
                Toast.makeText(this, "La fecha final debe ser mayor o igual a la fecha inicial", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cargarDatosReporte(fechaDesde, fechaHasta)
        }
    }

    private fun cargarDatosReporte(fechaDesde: String? = null, fechaHasta: String? = null) {
        mostrarCarga(true)

        val jsonObject = JSONObject().apply {
            if (fechaDesde != null && fechaHasta != null) {
                put("fecha_desde", fechaDesde)
                put("fecha_hasta", fechaHasta)
            }
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/reportes/consultar_reporte3.php"
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
                                idCliente = item.getString("id_cliente"),
                                nombre = item.getString("nombre"),
                                correo = item.getString("correo"),
                                cantidadServicios = item.getString("cantidad_servicios"),
                                valorTotal = item.getString("valor_total")
                            )
                        )
                    }

                    if (listaReporte.isNotEmpty()) {
                        adapter.actualizarDatos(listaReporte)
                        recyclerView.visibility = View.VISIBLE
                        tvMensajeVacio.visibility = View.GONE

                        // Mostrar información de filtros aplicados
                        if (fechaDesde != null && fechaHasta != null) {
                            val mensajeFiltro = "Mostrando datos del $fechaDesde al $fechaHasta"
                            Toast.makeText(this, mensajeFiltro, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        mostrarMensajeVacio()
                    }
                } else {
                    mostrarMensajeVacio()
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                mostrarCarga(false)
                mostrarMensajeVacio()
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
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
        val idCliente: String,
        val nombre: String,
        val correo: String,
        val cantidadServicios: String,
        val valorTotal: String
    )

    // Adapter para el RecyclerView
    inner class ReporteAdapter(private var datos: List<DatoReporte>) :
        RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder>() {

        inner class ReporteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdCliente: TextView = itemView.findViewById(R.id.tvIdCliente)
            val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
            val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
            val tvCantidadServicios: TextView = itemView.findViewById(R.id.tvCantidadServicios)
            val tvValorTotal: TextView = itemView.findViewById(R.id.tvValorTotal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reporte3, parent, false)
            return ReporteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
            val dato = datos[position]

            holder.tvIdCliente.text = dato.idCliente
            holder.tvNombre.text = dato.nombre
            holder.tvCorreo.text = dato.correo
            holder.tvCantidadServicios.text = dato.cantidadServicios
            holder.tvValorTotal.text = "$${dato.valorTotal}"
        }

        override fun getItemCount(): Int = datos.size

        fun actualizarDatos(nuevosDatos: List<DatoReporte>) {
            datos = nuevosDatos
            notifyDataSetChanged()
        }
    }
}