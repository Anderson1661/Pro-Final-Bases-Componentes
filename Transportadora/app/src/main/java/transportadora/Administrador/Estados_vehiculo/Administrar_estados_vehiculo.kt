package transportadora.Administrador.Estados_vehiculo

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

class Administrar_estados_vehiculo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstadoVehiculoAdapter
    private val estadosVehiculoList = mutableListOf<EstadoVehiculo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_estados_vehiculo)
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
            val intent = Intent(this, Crear_estados_vehiculo::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarEstadosVehiculo()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EstadoVehiculoAdapter(estadosVehiculoList,
            onEditarClick = { estadoVehiculo ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_estados_vehiculo::class.java).apply {
                    putExtra("id_estado_vehiculo", estadoVehiculo.id)
                    putExtra("descripcion", estadoVehiculo.descripcion)
                }
                startActivity(intent)
            },
            onEliminarClick = { estadoVehiculo ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(estadoVehiculo)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarEstadosVehiculo() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_vehiculo/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        estadosVehiculoList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            estadosVehiculoList.add(
                                EstadoVehiculo(
                                    id = item.getInt("id_estado_vehiculo"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (estadosVehiculoList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay estados de vehículo registrados"
                        }
                    } else {
                        Log.e("Administrar_estados_vehiculo", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_vehiculo", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_estados_vehiculo", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar estados de vehículo"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarEstadosVehiculo()
    }

    // Data class para los estados de vehículo
    data class EstadoVehiculo(
        val id: Int,
        val descripcion: String
    )

    // Adapter para el RecyclerView
    inner class EstadoVehiculoAdapter(
        private var estadosVehiculo: List<EstadoVehiculo>,
        private val onEditarClick: (EstadoVehiculo) -> Unit,
        private val onEliminarClick: (EstadoVehiculo) -> Unit
    ) : RecyclerView.Adapter<EstadoVehiculoAdapter.EstadoVehiculoViewHolder>() {

        inner class EstadoVehiculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdEstadoVehiculo: TextView = itemView.findViewById(R.id.tvIdEstadoVehiculo)
            val tvDescripcionEstadoVehiculo: TextView = itemView.findViewById(R.id.tvDescripcionEstadoVehiculo)
            val btnEditarEstadoVehiculo: Button = itemView.findViewById(R.id.btnEditarEstadoVehiculo)
            val btnEliminarEstadoVehiculo: Button = itemView.findViewById(R.id.btnEliminarEstadoVehiculo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstadoVehiculoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_estado_vehiculo, parent, false)
            return EstadoVehiculoViewHolder(view)
        }

        override fun onBindViewHolder(holder: EstadoVehiculoViewHolder, position: Int) {
            val estadoVehiculo = estadosVehiculo[position]

            holder.tvIdEstadoVehiculo.text = estadoVehiculo.id.toString()
            holder.tvDescripcionEstadoVehiculo.text = estadoVehiculo.descripcion

            holder.btnEditarEstadoVehiculo.setOnClickListener {
                onEditarClick(estadoVehiculo)
            }

            holder.btnEliminarEstadoVehiculo.setOnClickListener {
                onEliminarClick(estadoVehiculo)
            }
        }

        override fun getItemCount(): Int = estadosVehiculo.size

        fun actualizarLista(nuevaLista: List<EstadoVehiculo>) {
            estadosVehiculo = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(estadoVehiculo: EstadoVehiculo) {
        // Primero verificamos dependencias
        verificarDependencias(estadoVehiculo.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(estadoVehiculo)
            }
        }
    }

    private fun verificarDependencias(idEstadoVehiculo: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_vehiculo/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("VerificarDependencias", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        // No hay dependencias, se puede eliminar
                        callback(false, response.getString("mensaje"), null)
                    } else {
                        // Hay dependencias
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
                    Log.e("VerificarDependencias", "Error parsing JSON: ${e.message}")
                    callback(true, "Error al verificar dependencias", null)
                }
            },
            { error ->
                Log.e("VerificarDependencias", "Volley error: ${error.message}")
                callback(true, "Error de conexión", null)
            }
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
                    "vehiculos" -> mensajeDetallado.append("\n• Vehículos asociados: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(estadoVehiculo: EstadoVehiculo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el estado de vehículo '${estadoVehiculo.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarEstadoVehiculo(estadoVehiculo.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarEstadoVehiculo(idEstadoVehiculo: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_vehiculo/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_vehiculo", idEstadoVehiculo)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_vehiculo", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarEstadosVehiculo()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Estado de vehículo eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_estados_vehiculo", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar estado de vehículo: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_vehiculo", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_estados_vehiculo", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}