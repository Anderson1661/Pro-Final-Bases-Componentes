package transportadora.Administrador.Generos

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

class Administrar_generos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GeneroAdapter
    private val generosList = mutableListOf<Genero>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_generos)
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
            val intent = Intent(this, Crear_generos::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarGeneros()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GeneroAdapter(generosList,
            onEditarClick = { genero ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_generos::class.java).apply {
                    putExtra("id_genero", genero.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { genero ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(genero)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarGeneros() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/genero/read.php"
        val jsonObject = JSONObject()
        // No se necesitan parámetros para esta consulta

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_generos", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        generosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            generosList.add(
                                Genero(
                                    id = item.getInt("id_genero"),
                                    descripcion = item.getString("descripcion")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (generosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay géneros registrados"
                        }
                    } else {
                        Log.e("Administrar_generos", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_generos", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_generos", "Volley error: ${error.message}")
                // Mostrar mensaje de error
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar géneros"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando la actividad se reanude
        cargarGeneros()
    }

    // Data class para los géneros
    data class Genero(val id: Int, val descripcion: String)

    // Adapter para el RecyclerView
    inner class GeneroAdapter(
        private var generos: List<Genero>,
        private val onEditarClick: (Genero) -> Unit,
        private val onEliminarClick: (Genero) -> Unit
    ) : RecyclerView.Adapter<GeneroAdapter.GeneroViewHolder>() {

        inner class GeneroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdGenero: TextView = itemView.findViewById(R.id.tvIdGenero)
            val tvDescripcionGenero: TextView = itemView.findViewById(R.id.tvDescripcionGenero)
            val btnEditarGenero: Button = itemView.findViewById(R.id.btnEditarGenero)
            val btnEliminarGenero: Button = itemView.findViewById(R.id.btnEliminarGenero)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_generos, parent, false)
            return GeneroViewHolder(view)
        }

        override fun onBindViewHolder(holder: GeneroViewHolder, position: Int) {
            val genero = generos[position]

            holder.tvIdGenero.text = genero.id.toString()
            holder.tvDescripcionGenero.text = genero.descripcion

            holder.btnEditarGenero.setOnClickListener {
                onEditarClick(genero)
            }

            holder.btnEliminarGenero.setOnClickListener {
                onEliminarClick(genero)
            }
        }

        override fun getItemCount(): Int = generos.size

        fun actualizarLista(nuevaLista: List<Genero>) {
            generos = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(genero: Genero) {
        // Primero verificamos dependencias
        verificarDependencias(genero.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                // Mostrar alerta de que no se puede eliminar por dependencias
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                // Mostrar diálogo de confirmación para eliminar
                mostrarConfirmacionEliminacion(genero)
            }
        }
    }

    private fun verificarDependencias(idGenero: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/genero/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_genero", idGenero)
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

    private fun mostrarConfirmacionEliminacion(genero: Genero) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar el género '${genero.descripcion}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarGenero(genero.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarGenero(idGenero: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/genero/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_genero", idGenero)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_generos", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        // Eliminación exitosa, recargar la lista
                        cargarGeneros()
                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Género eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_generos", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar género: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_generos", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_generos", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}