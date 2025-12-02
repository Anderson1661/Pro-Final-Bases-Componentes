package transportadora.Administrador.Estados_conductor

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

class Administrar_estados_conductor : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstadoConductorAdapter
    private val estadosConductorList = mutableListOf<EstadoConductor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_estados_conductor)
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
            val intent = Intent(this, Crear_estados_conductor::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarEstadosConductor()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EstadoConductorAdapter(estadosConductorList,
            onEditarClick = { estadoConductor ->
                // Manejar clic en editar: Solo se necesita pasar el ID
                val intent = Intent(this, Editar_estados_conductor::class.java).apply {
                    putExtra("id_estado_conductor", estadoConductor.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { estadoConductor ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(estadoConductor)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarEstadosConductor() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_conductor", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        estadosConductorList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            estadosConductorList.add(
                                EstadoConductor(
                                    id = item.getInt("id_estado_conductor"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (estadosConductorList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay estados de conductor registrados"
                        }
                    } else {
                        Log.e("Administrar_estados_conductor", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_conductor", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_estados_conductor", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar estados de conductor"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarEstadosConductor()
    }

    // Data class para los estados de conductor
    data class EstadoConductor(
        val id: Int,
        val descripcion: String
    )

    // Adapter para el RecyclerView
    inner class EstadoConductorAdapter(
        private var estadosConductor: List<EstadoConductor>,
        private val onEditarClick: (EstadoConductor) -> Unit,
        private val onEliminarClick: (EstadoConductor) -> Unit
    ) : RecyclerView.Adapter<EstadoConductorAdapter.EstadoConductorViewHolder>() {

        inner class EstadoConductorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdEstadoConductor: TextView = itemView.findViewById(R.id.tvIdEstadoConductor)
            val tvDescripcionEstadoConductor: TextView = itemView.findViewById(R.id.tvDescripcionEstadoConductor)
            val btnEditarEstadoConductor: Button = itemView.findViewById(R.id.btnEditarEstadoConductor)
            val btnEliminarEstadoConductor: Button = itemView.findViewById(R.id.btnEliminarEstadoConductor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstadoConductorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_estado_conductor, parent, false)
            return EstadoConductorViewHolder(view)
        }

        override fun onBindViewHolder(holder: EstadoConductorViewHolder, position: Int) {
            val estadoConductor = estadosConductor[position]

            holder.tvIdEstadoConductor.text = estadoConductor.id.toString()
            holder.tvDescripcionEstadoConductor.text = estadoConductor.descripcion

            holder.btnEditarEstadoConductor.setOnClickListener {
                onEditarClick(estadoConductor)
            }

            holder.btnEliminarEstadoConductor.setOnClickListener {
                onEliminarClick(estadoConductor)
            }
        }

        override fun getItemCount(): Int = estadosConductor.size

        fun actualizarLista(nuevaLista: List<EstadoConductor>) {
            estadosConductor = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(estadoConductor: EstadoConductor) {
        // Primero verificamos dependencias
        verificarDependencias(estadoConductor.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(estadoConductor)
            }
        }
    }

    private fun verificarDependencias(idEstadoConductor: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_conductor", idEstadoConductor)
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
                    "conductores" -> mensajeDetallado.append("\n• Conductores asociados: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(estadoConductor: EstadoConductor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el estado de conductor '${estadoConductor.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarEstadoConductor(estadoConductor.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarEstadoConductor(idEstadoConductor: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_estado_conductor", idEstadoConductor)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_estados_conductor", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarEstadosConductor()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Estado de conductor eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_estados_conductor", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar estado de conductor: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_estados_conductor", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_estados_conductor", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}