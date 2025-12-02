package transportadora.Administrador.Codigos_postales

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

class Administrar_codigos_postales : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CodigoPostalAdapter
    private val codigosPostalesList = mutableListOf<CodigoPostal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_codigos_postales)
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
            val intent = Intent(this, Crear_codigos_postales::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarCodigosPostales()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CodigoPostalAdapter(codigosPostalesList,
            onEditarClick = { codigoPostal ->
                // Pasar solo el ID, los demás datos se cargarán desde el servidor
                val intent = Intent(this, Editar_codigos_postales::class.java).apply {
                    putExtra("id_codigo_postal", codigoPostal.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { codigoPostal ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(codigoPostal)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarCodigosPostales() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_codigos_postales", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        codigosPostalesList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            codigosPostalesList.add(
                                CodigoPostal(
                                    id = item.getString("id_codigo_postal"),
                                    idPais = item.getInt("id_pais"),
                                    departamento = item.getString("departamento"),
                                    ciudad = item.getString("ciudad")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (codigosPostalesList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay códigos postales registrados"
                        }
                    } else {
                        Log.e("Administrar_codigos_postales", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_codigos_postales", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_codigos_postales", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar códigos postales"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarCodigosPostales()
    }

    // Data class para los códigos postales
    data class CodigoPostal(
        val id: String,
        val idPais: Int,
        val departamento: String,
        val ciudad: String
    )

    // Adapter para el RecyclerView
    inner class CodigoPostalAdapter(
        private var codigosPostales: List<CodigoPostal>,
        private val onEditarClick: (CodigoPostal) -> Unit,
        private val onEliminarClick: (CodigoPostal) -> Unit
    ) : RecyclerView.Adapter<CodigoPostalAdapter.CodigoPostalViewHolder>() {

        inner class CodigoPostalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdCodigoPostal: TextView = itemView.findViewById(R.id.tvIdCodigoPostal)
            val tvIdPaisCodigoPostal: TextView = itemView.findViewById(R.id.tvIdPaisCodigoPostal)
            val tvDepartamentoCodigoPostal: TextView = itemView.findViewById(R.id.tvDepartamentoCodigoPostal)
            val tvCiudadCodigoPostal: TextView = itemView.findViewById(R.id.tvCiudadCodigoPostal)
            val btnEditarCodigoPostal: Button = itemView.findViewById(R.id.btnEditarCodigoPostal)
            val btnEliminarCodigoPostal: Button = itemView.findViewById(R.id.btnEliminarCodigoPostal)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodigoPostalViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_codigo_postal, parent, false)
            return CodigoPostalViewHolder(view)
        }

        override fun onBindViewHolder(holder: CodigoPostalViewHolder, position: Int) {
            val codigoPostal = codigosPostales[position]

            holder.tvIdCodigoPostal.text = codigoPostal.id
            holder.tvIdPaisCodigoPostal.text = codigoPostal.idPais.toString()
            holder.tvDepartamentoCodigoPostal.text = codigoPostal.departamento
            holder.tvCiudadCodigoPostal.text = codigoPostal.ciudad

            holder.btnEditarCodigoPostal.setOnClickListener {
                onEditarClick(codigoPostal)
            }

            holder.btnEliminarCodigoPostal.setOnClickListener {
                onEliminarClick(codigoPostal)
            }
        }

        override fun getItemCount(): Int = codigosPostales.size

        fun actualizarLista(nuevaLista: List<CodigoPostal>) {
            codigosPostales = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(codigoPostal: CodigoPostal) {
        // Primero verificamos dependencias
        verificarDependencias(codigoPostal.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(codigoPostal)
            }
        }
    }

    private fun verificarDependencias(idCodigoPostal: String, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_codigo_postal", idCodigoPostal)
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
                    "clientes" -> mensajeDetallado.append("\n• Clientes: $count registro(s)")
                    "administradores" -> mensajeDetallado.append("\n• Administradores: $count registro(s)")
                    "conductores" -> mensajeDetallado.append("\n• Conductores: $count registro(s)")
                    "rutas_origen" -> mensajeDetallado.append("\n• Rutas (origen): $count registro(s)")
                    "rutas_destino" -> mensajeDetallado.append("\n• Rutas (destino): $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(codigoPostal: CodigoPostal) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el código postal '${codigoPostal.id}' (${codigoPostal.ciudad})?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarCodigoPostal(codigoPostal.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarCodigoPostal(idCodigoPostal: String) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/codigo_postal/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_codigo_postal", idCodigoPostal)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_codigos_postales", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarCodigosPostales()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Código postal eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_codigos_postales", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar código postal: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_codigos_postales", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_codigos_postales", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}