package transportadora.Administrador.Servicios

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import transportadora.Administrador.Principal_administrador
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Administrar_servicios : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RutaAdapter
    private val rutasList = mutableListOf<Ruta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_servicios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            val intent = Intent(this, Principal_administrador::class.java)
            startActivity(intent)
            finish()
        }

        val btnCrear = findViewById<Button>(R.id.btnCrear)
        btnCrear.setOnClickListener {
            val intent = Intent(this, Crear_servicios::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarRutas()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RutaAdapter(rutasList,
            onEditarClick = { ruta ->
                val intent = Intent(this, Editar_servicios::class.java).apply {
                    putExtra("id_ruta", ruta.idRuta)
                    putExtra("direccion_origen", ruta.direccionOrigen)
                    putExtra("direccion_destino", ruta.direccionDestino)
                    putExtra("id_codigo_postal_origen", ruta.idCodigoPostalOrigen)
                    putExtra("id_codigo_postal_destino", ruta.idCodigoPostalDestino)
                    putExtra("distancia_km", ruta.distanciaKm)
                    putExtra("fecha_hora_reserva", ruta.fechaHoraReserva)
                    putExtra("fecha_hora_origen", ruta.fechaHoraOrigen ?: "")
                    putExtra("fecha_hora_destino", ruta.fechaHoraDestino ?: "")
                    putExtra("id_conductor", ruta.idConductor ?: 0)
                    putExtra("id_tipo_servicio", ruta.idTipoServicio)
                    putExtra("id_cliente", ruta.idCliente)
                    putExtra("id_estado_servicio", ruta.idEstadoServicio)
                    putExtra("id_categoria_servicio", ruta.idCategoriaServicio)
                    putExtra("id_metodo_pago", ruta.idMetodoPago)
                    putExtra("total", ruta.total ?: 0.0)
                    putExtra("pago_conductor", ruta.pagoConductor ?: 0.0)
                }
                startActivity(intent)
            },
            onEliminarClick = { ruta ->
                mostrarDialogoEliminacion(ruta)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarRutas() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        rutasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            rutasList.add(
                                Ruta(
                                    idRuta = item.getInt("id_ruta"),
                                    direccionOrigen = item.getString("direccion_origen"),
                                    direccionDestino = item.getString("direccion_destino"),
                                    idCodigoPostalOrigen = item.getString("id_codigo_postal_origen"),
                                    idCodigoPostalDestino = item.getString("id_codigo_postal_destino"),
                                    distanciaKm = item.getDouble("distancia_km"),
                                    fechaHoraReserva = item.getString("fecha_hora_reserva"),
                                    fechaHoraOrigen = if (item.isNull("fecha_hora_origen")) null else item.getString("fecha_hora_origen"),
                                    fechaHoraDestino = if (item.isNull("fecha_hora_destino")) null else item.getString("fecha_hora_destino"),
                                    idConductor = if (item.isNull("id_conductor")) null else item.getInt("id_conductor"),
                                    idTipoServicio = item.getInt("id_tipo_servicio"),
                                    idCliente = item.getInt("id_cliente"),
                                    idEstadoServicio = item.getInt("id_estado_servicio"),
                                    idCategoriaServicio = item.getInt("id_categoria_servicio"),
                                    idMetodoPago = item.getInt("id_metodo_pago"),
                                    total = if (item.isNull("total")) null else item.getDouble("total"),
                                    pagoConductor = if (item.isNull("pago_conductor")) null else item.getDouble("pago_conductor")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (rutasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay rutas registradas"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_servicios", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_servicios", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar rutas"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarRutas()
    }

    data class Ruta(
        val idRuta: Int,
        val direccionOrigen: String,
        val direccionDestino: String,
        val idCodigoPostalOrigen: String,
        val idCodigoPostalDestino: String,
        val distanciaKm: Double,
        val fechaHoraReserva: String,
        val fechaHoraOrigen: String?,
        val fechaHoraDestino: String?,
        val idConductor: Int?,
        val idTipoServicio: Int,
        val idCliente: Int,
        val idEstadoServicio: Int,
        val idCategoriaServicio: Int,
        val idMetodoPago: Int,
        val total: Double?,
        val pagoConductor: Double?
    )

    inner class RutaAdapter(
        private var rutas: List<Ruta>,
        private val onEditarClick: (Ruta) -> Unit,
        private val onEliminarClick: (Ruta) -> Unit
    ) : RecyclerView.Adapter<RutaAdapter.RutaViewHolder>() {

        inner class RutaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdRuta: TextView = itemView.findViewById(R.id.tvIdRuta)
            val tvDireccionOrigen: TextView = itemView.findViewById(R.id.tvDireccionOrigen)
            val tvDireccionDestino: TextView = itemView.findViewById(R.id.tvDireccionDestino)
            val tvCodigoPostalOrigen: TextView = itemView.findViewById(R.id.tvCodigoPostalOrigen)
            val tvCodigoPostalDestino: TextView = itemView.findViewById(R.id.tvCodigoPostalDestino)
            val tvDistanciaKm: TextView = itemView.findViewById(R.id.tvDistanciaKm)
            val tvFechaHoraReserva: TextView = itemView.findViewById(R.id.tvFechaHoraReserva)
            val tvFechaHoraOrigen: TextView = itemView.findViewById(R.id.tvFechaHoraOrigen)
            val tvFechaHoraDestino: TextView = itemView.findViewById(R.id.tvFechaHoraDestino)
            val tvIdConductorRuta: TextView = itemView.findViewById(R.id.tvIdConductorRuta)
            val tvIdTipoServicioRuta: TextView = itemView.findViewById(R.id.tvIdTipoServicioRuta)
            val tvIdClienteRuta: TextView = itemView.findViewById(R.id.tvIdClienteRuta)
            val tvIdEstadoServicioRuta: TextView = itemView.findViewById(R.id.tvIdEstadoServicioRuta)
            val tvIdCategoriaServicioRuta: TextView = itemView.findViewById(R.id.tvIdCategoriaServicioRuta)
            val tvIdMetodoPagoRuta: TextView = itemView.findViewById(R.id.tvIdMetodoPagoRuta)
            val tvTotalRuta: TextView = itemView.findViewById(R.id.tvTotalRuta)
            val tvPagoConductorRuta: TextView = itemView.findViewById(R.id.tvPagoConductorRuta)
            val btnEditarRuta: Button = itemView.findViewById(R.id.btnEditarRuta)
            val btnEliminarRuta: Button = itemView.findViewById(R.id.btnEliminarRuta)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ruta, parent, false)
            return RutaViewHolder(view)
        }

        override fun onBindViewHolder(holder: RutaViewHolder, position: Int) {
            val ruta = rutas[position]

            holder.tvIdRuta.text = ruta.idRuta.toString()
            holder.tvDireccionOrigen.text = ruta.direccionOrigen
            holder.tvDireccionDestino.text = ruta.direccionDestino
            holder.tvCodigoPostalOrigen.text = ruta.idCodigoPostalOrigen
            holder.tvCodigoPostalDestino.text = ruta.idCodigoPostalDestino
            holder.tvDistanciaKm.text = ruta.distanciaKm.toString()
            holder.tvFechaHoraReserva.text = ruta.fechaHoraReserva
            holder.tvFechaHoraOrigen.text = ruta.fechaHoraOrigen ?: "N/A"
            holder.tvFechaHoraDestino.text = ruta.fechaHoraDestino ?: "N/A"
            holder.tvIdConductorRuta.text = ruta.idConductor?.toString() ?: "N/A"
            holder.tvIdTipoServicioRuta.text = ruta.idTipoServicio.toString()
            holder.tvIdClienteRuta.text = ruta.idCliente.toString()
            holder.tvIdEstadoServicioRuta.text = ruta.idEstadoServicio.toString()
            holder.tvIdCategoriaServicioRuta.text = ruta.idCategoriaServicio.toString()
            holder.tvIdMetodoPagoRuta.text = ruta.idMetodoPago.toString()
            holder.tvTotalRuta.text = ruta.total?.toString() ?: "0.0"
            holder.tvPagoConductorRuta.text = ruta.pagoConductor?.toString() ?: "0.0"

            holder.btnEditarRuta.setOnClickListener { onEditarClick(ruta) }
            holder.btnEliminarRuta.setOnClickListener { onEliminarClick(ruta) }
        }

        override fun getItemCount(): Int = rutas.size
    }

    private fun mostrarDialogoEliminacion(ruta: Ruta) {
        verificarDependencias(ruta.idRuta) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(ruta)
            }
        }
    }

    private fun verificarDependencias(idRuta: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRuta)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        callback(false, response.getString("mensaje"), null)
                    } else {
                        val dependencias = mutableMapOf<String, Int>()
                        val jsonDependencies = response.getJSONObject("dependencies")
                        val keys = jsonDependencies.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            dependencias[key] = jsonDependencies.getInt(key)
                        }
                        callback(true, response.getString("mensaje"), dependencias)
                    }
                } catch (e: JSONException) {
                    callback(true, "Error al verificar dependencias", null)
                }
            },
            { error -> callback(true, "Error de conexión", null) }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarAlertaDependencias(mensaje: String, dependencias: Map<String, Int>?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No se puede eliminar")

        val mensajeDetallado = StringBuilder(mensaje)
        dependencias?.forEach { (tabla, count) ->
            if (count > 0) {
                when (tabla) {
                    "pasajeros_ruta" -> mensajeDetallado.append("\n• Pasajeros: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(ruta: Ruta) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la ruta #${ruta.idRuta}?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarRuta(ruta.idRuta)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarRuta(idRuta: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/ruta/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRuta)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarRutas()
                        Toast.makeText(this, "Ruta eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al eliminar: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show() }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
