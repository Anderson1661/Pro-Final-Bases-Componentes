package transportadora.Administrador.Vehiculos

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

class Administrar_vehiculos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VehiculoAdapter
    private val vehiculosList = mutableListOf<Vehiculo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_vehiculos)
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
            val intent = Intent(this, Crear_vehiculos::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarVehiculos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VehiculoAdapter(vehiculosList,
            onEditarClick = { vehiculo ->
                val intent = Intent(this, Editar_vehiculos::class.java).apply {
                    putExtra("placa", vehiculo.placa)
                    putExtra("linea_vehiculo", vehiculo.lineaVehiculo)
                    putExtra("modelo", vehiculo.modelo)
                    putExtra("id_color", vehiculo.idColor)
                    putExtra("id_marca", vehiculo.idMarca)
                    putExtra("id_tipo_servicio", vehiculo.idTipoServicio)
                    putExtra("id_estado_vehiculo", vehiculo.idEstadoVehiculo)
                }
                startActivity(intent)
            },
            onEliminarClick = { vehiculo ->
                mostrarDialogoEliminacion(vehiculo)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarVehiculos() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        vehiculosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            vehiculosList.add(
                                Vehiculo(
                                    placa = item.getString("placa"),
                                    lineaVehiculo = item.getString("linea_vehiculo"),
                                    modelo = item.getInt("modelo"),
                                    idColor = item.getInt("id_color"),
                                    idMarca = item.getInt("id_marca"),
                                    idTipoServicio = item.getInt("id_tipo_servicio"),
                                    idEstadoVehiculo = item.getInt("id_estado_vehiculo")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (vehiculosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay vehículos registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_vehiculos", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_vehiculos", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar vehículos"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarVehiculos()
    }

    data class Vehiculo(
        val placa: String,
        val lineaVehiculo: String,
        val modelo: Int,
        val idColor: Int,
        val idMarca: Int,
        val idTipoServicio: Int,
        val idEstadoVehiculo: Int
    )

    inner class VehiculoAdapter(
        private var vehiculos: List<Vehiculo>,
        private val onEditarClick: (Vehiculo) -> Unit,
        private val onEliminarClick: (Vehiculo) -> Unit
    ) : RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder>() {

        inner class VehiculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvPlacaVehiculo: TextView = itemView.findViewById(R.id.tvPlacaVehiculo)
            val tvLineaVehiculo: TextView = itemView.findViewById(R.id.tvLineaVehiculo)
            val tvModeloVehiculo: TextView = itemView.findViewById(R.id.tvModeloVehiculo)
            val tvIdColorVehiculo: TextView = itemView.findViewById(R.id.tvIdColorVehiculo)
            val tvIdMarcaVehiculo: TextView = itemView.findViewById(R.id.tvIdMarcaVehiculo)
            val tvIdTipoServicioVehiculo: TextView = itemView.findViewById(R.id.tvIdTipoServicioVehiculo)
            val tvIdEstadoVehiculo: TextView = itemView.findViewById(R.id.tvIdEstadoVehiculo)
            val btnEditarVehiculo: Button = itemView.findViewById(R.id.btnEditarVehiculo)
            val btnEliminarVehiculo: Button = itemView.findViewById(R.id.btnEliminarVehiculo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_vehiculo, parent, false)
            return VehiculoViewHolder(view)
        }

        override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
            val vehiculo = vehiculos[position]

            holder.tvPlacaVehiculo.text = vehiculo.placa
            holder.tvLineaVehiculo.text = vehiculo.lineaVehiculo
            holder.tvModeloVehiculo.text = vehiculo.modelo.toString()
            holder.tvIdColorVehiculo.text = vehiculo.idColor.toString()
            holder.tvIdMarcaVehiculo.text = vehiculo.idMarca.toString()
            holder.tvIdTipoServicioVehiculo.text = vehiculo.idTipoServicio.toString()
            holder.tvIdEstadoVehiculo.text = vehiculo.idEstadoVehiculo.toString()

            holder.btnEditarVehiculo.setOnClickListener { onEditarClick(vehiculo) }
            holder.btnEliminarVehiculo.setOnClickListener { onEliminarClick(vehiculo) }
        }

        override fun getItemCount(): Int = vehiculos.size
    }

    private fun mostrarDialogoEliminacion(vehiculo: Vehiculo) {
        verificarDependencias(vehiculo.placa) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(vehiculo)
            }
        }
    }

    private fun verificarDependencias(placa: String, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("placa", placa)
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
                    "conductores" -> mensajeDetallado.append("\n• Conductores: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(vehiculo: Vehiculo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el vehículo con placa '${vehiculo.placa}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarVehiculo(vehiculo.placa)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarVehiculo(placa: String) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/vehiculo/delete.php"
        val jsonObject = JSONObject().apply {
            put("placa", placa)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarVehiculos()
                        Toast.makeText(this, "Vehículo eliminado correctamente", Toast.LENGTH_SHORT).show()
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
