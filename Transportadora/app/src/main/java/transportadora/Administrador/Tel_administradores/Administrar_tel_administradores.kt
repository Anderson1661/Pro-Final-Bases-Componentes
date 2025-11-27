package transportadora.Administrador.Tel_administradores

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

class Administrar_tel_administradores : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TelefonoAdministradorAdapter
    private val telefonosList = mutableListOf<TelefonoAdministrador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_tel_administradores)
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
            val intent = Intent(this, Crear_tel_administradores::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarTelefonos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TelefonoAdministradorAdapter(telefonosList,
            onEditarClick = { telefono ->
                val intent = Intent(this, Editar_tel_administradores::class.java).apply {
                    putExtra("id_administrador", telefono.idAdministrador)
                    putExtra("telefono", telefono.telefono)
                }
                startActivity(intent)
            },
            onEliminarClick = { telefono ->
                mostrarDialogoEliminacion(telefono)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarTelefonos() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        telefonosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            telefonosList.add(
                                TelefonoAdministrador(
                                    idAdministrador = item.getInt("id_administrador"),
                                    telefono = item.getLong("telefono")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (telefonosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay teléfonos registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_tel_administradores", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_tel_administradores", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar teléfonos"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarTelefonos()
    }

    data class TelefonoAdministrador(val idAdministrador: Int, val telefono: Long)

    inner class TelefonoAdministradorAdapter(
        private var telefonos: List<TelefonoAdministrador>,
        private val onEditarClick: (TelefonoAdministrador) -> Unit,
        private val onEliminarClick: (TelefonoAdministrador) -> Unit
    ) : RecyclerView.Adapter<TelefonoAdministradorAdapter.TelefonoAdministradorViewHolder>() {

        inner class TelefonoAdministradorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdAdministradorTelefono: TextView = itemView.findViewById(R.id.tvIdAdministradorTelefono)
            val tvTelefonoAdministrador: TextView = itemView.findViewById(R.id.tvTelefonoAdministrador)
            val btnEditarTelefonoAdministrador: Button = itemView.findViewById(R.id.btnEditarTelefonoAdministrador)
            val btnEliminarTelefonoAdministrador: Button = itemView.findViewById(R.id.btnEliminarTelefonoAdministrador)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TelefonoAdministradorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_telefono_administrador, parent, false)
            return TelefonoAdministradorViewHolder(view)
        }

        override fun onBindViewHolder(holder: TelefonoAdministradorViewHolder, position: Int) {
            val telefono = telefonos[position]

            holder.tvIdAdministradorTelefono.text = telefono.idAdministrador.toString()
            holder.tvTelefonoAdministrador.text = telefono.telefono.toString()

            holder.btnEditarTelefonoAdministrador.setOnClickListener { onEditarClick(telefono) }
            holder.btnEliminarTelefonoAdministrador.setOnClickListener { onEliminarClick(telefono) }
        }

        override fun getItemCount(): Int = telefonos.size
    }

    private fun mostrarDialogoEliminacion(telefono: TelefonoAdministrador) {
        verificarDependencias(telefono.idAdministrador, telefono.telefono) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(telefono)
            }
        }
    }

    private fun verificarDependencias(idAdministrador: Int, telefono: Long, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
            put("telefono", telefono)
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
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(telefono: TelefonoAdministrador) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el teléfono ${telefono.telefono} del administrador ${telefono.idAdministrador}?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarTelefono(telefono.idAdministrador, telefono.telefono)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarTelefono(idAdministrador: Int, telefono: Long) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/telefono_administrador/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
            put("telefono", telefono)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarTelefonos()
                        Toast.makeText(this, "Teléfono eliminado correctamente", Toast.LENGTH_SHORT).show()
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
