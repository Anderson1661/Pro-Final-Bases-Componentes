package transportadora.Administrador.Preguntas_seguridad

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

class Administrar_preguntas_seguridad : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PreguntaSeguridadAdapter
    private val preguntasList = mutableListOf<PreguntaSeguridad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_preguntas_seguridad)
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
            val intent = Intent(this, Crear_preguntas_seguridad::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarPreguntas()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PreguntaSeguridadAdapter(preguntasList,
            onEditarClick = { pregunta ->
                val intent = Intent(this, Editar_preguntas_seguridad::class.java).apply {
                    putExtra("id_pregunta", pregunta.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { pregunta ->
                mostrarDialogoEliminacion(pregunta)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarPreguntas() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/preguntas_seguridad/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        preguntasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            preguntasList.add(
                                PreguntaSeguridad(
                                    id = item.getInt("id_pregunta"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (preguntasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay preguntas de seguridad registradas"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_preguntas_seguridad", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_preguntas_seguridad", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar preguntas de seguridad"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarPreguntas()
    }

    data class PreguntaSeguridad(val id: Int, val descripcion: String)

    inner class PreguntaSeguridadAdapter(
        private var preguntas: List<PreguntaSeguridad>,
        private val onEditarClick: (PreguntaSeguridad) -> Unit,
        private val onEliminarClick: (PreguntaSeguridad) -> Unit
    ) : RecyclerView.Adapter<PreguntaSeguridadAdapter.PreguntaSeguridadViewHolder>() {

        inner class PreguntaSeguridadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdPreguntaSeguridad: TextView = itemView.findViewById(R.id.tvIdPreguntaSeguridad)
            val tvDescripcionPreguntaSeguridad: TextView = itemView.findViewById(R.id.tvDescripcionPreguntaSeguridad)
            val btnEditarPreguntaSeguridad: Button = itemView.findViewById(R.id.btnEditarPreguntaSeguridad)
            val btnEliminarPreguntaSeguridad: Button = itemView.findViewById(R.id.btnEliminarPreguntaSeguridad)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaSeguridadViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_preguntas_seguridad, parent, false)
            return PreguntaSeguridadViewHolder(view)
        }

        override fun onBindViewHolder(holder: PreguntaSeguridadViewHolder, position: Int) {
            val pregunta = preguntas[position]

            holder.tvIdPreguntaSeguridad.text = pregunta.id.toString()
            holder.tvDescripcionPreguntaSeguridad.text = pregunta.descripcion

            holder.btnEditarPreguntaSeguridad.setOnClickListener { onEditarClick(pregunta) }
            holder.btnEliminarPreguntaSeguridad.setOnClickListener { onEliminarClick(pregunta) }
        }

        override fun getItemCount(): Int = preguntas.size
    }

    private fun mostrarDialogoEliminacion(pregunta: PreguntaSeguridad) {
        verificarDependencias(pregunta.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(pregunta)
            }
        }
    }

    private fun verificarDependencias(idPregunta: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/preguntas_seguridad/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPregunta)
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

    private fun mostrarConfirmacionEliminacion(pregunta: PreguntaSeguridad) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la pregunta '${pregunta.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarPregunta(pregunta.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarPregunta(idPregunta: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/preguntas_seguridad/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPregunta)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarPreguntas()
                        Toast.makeText(this, "Pregunta de seguridad eliminada correctamente", Toast.LENGTH_SHORT).show()
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
