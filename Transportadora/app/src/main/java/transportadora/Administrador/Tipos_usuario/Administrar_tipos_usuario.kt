package transportadora.Administrador.Tipos_usuario

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

class Administrar_tipos_usuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TipoUsuarioAdapter
    private val tiposList = mutableListOf<TipoUsuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_tipos_usuario)
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
            val intent = Intent(this, Crear_tipos_usuario::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarTipos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TipoUsuarioAdapter(tiposList,
            onEditarClick = { tipo ->
                val intent = Intent(this, Editar_tipos_usuario::class.java).apply {
                    putExtra("id_tipo_usuario", tipo.id)
                    putExtra("descripcion", tipo.descripcion)
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
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_usuario/read.php"
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
                                TipoUsuario(
                                    id = item.getInt("id_tipo_usuario"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (tiposList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay tipos de usuario registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_tipos_usuario", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_tipos_usuario", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar tipos de usuario"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarTipos()
    }

    data class TipoUsuario(val id: Int, val descripcion: String)

    inner class TipoUsuarioAdapter(
        private var tipos: List<TipoUsuario>,
        private val onEditarClick: (TipoUsuario) -> Unit,
        private val onEliminarClick: (TipoUsuario) -> Unit
    ) : RecyclerView.Adapter<TipoUsuarioAdapter.TipoUsuarioViewHolder>() {

        inner class TipoUsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdTipoUsuario: TextView = itemView.findViewById(R.id.tvIdTipoUsuario)
            val tvDescripcionTipoUsuario: TextView = itemView.findViewById(R.id.tvDescripcionTipoUsuario)
            val btnEditarTipoUsuario: Button = itemView.findViewById(R.id.btnEditarTipoUsuario)
            val btnEliminarTipoUsuario: Button = itemView.findViewById(R.id.btnEliminarTipoUsuario)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoUsuarioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tipo_usuario, parent, false)
            return TipoUsuarioViewHolder(view)
        }

        override fun onBindViewHolder(holder: TipoUsuarioViewHolder, position: Int) {
            val tipo = tipos[position]

            holder.tvIdTipoUsuario.text = tipo.id.toString()
            holder.tvDescripcionTipoUsuario.text = tipo.descripcion

            holder.btnEditarTipoUsuario.setOnClickListener { onEditarClick(tipo) }
            holder.btnEliminarTipoUsuario.setOnClickListener { onEliminarClick(tipo) }
        }

        override fun getItemCount(): Int = tipos.size
    }

    private fun mostrarDialogoEliminacion(tipo: TipoUsuario) {
        verificarDependencias(tipo.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(tipo)
            }
        }
    }

    private fun verificarDependencias(idTipoUsuario: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_usuario/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_usuario", idTipoUsuario)
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
                    "usuarios" -> mensajeDetallado.append("\n• Usuarios: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(tipo: TipoUsuario) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el tipo de usuario '${tipo.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarTipo(tipo.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarTipo(idTipoUsuario: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/tipo_usuario/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_tipo_usuario", idTipoUsuario)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarTipos()
                        Toast.makeText(this, "Tipo de usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
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
