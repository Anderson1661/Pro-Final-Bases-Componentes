package transportadora.Administrador.Metodos_pago

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

class Administrar_metodos_pago : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MetodoPagoAdapter
    private val metodosList = mutableListOf<MetodoPago>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_metodos_pago)
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
            val intent = Intent(this, Crear_metodos_pago::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarMetodos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MetodoPagoAdapter(metodosList,
            onEditarClick = { metodo ->
                val intent = Intent(this, Editar_metodos_pago::class.java).apply {
                    putExtra("id_metodo_pago", metodo.id)
                    putExtra("descripcion", metodo.descripcion)
                }
                startActivity(intent)
            },
            onEliminarClick = { metodo ->
                mostrarDialogoEliminacion(metodo)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarMetodos() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/metodo_pago/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        metodosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            metodosList.add(
                                MetodoPago(
                                    id = item.getInt("id_metodo_pago"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (metodosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay métodos de pago registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_metodos_pago", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_metodos_pago", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar métodos de pago"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarMetodos()
    }

    data class MetodoPago(val id: Int, val descripcion: String)

    inner class MetodoPagoAdapter(
        private var metodos: List<MetodoPago>,
        private val onEditarClick: (MetodoPago) -> Unit,
        private val onEliminarClick: (MetodoPago) -> Unit
    ) : RecyclerView.Adapter<MetodoPagoAdapter.MetodoPagoViewHolder>() {

        inner class MetodoPagoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdMetodoPago: TextView = itemView.findViewById(R.id.tvIdMetodoPago)
            val tvDescripcionMetodoPago: TextView = itemView.findViewById(R.id.tvDescripcionMetodoPago)
            val btnEditarMetodoPago: Button = itemView.findViewById(R.id.btnEditarMetodoPago)
            val btnEliminarMetodoPago: Button = itemView.findViewById(R.id.btnEliminarMetodoPago)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetodoPagoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_metodo_pago, parent, false)
            return MetodoPagoViewHolder(view)
        }

        override fun onBindViewHolder(holder: MetodoPagoViewHolder, position: Int) {
            val metodo = metodos[position]

            holder.tvIdMetodoPago.text = metodo.id.toString()
            holder.tvDescripcionMetodoPago.text = metodo.descripcion

            holder.btnEditarMetodoPago.setOnClickListener {
                onEditarClick(metodo)
            }

            holder.btnEliminarMetodoPago.setOnClickListener {
                onEliminarClick(metodo)
            }
        }

        override fun getItemCount(): Int = metodos.size
    }

    private fun mostrarDialogoEliminacion(metodo: MetodoPago) {
        verificarDependencias(metodo.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(metodo)
            }
        }
    }

    private fun verificarDependencias(idMetodoPago: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/metodo_pago/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_metodo_pago", idMetodoPago)
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
            { error ->
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
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(metodo: MetodoPago) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el método de pago '${metodo.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarMetodo(metodo.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarMetodo(idMetodoPago: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/metodo_pago/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_metodo_pago", idMetodoPago)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarMetodos()
                        Toast.makeText(this, "Método de pago eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al eliminar: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
