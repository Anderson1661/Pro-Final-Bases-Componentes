package transportadora.Administrador.Administradores

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

class Administrar_administradores : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdministradorAdapter
    private val administradoresList = mutableListOf<Administrador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_administradores)
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
            val intent = Intent(this, Crear_administradores::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarAdministradores()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdministradorAdapter(administradoresList,
            onEditarClick = { administrador ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_administradores::class.java).apply {
                    putExtra("id_administrador", administrador.id)
                    putExtra("identificacion", administrador.identificacion)
                    putExtra("id_tipo_identificacion", administrador.idTipoIdentificacion)
                    putExtra("nombre", administrador.nombre)
                    putExtra("direccion", administrador.direccion)
                    putExtra("correo", administrador.correo)
                    putExtra("id_genero", administrador.idGenero)
                    putExtra("codigo_postal", administrador.codigoPostal)
                }
                startActivity(intent)
            },
            onEliminarClick = { administrador ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(administrador)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarAdministradores() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/administrador/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_administradores", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        administradoresList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            administradoresList.add(
                                Administrador(
                                    id = item.getInt("id_administrador"),
                                    identificacion = item.getString("identificacion"),
                                    idTipoIdentificacion = item.getInt("id_tipo_identificacion"),
                                    nombre = item.getString("nombre"),
                                    direccion = item.getString("direccion"),
                                    correo = item.getString("correo"),
                                    idGenero = item.getInt("id_genero"),
                                    codigoPostal = item.getString("codigo_postal")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (administradoresList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay administradores registrados"
                        }
                    } else {
                        Log.e("Administrar_administradores", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_administradores", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_administradores", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar administradores"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarAdministradores()
    }

    // Data class para los administradores
    data class Administrador(
        val id: Int,
        val identificacion: String,
        val idTipoIdentificacion: Int,
        val nombre: String,
        val direccion: String,
        val correo: String,
        val idGenero: Int,
        val codigoPostal: String
    )

    // Adapter para el RecyclerView
    inner class AdministradorAdapter(
        private var administradores: List<Administrador>,
        private val onEditarClick: (Administrador) -> Unit,
        private val onEliminarClick: (Administrador) -> Unit
    ) : RecyclerView.Adapter<AdministradorAdapter.AdministradorViewHolder>() {

        inner class AdministradorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdAdministrador: TextView = itemView.findViewById(R.id.tvIdAdministrador)
            val tvIdentificacionAdministrador: TextView = itemView.findViewById(R.id.tvIdentificacionAdministrador)
            val tvTipoIdentificacionAdministrador: TextView = itemView.findViewById(R.id.tvTipoIdentificacionAdministrador)
            val tvNombreAdministrador: TextView = itemView.findViewById(R.id.tvNombreAdministrador)
            val tvDireccionAdministrador: TextView = itemView.findViewById(R.id.tvDireccionAdministrador)
            val tvCorreoAdministrador: TextView = itemView.findViewById(R.id.tvCorreoAdministrador)
            val tvGeneroAdministrador: TextView = itemView.findViewById(R.id.tvGeneroAdministrador)
            val tvCodigoPostalAdministrador: TextView = itemView.findViewById(R.id.tvCodigoPostalAdministrador)
            val btnEditarAdministrador: Button = itemView.findViewById(R.id.btnEditarAdministrador)
            val btnEliminarAdministrador: Button = itemView.findViewById(R.id.btnEliminarAdministrador)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdministradorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_administrador, parent, false)
            return AdministradorViewHolder(view)
        }

        override fun onBindViewHolder(holder: AdministradorViewHolder, position: Int) {
            val administrador = administradores[position]

            holder.tvIdAdministrador.text = administrador.id.toString()
            holder.tvIdentificacionAdministrador.text = administrador.identificacion
            holder.tvTipoIdentificacionAdministrador.text = administrador.idTipoIdentificacion.toString()
            holder.tvNombreAdministrador.text = administrador.nombre
            holder.tvDireccionAdministrador.text = administrador.direccion
            holder.tvCorreoAdministrador.text = administrador.correo
            holder.tvGeneroAdministrador.text = administrador.idGenero.toString()
            holder.tvCodigoPostalAdministrador.text = administrador.codigoPostal

            holder.btnEditarAdministrador.setOnClickListener {
                onEditarClick(administrador)
            }

            holder.btnEliminarAdministrador.setOnClickListener {
                onEliminarClick(administrador)
            }
        }

        override fun getItemCount(): Int = administradores.size

        fun actualizarLista(nuevaLista: List<Administrador>) {
            administradores = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(administrador: Administrador) {
        // Primero verificamos dependencias
        verificarDependencias(administrador.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(administrador)
            }
        }
    }

    private fun verificarDependencias(idAdministrador: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/administrador/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
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

    private fun mostrarConfirmacionEliminacion(administrador: Administrador) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar al administrador '${administrador.nombre}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarAdministrador(administrador.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarAdministrador(idAdministrador: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/administrador/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_administrador", idAdministrador)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_administradores", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarAdministradores()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Administrador eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_administradores", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar administrador: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_administradores", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_administradores", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}