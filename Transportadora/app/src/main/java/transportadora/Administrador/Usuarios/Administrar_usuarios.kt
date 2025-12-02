package transportadora.Administrador.Usuarios

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

class Administrar_usuarios : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private val usuariosList = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_usuarios)
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
            val intent = Intent(this, Crear_usuarios::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarUsuarios()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsuarioAdapter(usuariosList,
            onEditarClick = { usuario ->
                val intent = Intent(this, Editar_usuarios::class.java).apply {
                    putExtra("id_usuario", usuario.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { usuario ->
                mostrarDialogoEliminacion(usuario)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarUsuarios() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        usuariosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            usuariosList.add(
                                Usuario(
                                    id = item.getInt("id_usuario"),
                                    idTipoUsuario = item.getInt("id_tipo_usuario"),
                                    correo = item.getString("correo")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (usuariosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay usuarios registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_usuarios", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_usuarios", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar usuarios"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    data class Usuario(val id: Int, val idTipoUsuario: Int, val correo: String)

    inner class UsuarioAdapter(
        private var usuarios: List<Usuario>,
        private val onEditarClick: (Usuario) -> Unit,
        private val onEliminarClick: (Usuario) -> Unit
    ) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

        inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdUsuario: TextView = itemView.findViewById(R.id.tvIdUsuario)
            val tvIdTipoUsuarioUsuario: TextView = itemView.findViewById(R.id.tvIdTipoUsuarioUsuario)
            val tvCorreoUsuario: TextView = itemView.findViewById(R.id.tvCorreoUsuario)
            val btnEditarUsuario: Button = itemView.findViewById(R.id.btnEditarUsuario)
            val btnEliminarUsuario: Button = itemView.findViewById(R.id.btnEliminarUsuario)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_usuario, parent, false)
            return UsuarioViewHolder(view)
        }

        override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
            val usuario = usuarios[position]

            holder.tvIdUsuario.text = usuario.id.toString()
            holder.tvIdTipoUsuarioUsuario.text = usuario.idTipoUsuario.toString()
            holder.tvCorreoUsuario.text = usuario.correo

            holder.btnEditarUsuario.setOnClickListener { onEditarClick(usuario) }
            holder.btnEliminarUsuario.setOnClickListener { onEliminarClick(usuario) }
        }

        override fun getItemCount(): Int = usuarios.size
    }

    private fun mostrarDialogoEliminacion(usuario: Usuario) {
        verificarDependencias(usuario.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(usuario)
            }
        }
    }

    private fun verificarDependencias(idUsuario: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_usuario", idUsuario)
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
                    "respuestas_seguridad" -> mensajeDetallado.append("\n• Respuestas de seguridad: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(usuario: Usuario) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el usuario '${usuario.correo}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarUsuario(usuario.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarUsuario(idUsuario: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/usuario/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_usuario", idUsuario)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarUsuarios()
                        Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
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
