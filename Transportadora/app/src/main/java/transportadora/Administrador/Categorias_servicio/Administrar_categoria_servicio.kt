package transportadora.Administrador.Categorias_servicio

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

class Administrar_categoria_servicio : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoriaServicioAdapter
    private val categoriasList = mutableListOf<CategoriaServicio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_categoria_servicio)
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
            val intent = Intent(this, Crear_categoria_servicio::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarCategorias()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CategoriaServicioAdapter(categoriasList,
            onEditarClick = { categoria ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_categoria_servicio::class.java).apply {
                    putExtra("id_categoria_servicio", categoria.id)
                    putExtra("descripcion", categoria.descripcion)
                    putExtra("valor_km", categoria.valorKm)
                }
                startActivity(intent)
            },
            onEliminarClick = { categoria ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(categoria)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarCategorias() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_categoria_servicio", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        categoriasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            categoriasList.add(
                                CategoriaServicio(
                                    id = item.getInt("id_categoria_servicio"),
                                    descripcion = item.getString("descripcion"),
                                    valorKm = item.getDouble("valor_km")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (categoriasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay categorías registradas"
                        }
                    } else {
                        Log.e("Administrar_categoria_servicio", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_categoria_servicio", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_categoria_servicio", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar categorías"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarCategorias()
    }

    // Data class para las categorías de servicio
    data class CategoriaServicio(
        val id: Int,
        val descripcion: String,
        val valorKm: Double
    )

    // Adapter para el RecyclerView
    inner class CategoriaServicioAdapter(
        private var categorias: List<CategoriaServicio>,
        private val onEditarClick: (CategoriaServicio) -> Unit,
        private val onEliminarClick: (CategoriaServicio) -> Unit
    ) : RecyclerView.Adapter<CategoriaServicioAdapter.CategoriaServicioViewHolder>() {

        inner class CategoriaServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdCategoriaServicio: TextView = itemView.findViewById(R.id.tvIdCategoriaServicio)
            val tvDescripcionCategoriaServicio: TextView = itemView.findViewById(R.id.tvDescripcionCategoriaServicio)
            val tvValorKmCategoriaServicio: TextView = itemView.findViewById(R.id.tvValorKmCategoriaServicio)
            val btnEditarCategoriaServicio: Button = itemView.findViewById(R.id.btnEditarCategoriaServicio)
            val btnEliminarCategoriaServicio: Button = itemView.findViewById(R.id.btnEliminarCategoriaServicio)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaServicioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categoria_servicio, parent, false)
            return CategoriaServicioViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoriaServicioViewHolder, position: Int) {
            val categoria = categorias[position]

            holder.tvIdCategoriaServicio.text = categoria.id.toString()
            holder.tvDescripcionCategoriaServicio.text = categoria.descripcion
            holder.tvValorKmCategoriaServicio.text = String.format("$%,.2f", categoria.valorKm)

            holder.btnEditarCategoriaServicio.setOnClickListener {
                onEditarClick(categoria)
            }

            holder.btnEliminarCategoriaServicio.setOnClickListener {
                onEliminarClick(categoria)
            }
        }

        override fun getItemCount(): Int = categorias.size

        fun actualizarLista(nuevaLista: List<CategoriaServicio>) {
            categorias = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(categoria: CategoriaServicio) {
        // Primero verificamos dependencias
        verificarDependencias(categoria.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(categoria)
            }
        }
    }

    private fun verificarDependencias(idCategoriaServicio: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_categoria_servicio", idCategoriaServicio)
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
                    "rutas" -> mensajeDetallado.append("\n• Rutas: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(categoria: CategoriaServicio) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la categoría '${categoria.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarCategoria(categoria.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarCategoria(idCategoriaServicio: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_categoria_servicio", idCategoriaServicio)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_categoria_servicio", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarCategorias()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Categoría eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_categoria_servicio", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar categoría: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_categoria_servicio", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_categoria_servicio", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}