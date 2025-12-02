package transportadora.Administrador.Tipos_identificacion

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

class Administrar_tipos_identificacion : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TipoIdentificacionAdapter
    private val tiposList = mutableListOf<TipoIdentificacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_tipos_identificacion)
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
            val intent = Intent(this, Crear_tipos_identificacion::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarTipos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TipoIdentificacionAdapter(tiposList,
            onEditarClick = { tipo ->
                val intent = Intent(this, Editar_tipos_identificacion::class.java).apply {
                    putExtra("id_tipo_identificacion", tipo.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { tipo ->
                mostrarDialogoEliminacion(tipo)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarTipos() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_identificacion/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        tiposList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            tiposList.add(
                                TipoIdentificacion(
                                    id = item.getInt("id_tipo_identificacion"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (tiposList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay tipos de identificación registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_tipos_identificacion", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_tipos_identificacion", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar tipos de identificación"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarTipos()
    }

    data class TipoIdentificacion(val id: Int, val descripcion: String)

    inner class TipoIdentificacionAdapter(
        private var tipos: List<TipoIdentificacion>,
        private val onEditarClick: (TipoIdentificacion) -> Unit,
        private val onEliminarClick: (TipoIdentificacion) -> Unit
    ) : RecyclerView.Adapter<TipoIdentificacionAdapter.TipoIdentificacionViewHolder>() {

        inner class TipoIdentificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdTipoIdentificacion: TextView = itemView.findViewById(R.id.tvIdTipoIdentificacion)
            val tvDescripcionTipoIdentificacion: TextView = itemView.findViewById(R.id.tvDescripcionTipoIdentificacion)
            val btnEditarTipoIdentificacion: Button = itemView.findViewById(R.id.btnEditarTipoIdentificacion)
            val btnEliminarTipoIdentificacion: Button = itemView.findViewById(R.id.btnEliminarTipoIdentificacion)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoIdentificacionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tipo_identificacion, parent, false)
            return TipoIdentificacionViewHolder(view)
        }

        override fun onBindViewHolder(holder: TipoIdentificacionViewHolder, position: Int) {
            val tipo = tipos[position]

            holder.tvIdTipoIdentificacion.text = tipo.id.toString()
            holder.tvDescripcionTipoIdentificacion.text = tipo.descripcion

            holder.btnEditarTipoIdentificacion.setOnClickListener { onEditarClick(tipo) }
            holder.btnEliminarTipoIdentificacion.setOnClickListener { onEliminarClick(tipo) }
        }

        override fun getItemCount(): Int = tipos.size
    }

    private fun mostrarDialogoEliminacion(tipo: TipoIdentificacion) {
        verificarDependencias(tipo.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(tipo)
            }
        }
    }

    private fun verificarDependencias(idTipoIdentificacion: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_identificacion/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_identificacion", idTipoIdentificacion)
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
                    "clientes" -> mensajeDetallado.append("\n• Clientes: $count registro(s)")
                    "administradores" -> mensajeDetallado.append("\n• Administradores: $count registro(s)")
                    "conductores" -> mensajeDetallado.append("\n• Conductores: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(tipo: TipoIdentificacion) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el tipo de identificación '${tipo.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarTipo(tipo.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarTipo(idTipoIdentificacion: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_identificacion/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_identificacion", idTipoIdentificacion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarTipos()
                        Toast.makeText(this, "Tipo de identificación eliminado correctamente", Toast.LENGTH_SHORT).show()
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
