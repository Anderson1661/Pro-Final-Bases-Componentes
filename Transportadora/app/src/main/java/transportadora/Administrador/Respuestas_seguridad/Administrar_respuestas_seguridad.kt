package transportadora.Administrador.Respuestas_seguridad

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

class Administrar_respuestas_seguridad : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RespuestaSeguridadAdapter
    private val respuestasList = mutableListOf<RespuestaSeguridad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_respuestas_seguridad)
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
            val intent = Intent(this, Crear_respuestas_seguridad::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarRespuestas()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RespuestaSeguridadAdapter(respuestasList,
            onEditarClick = { respuesta ->
                val intent = Intent(this, Editar_respuestas_seguridad::class.java).apply {
                    putExtra("id_pregunta", respuesta.idPregunta)
                    putExtra("id_usuario", respuesta.idUsuario)
                }
                startActivity(intent)
            },
            onEliminarClick = { respuesta ->
                mostrarDialogoEliminacion(respuesta)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarRespuestas() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        respuestasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            respuestasList.add(
                                RespuestaSeguridad(
                                    idPregunta = item.getInt("id_pregunta"),
                                    idUsuario = item.getInt("id_usuario"),
                                    respuestaPregunta = item.getString("respuesta_pregunta")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (respuestasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay respuestas de seguridad registradas"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_respuestas_seguridad", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_respuestas_seguridad", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar respuestas de seguridad"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarRespuestas()
    }

    data class RespuestaSeguridad(val idPregunta: Int, val idUsuario: Int, val respuestaPregunta: String)

    inner class RespuestaSeguridadAdapter(
        private var respuestas: List<RespuestaSeguridad>,
        private val onEditarClick: (RespuestaSeguridad) -> Unit,
        private val onEliminarClick: (RespuestaSeguridad) -> Unit
    ) : RecyclerView.Adapter<RespuestaSeguridadAdapter.RespuestaSeguridadViewHolder>() {

        inner class RespuestaSeguridadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdPreguntaRespuesta: TextView = itemView.findViewById(R.id.tvIdPreguntaRespuesta)
            val tvIdUsuarioRespuesta: TextView = itemView.findViewById(R.id.tvIdUsuarioRespuesta)
            val tvRespuestaSeguridad: TextView = itemView.findViewById(R.id.tvRespuestaSeguridad)
            val btnEditarRespuestaSeguridad: Button = itemView.findViewById(R.id.btnEditarRespuestaSeguridad)
            val btnEliminarRespuestaSeguridad: Button = itemView.findViewById(R.id.btnEliminarRespuestaSeguridad)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RespuestaSeguridadViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_respuestas_seguridad, parent, false)
            return RespuestaSeguridadViewHolder(view)
        }

        override fun onBindViewHolder(holder: RespuestaSeguridadViewHolder, position: Int) {
            val respuesta = respuestas[position]

            holder.tvIdPreguntaRespuesta.text = respuesta.idPregunta.toString()
            holder.tvIdUsuarioRespuesta.text = respuesta.idUsuario.toString()
            holder.tvRespuestaSeguridad.text = respuesta.respuestaPregunta

            holder.btnEditarRespuestaSeguridad.setOnClickListener { onEditarClick(respuesta) }
            holder.btnEliminarRespuestaSeguridad.setOnClickListener { onEliminarClick(respuesta) }
        }

        override fun getItemCount(): Int = respuestas.size
    }

    private fun mostrarDialogoEliminacion(respuesta: RespuestaSeguridad) {
        verificarDependencias(respuesta.idPregunta, respuesta.idUsuario) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(respuesta)
            }
        }
    }

    private fun verificarDependencias(idPregunta: Int, idUsuario: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPregunta)
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
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(respuesta: RespuestaSeguridad) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar esta respuesta de seguridad?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarRespuesta(respuesta.idPregunta, respuesta.idUsuario)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarRespuesta(idPregunta: Int, idUsuario: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/respuestas_seguridad/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_pregunta", idPregunta)
            put("id_usuario", idUsuario)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarRespuestas()
                        Toast.makeText(this, "Respuesta de seguridad eliminada correctamente", Toast.LENGTH_SHORT).show()
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
