package transportadora.Administrador.Paises

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

class Administrar_paises : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaisAdapter
    private val paisesList = mutableListOf<Pais>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_paises)
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
            val intent = Intent(this, Crear_paises::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarPaises()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PaisAdapter(paisesList,
            onEditarClick = { pais ->
                val intent = Intent(this, Editar_paises::class.java).apply {
                    putExtra("id_pais", pais.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { pais ->
                mostrarDialogoEliminacion(pais)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarPaises() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pais/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        paisesList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            paisesList.add(
                                Pais(
                                    id = item.getInt("id_pais"),
                                    nombre = item.getString("nombre")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (paisesList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay países registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_paises", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_paises", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar países"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarPaises()
    }

    data class Pais(val id: Int, val nombre: String)

    inner class PaisAdapter(
        private var paises: List<Pais>,
        private val onEditarClick: (Pais) -> Unit,
        private val onEliminarClick: (Pais) -> Unit
    ) : RecyclerView.Adapter<PaisAdapter.PaisViewHolder>() {

        inner class PaisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdPais: TextView = itemView.findViewById(R.id.tvIdPais)
            val tvNombrePais: TextView = itemView.findViewById(R.id.tvNombrePais)
            val btnEditarPais: Button = itemView.findViewById(R.id.btnEditarPais)
            val btnEliminarPais: Button = itemView.findViewById(R.id.btnEliminarPais)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaisViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pais, parent, false)
            return PaisViewHolder(view)
        }

        override fun onBindViewHolder(holder: PaisViewHolder, position: Int) {
            val pais = paises[position]

            holder.tvIdPais.text = pais.id.toString()
            holder.tvNombrePais.text = pais.nombre

            holder.btnEditarPais.setOnClickListener { onEditarClick(pais) }
            holder.btnEliminarPais.setOnClickListener { onEliminarClick(pais) }
        }

        override fun getItemCount(): Int = paises.size
    }

    private fun mostrarDialogoEliminacion(pais: Pais) {
        verificarDependencias(pais.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(pais)
            }
        }
    }

    private fun verificarDependencias(idPais: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pais/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_pais", idPais)
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
                    "codigos_postales" -> mensajeDetallado.append("\n• Códigos postales: $count registro(s)")
                    "clientes" -> mensajeDetallado.append("\n• Clientes: $count registro(s)")
                    "conductores" -> mensajeDetallado.append("\n• Conductores: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(pais: Pais) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el país '${pais.nombre}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarPais(pais.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarPais(idPais: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pais/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_pais", idPais)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarPaises()
                        Toast.makeText(this, "País eliminado correctamente", Toast.LENGTH_SHORT).show()
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
