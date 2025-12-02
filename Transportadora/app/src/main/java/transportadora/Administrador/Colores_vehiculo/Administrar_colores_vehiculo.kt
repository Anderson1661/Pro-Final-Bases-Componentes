package transportadora.Administrador.Colores_vehiculo

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

class Administrar_colores_vehiculo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ColorVehiculoAdapter
    private val coloresList = mutableListOf<ColorVehiculo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_colores_vehiculo)
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
            val intent = Intent(this, Crear_colores_vehiculo::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarColores()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ColorVehiculoAdapter(coloresList,
            onEditarClick = { color ->
                // Pasar solo el ID, los demás datos se cargarán desde el servidor
                val intent = Intent(this, Editar_colores_vehiculo::class.java).apply {
                    putExtra("id_color", color.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { color ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(color)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarColores() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/color_vehiculo/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_colores_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        coloresList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            coloresList.add(
                                ColorVehiculo(
                                    id = item.getInt("id_color"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (coloresList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay colores registrados"
                        }
                    } else {
                        Log.e("Administrar_colores_vehiculo", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_colores_vehiculo", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_colores_vehiculo", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar colores"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarColores()
    }

    // Data class para los colores de vehículos
    data class ColorVehiculo(
        val id: Int,
        val descripcion: String
    )

    // Adapter para el RecyclerView
    inner class ColorVehiculoAdapter(
        private var colores: List<ColorVehiculo>,
        private val onEditarClick: (ColorVehiculo) -> Unit,
        private val onEliminarClick: (ColorVehiculo) -> Unit
    ) : RecyclerView.Adapter<ColorVehiculoAdapter.ColorVehiculoViewHolder>() {

        inner class ColorVehiculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdColorVehiculo: TextView = itemView.findViewById(R.id.tvIdColorVehiculo)
            val tvDescripcionColorVehiculo: TextView = itemView.findViewById(R.id.tvDescripcionColorVehiculo)
            val btnEditarColorVehiculo: Button = itemView.findViewById(R.id.btnEditarColorVehiculo)
            val btnEliminarColorVehiculo: Button = itemView.findViewById(R.id.btnEliminarColorVehiculo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorVehiculoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_color_vehiculo, parent, false)
            return ColorVehiculoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ColorVehiculoViewHolder, position: Int) {
            val color = colores[position]

            holder.tvIdColorVehiculo.text = color.id.toString()
            holder.tvDescripcionColorVehiculo.text = color.descripcion

            holder.btnEditarColorVehiculo.setOnClickListener {
                onEditarClick(color)
            }

            holder.btnEliminarColorVehiculo.setOnClickListener {
                onEliminarClick(color)
            }
        }

        override fun getItemCount(): Int = colores.size

        fun actualizarLista(nuevaLista: List<ColorVehiculo>) {
            colores = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(color: ColorVehiculo) {
        // Primero verificamos dependencias
        verificarDependencias(color.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(color)
            }
        }
    }

    private fun verificarDependencias(idColor: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/color_vehiculo/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_color", idColor)
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
                    "vehiculos" -> mensajeDetallado.append("\n• Vehículos: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(color: ColorVehiculo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el color '${color.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarColor(color.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarColor(idColor: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/color_vehiculo/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_color", idColor)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_colores_vehiculo", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarColores()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Color eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_colores_vehiculo", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar color: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_colores_vehiculo", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_colores_vehiculo", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}