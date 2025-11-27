package transportadora.Administrador.Conductores

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

class Administrar_conductores : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConductorAdapter
    private val conductoresList = mutableListOf<Conductor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_conductores)
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
            val intent = Intent(this, Crear_conductores::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarConductores()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ConductorAdapter(conductoresList,
            onEditarClick = { conductor ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_conductores::class.java).apply {
                    putExtra("id_conductor", conductor.id)
                    putExtra("id_estado_conductor", conductor.idEstadoConductor)
                    putExtra("placa_vehiculo", conductor.placaVehiculo)
                    putExtra("identificacion", conductor.identificacion)
                    putExtra("id_tipo_identificacion", conductor.idTipoIdentificacion)
                    putExtra("nombre", conductor.nombre)
                    putExtra("direccion", conductor.direccion)
                    putExtra("correo", conductor.correo)
                    putExtra("id_genero", conductor.idGenero)
                    putExtra("codigo_postal", conductor.codigoPostal)
                    putExtra("id_pais_nacionalidad", conductor.idPaisNacionalidad)
                    putExtra("url_foto", conductor.urlFoto)
                }
                startActivity(intent)
            },
            onEliminarClick = { conductor ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(conductor)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarConductores() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_conductores", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        conductoresList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            conductoresList.add(
                                Conductor(
                                    id = item.getInt("id_conductor"),
                                    idEstadoConductor = item.getInt("id_estado_conductor"),
                                    placaVehiculo = item.getString("placa_vehiculo"),
                                    identificacion = item.getString("identificacion"),
                                    idTipoIdentificacion = item.getInt("id_tipo_identificacion"),
                                    nombre = item.getString("nombre"),
                                    direccion = item.getString("direccion"),
                                    correo = item.getString("correo"),
                                    idGenero = item.getInt("id_genero"),
                                    codigoPostal = item.getString("codigo_postal"),
                                    idPaisNacionalidad = item.getInt("id_pais_nacionalidad"),
                                    urlFoto = item.getString("url_foto")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (conductoresList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay conductores registrados"
                        }
                    } else {
                        Log.e("Administrar_conductores", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_conductores", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_conductores", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar conductores"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarConductores()
    }

    // Data class para los conductores
    data class Conductor(
        val id: Int,
        val idEstadoConductor: Int,
        val placaVehiculo: String,
        val identificacion: String,
        val idTipoIdentificacion: Int,
        val nombre: String,
        val direccion: String,
        val correo: String,
        val idGenero: Int,
        val codigoPostal: String,
        val idPaisNacionalidad: Int,
        val urlFoto: String
    )

    // Adapter para el RecyclerView
    inner class ConductorAdapter(
        private var conductores: List<Conductor>,
        private val onEditarClick: (Conductor) -> Unit,
        private val onEliminarClick: (Conductor) -> Unit
    ) : RecyclerView.Adapter<ConductorAdapter.ConductorViewHolder>() {

        inner class ConductorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdConductor: TextView = itemView.findViewById(R.id.tvIdConductor)
            val tvEstadoConductor: TextView = itemView.findViewById(R.id.tvEstadoConductor)
            val tvPlacaVehiculoConductor: TextView = itemView.findViewById(R.id.tvPlacaVehiculoConductor)
            val tvIdentificacionConductor: TextView = itemView.findViewById(R.id.tvIdentificacionConductor)
            val tvTipoIdentificacionConductor: TextView = itemView.findViewById(R.id.tvTipoIdentificacionConductor)
            val tvNombreConductor: TextView = itemView.findViewById(R.id.tvNombreConductor)
            val tvDireccionConductor: TextView = itemView.findViewById(R.id.tvDireccionConductor)
            val tvCorreoConductor: TextView = itemView.findViewById(R.id.tvCorreoConductor)
            val tvGeneroConductor: TextView = itemView.findViewById(R.id.tvGeneroConductor)
            val tvCodigoPostalConductor: TextView = itemView.findViewById(R.id.tvCodigoPostalConductor)
            val tvPaisNacionalidadConductor: TextView = itemView.findViewById(R.id.tvPaisNacionalidadConductor)
            val tvUrlFotoConductor: TextView = itemView.findViewById(R.id.tvUrlFotoConductor)
            val btnEditarConductor: Button = itemView.findViewById(R.id.btnEditarConductor)
            val btnEliminarConductor: Button = itemView.findViewById(R.id.btnEliminarConductor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConductorViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_conductor, parent, false)
            return ConductorViewHolder(view)
        }

        override fun onBindViewHolder(holder: ConductorViewHolder, position: Int) {
            val conductor = conductores[position]

            holder.tvIdConductor.text = conductor.id.toString()
            holder.tvEstadoConductor.text = conductor.idEstadoConductor.toString()
            holder.tvPlacaVehiculoConductor.text = conductor.placaVehiculo
            holder.tvIdentificacionConductor.text = conductor.identificacion
            holder.tvTipoIdentificacionConductor.text = conductor.idTipoIdentificacion.toString()
            holder.tvNombreConductor.text = conductor.nombre
            holder.tvDireccionConductor.text = conductor.direccion
            holder.tvCorreoConductor.text = conductor.correo
            holder.tvGeneroConductor.text = conductor.idGenero.toString()
            holder.tvCodigoPostalConductor.text = conductor.codigoPostal
            holder.tvPaisNacionalidadConductor.text = conductor.idPaisNacionalidad.toString()
            holder.tvUrlFotoConductor.text = conductor.urlFoto

            holder.btnEditarConductor.setOnClickListener {
                onEditarClick(conductor)
            }

            holder.btnEliminarConductor.setOnClickListener {
                onEliminarClick(conductor)
            }
        }

        override fun getItemCount(): Int = conductores.size

        fun actualizarLista(nuevaLista: List<Conductor>) {
            conductores = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(conductor: Conductor) {
        // Primero verificamos dependencias
        verificarDependencias(conductor.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(conductor)
            }
        }
    }

    private fun verificarDependencias(idConductor: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
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
                    "telefonos" -> mensajeDetallado.append("\n• Teléfonos: $count registro(s)")
                    "usuarios" -> mensajeDetallado.append("\n• Usuarios: $count registro(s)")
                    "rutas" -> mensajeDetallado.append("\n• Rutas asignadas: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(conductor: Conductor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar al conductor '${conductor.nombre}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarConductor(conductor.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarConductor(idConductor: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/conductor/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_conductor", idConductor)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_conductores", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarConductores()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Conductor eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_conductores", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar conductor: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_conductores", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_conductores", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}