package transportadora.Administrador.Estados_servicio

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

class Administrar_estados_servicio : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstadoServicioAdapter
    private val estadosServicioList = mutableListOf<EstadoServicio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_estados_servicio)
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
            val intent = Intent(this, Crear_estados_servicio::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarEstadosServicio()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EstadoServicioAdapter(estadosServicioList,
            onEditarClick = { estadoServicio ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_estados_servicio::class.java).apply {
                    putExtra("id_estado_servicio", estadoServicio.id)
                    putExtra("descripcion", estadoServicio.descripcion)
                }
                startActivity(intent)
            },
            onEliminarClick = { estadoServicio ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(estadoServicio)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarEstadosServicio() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_servicio/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_servicio", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        estadosServicioList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            estadosServicioList.add(
                                EstadoServicio(
                                    id = item.getInt("id_estado_servicio"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (estadosServicioList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay estados de servicio registrados"
                        }
                    } else {
                        Log.e("Administrar_estados_servicio", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_servicio", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_estados_servicio", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar estados de servicio"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarEstadosServicio()
    }

    // Data class para los estados de servicio
    data class EstadoServicio(
        val id: Int,
        val descripcion: String
    )

    // Adapter para el RecyclerView
    inner class EstadoServicioAdapter(
        private var estadosServicio: List<EstadoServicio>,
        private val onEditarClick: (EstadoServicio) -> Unit,
        private val onEliminarClick: (EstadoServicio) -> Unit
    ) : RecyclerView.Adapter<EstadoServicioAdapter.EstadoServicioViewHolder>() {

        inner class EstadoServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdEstadoServicio: TextView = itemView.findViewById(R.id.tvIdEstadoServicio)
            val tvDescripcionEstadoServicio: TextView = itemView.findViewById(R.id.tvDescripcionEstadoServicio)
            val btnEditarEstadoServicio: Button = itemView.findViewById(R.id.btnEditarEstadoServicio)
            val btnEliminarEstadoServicio: Button = itemView.findViewById(R.id.btnEliminarEstadoServicio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstadoServicioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_estado_servicio, parent, false)
            return EstadoServicioViewHolder(view)
        }

        override fun onBindViewHolder(holder: EstadoServicioViewHolder, position: Int) {
            val estadoServicio = estadosServicio[position]

            holder.tvIdEstadoServicio.text = estadoServicio.id.toString()
            holder.tvDescripcionEstadoServicio.text = estadoServicio.descripcion

            holder.btnEditarEstadoServicio.setOnClickListener {
                onEditarClick(estadoServicio)
            }

            holder.btnEliminarEstadoServicio.setOnClickListener {
                onEliminarClick(estadoServicio)
            }
        }

        override fun getItemCount(): Int = estadosServicio.size

        fun actualizarLista(nuevaLista: List<EstadoServicio>) {
            estadosServicio = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(estadoServicio: EstadoServicio) {
        // Primero verificamos dependencias
        verificarDependencias(estadoServicio.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(estadoServicio)
            }
        }
    }

    private fun verificarDependencias(idEstadoServicio: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_servicio/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_servicio", idEstadoServicio)
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
                    "rutas" -> mensajeDetallado.append("\n• Rutas asociadas: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(estadoServicio: EstadoServicio) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el estado de servicio '${estadoServicio.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarEstadoServicio(estadoServicio.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarEstadoServicio(idEstadoServicio: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_servicio/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_servicio", idEstadoServicio)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_servicio", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarEstadosServicio()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Estado de servicio eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_estados_servicio", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar estado de servicio: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_servicio", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_estados_servicio", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}