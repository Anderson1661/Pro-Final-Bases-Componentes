package transportadora.Administrador.Lineas_vehiculo

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

class Administrar_lineas_vehiculo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LineaVehiculoAdapter
    private val lineasList = mutableListOf<LineaVehiculo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_lineas_vehiculo)
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
            val intent = Intent(this, Crear_lineas_vehiculo::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarLineas()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LineaVehiculoAdapter(lineasList,
            onEditarClick = { linea ->
                val intent = Intent(this, Editar_lineas_vehiculo::class.java).apply {
                    putExtra("id_linea", linea.idLinea)
                    putExtra("id_marca", linea.idMarca)
                }
                startActivity(intent)
            },
            onEliminarClick = { linea ->
                mostrarDialogoEliminacion(linea)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarLineas() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        lineasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            lineasList.add(
                                LineaVehiculo(
                                    idLinea = item.getString("id_linea"),
                                    idMarca = item.getInt("id_marca")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (lineasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay líneas de vehículo registradas"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_lineas_vehiculo", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_lineas_vehiculo", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar líneas de vehículo"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarLineas()
    }

    data class LineaVehiculo(val idLinea: String, val idMarca: Int)

    inner class LineaVehiculoAdapter(
        private var lineas: List<LineaVehiculo>,
        private val onEditarClick: (LineaVehiculo) -> Unit,
        private val onEliminarClick: (LineaVehiculo) -> Unit
    ) : RecyclerView.Adapter<LineaVehiculoAdapter.LineaVehiculoViewHolder>() {

        inner class LineaVehiculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdLineaVehiculo: TextView = itemView.findViewById(R.id.tvIdLineaVehiculo)
            val tvIdMarcaVehiculo: TextView = itemView.findViewById(R.id.tvIdMarcaVehiculo)
            val btnEditarLineaVehiculo: Button = itemView.findViewById(R.id.btnEditarLineaVehiculo)
            val btnEliminarLineaVehiculo: Button = itemView.findViewById(R.id.btnEliminarLineaVehiculo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineaVehiculoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_linea_vehiculo, parent, false)
            return LineaVehiculoViewHolder(view)
        }

        override fun onBindViewHolder(holder: LineaVehiculoViewHolder, position: Int) {
            val linea = lineas[position]

            holder.tvIdLineaVehiculo.text = linea.idLinea
            holder.tvIdMarcaVehiculo.text = linea.idMarca.toString()

            holder.btnEditarLineaVehiculo.setOnClickListener { onEditarClick(linea) }
            holder.btnEliminarLineaVehiculo.setOnClickListener { onEliminarClick(linea) }
        }

        override fun getItemCount(): Int = lineas.size
    }

    private fun mostrarDialogoEliminacion(linea: LineaVehiculo) {
        verificarDependencias(linea.idLinea, linea.idMarca) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(linea)
            }
        }
    }

    private fun verificarDependencias(idLinea: String, idMarca: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_linea", idLinea)
            put("id_marca", idMarca)
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
                    "vehiculos" -> mensajeDetallado.append("\n• Vehículos: $count registro(s)")
                    else -> mensajeDetallado.append("\n• $tabla: $count registro(s)")
                }
            }
        }

        builder.setMessage(mensajeDetallado.toString())
        builder.setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun mostrarConfirmacionEliminacion(linea: LineaVehiculo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la línea '${linea.idLinea}' de la marca ${linea.idMarca}?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarLinea(linea.idLinea, linea.idMarca)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarLinea(idLinea: String, idMarca: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/linea_vehiculo/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_linea", idLinea)
            put("id_marca", idMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarLineas()
                        Toast.makeText(this, "Línea de vehículo eliminada correctamente", Toast.LENGTH_SHORT).show()
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
