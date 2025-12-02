package transportadora.Administrador.Clientes

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

class Administrar_clientes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClienteAdapter
    private val clientesList = mutableListOf<Cliente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_clientes)
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
            val intent = Intent(this, Crear_clientes::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarClientes()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClienteAdapter(clientesList,
            onEditarClick = { cliente ->
                // Pasar solo el ID, los demás datos se cargarán desde el servidor
                val intent = Intent(this, Editar_clientes::class.java).apply {
                    putExtra("id_cliente", cliente.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { cliente ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(cliente)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarClientes() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/cliente/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_clientes", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        clientesList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            clientesList.add(
                                Cliente(
                                    id = item.getInt("id_cliente"),
                                    identificacion = item.getString("identificacion"),
                                    idTipoIdentificacion = item.getInt("id_tipo_identificacion"),
                                    nombre = item.getString("nombre"),
                                    direccion = item.getString("direccion"),
                                    correo = item.getString("correo"),
                                    idGenero = item.getInt("id_genero"),
                                    idPaisNacionalidad = item.getInt("id_pais_nacionalidad"),
                                    codigoPostal = item.getString("codigo_postal")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (clientesList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay clientes registrados"
                        }
                    } else {
                        Log.e("Administrar_clientes", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_clientes", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_clientes", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar clientes"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarClientes()
    }

    // Data class para los clientes
    data class Cliente(
        val id: Int,
        val identificacion: String,
        val idTipoIdentificacion: Int,
        val nombre: String,
        val direccion: String,
        val correo: String,
        val idGenero: Int,
        val idPaisNacionalidad: Int,
        val codigoPostal: String
    )

    // Adapter para el RecyclerView
    inner class ClienteAdapter(
        private var clientes: List<Cliente>,
        private val onEditarClick: (Cliente) -> Unit,
        private val onEliminarClick: (Cliente) -> Unit
    ) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

        inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdCliente: TextView = itemView.findViewById(R.id.tvIdCliente)
            val tvIdentificacionCliente: TextView = itemView.findViewById(R.id.tvIdentificacionCliente)
            val tvTipoIdentificacionCliente: TextView = itemView.findViewById(R.id.tvTipoIdentificacionCliente)
            val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
            val tvDireccionCliente: TextView = itemView.findViewById(R.id.tvDireccionCliente)
            val tvCorreoCliente: TextView = itemView.findViewById(R.id.tvCorreoCliente)
            val tvGeneroCliente: TextView = itemView.findViewById(R.id.tvGeneroCliente)
            val tvPaisNacionalidadCliente: TextView = itemView.findViewById(R.id.tvPaisNacionalidadCliente)
            val tvCodigoPostalCliente: TextView = itemView.findViewById(R.id.tvCodigoPostalCliente)
            val btnEditarCliente: Button = itemView.findViewById(R.id.btnEditarCliente)
            val btnEliminarCliente: Button = itemView.findViewById(R.id.btnEliminarCliente)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cliente, parent, false)
            return ClienteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
            val cliente = clientes[position]

            holder.tvIdCliente.text = cliente.id.toString()
            holder.tvIdentificacionCliente.text = cliente.identificacion
            holder.tvTipoIdentificacionCliente.text = cliente.idTipoIdentificacion.toString()
            holder.tvNombreCliente.text = cliente.nombre
            holder.tvDireccionCliente.text = cliente.direccion
            holder.tvCorreoCliente.text = cliente.correo
            holder.tvGeneroCliente.text = cliente.idGenero.toString()
            holder.tvPaisNacionalidadCliente.text = cliente.idPaisNacionalidad.toString()
            holder.tvCodigoPostalCliente.text = cliente.codigoPostal

            holder.btnEditarCliente.setOnClickListener {
                onEditarClick(cliente)
            }

            holder.btnEliminarCliente.setOnClickListener {
                onEliminarClick(cliente)
            }
        }

        override fun getItemCount(): Int = clientes.size

        fun actualizarLista(nuevaLista: List<Cliente>) {
            clientes = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(cliente: Cliente) {
        // Primero verificamos dependencias
        verificarDependencias(cliente.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(cliente)
            }
        }
    }

    private fun verificarDependencias(idCliente: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/cliente/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_cliente", idCliente)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("VerificarDependencias", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        // No hay dependencias bloqueantes, se puede eliminar
                        callback(false, response.getString("mensaje"), null)
                    } else {
                        // Hay dependencias bloqueantes
                        val dependencias = mutableMapOf<String, Int>()
                        val jsonDependencies = response.getJSONObject("dependencies")
                        val keys = jsonDependencies.keys()

                        while (keys.hasNext()) {
                            val key = keys.next()
                            dependencias[key] = jsonDependencies.getInt(key)
                        }

                        val dependenciasBloqueantes = response.getInt("dependencias_bloqueantes")
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
                    "telefonos" -> mensajeDetallado.append("\n• Teléfonos: $count registro(s) - BLOQUEANTE")
                    "rutas" -> mensajeDetallado.append("\n• Rutas: $count registro(s) - BLOQUEANTE")
                    "usuarios" -> mensajeDetallado.append("\n• Usuario: $count registro(s) - SE ELIMINARÁ")
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

    private fun mostrarConfirmacionEliminacion(cliente: Cliente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar al cliente '${cliente.nombre}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarCliente(cliente.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarCliente(idCliente: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/cliente/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_cliente", idCliente)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_clientes", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarClientes()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Cliente eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_clientes", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar cliente: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_clientes", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_clientes", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}