package transportadora.Administrador.Marcas_vehiculo

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

class Administrar_marcas_vehiculo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarcaVehiculoAdapter
    private val marcasList = mutableListOf<MarcaVehiculo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_marcas_vehiculo)
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
            val intent = Intent(this, Crear_marcas_vehiculo::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        setupRecyclerView()
        cargarMarcas()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MarcaVehiculoAdapter(marcasList,
            onEditarClick = { marca ->
                // Manejar clic en editar
                val intent = Intent(this, Editar_marcas_vehiculo::class.java).apply {
                    putExtra("id_marca", marca.id)
                    putExtra("nombre_marca", marca.nombreMarca)
                }
                startActivity(intent)
            },
            onEliminarClick = { marca ->
                // Manejar clic en eliminar
                mostrarDialogoEliminacion(marca)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarMarcas() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/marca_vehiculo/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_marcas_vehiculo", "Respuesta: $response")

                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        marcasList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            marcasList.add(
                                MarcaVehiculo(
                                    id = item.getInt("id_marca"),
                                    nombreMarca = item.getString("nombre_marca")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        // Mostrar mensaje si no hay datos
                        if (marcasList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay marcas registradas"
                        }
                    } else {
                        Log.e("Administrar_marcas_vehiculo", "Error en la respuesta: ${response.getString("mensaje")}")
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_marcas_vehiculo", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_marcas_vehiculo", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar marcas"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarMarcas()
    }

    data class MarcaVehiculo(
        val id: Int,
        val nombreMarca: String
    )

    inner class MarcaVehiculoAdapter(
        private var marcas: List<MarcaVehiculo>,
        private val onEditarClick: (MarcaVehiculo) -> Unit,
        private val onEliminarClick: (MarcaVehiculo) -> Unit
    ) : RecyclerView.Adapter<MarcaVehiculoAdapter.MarcaVehiculoViewHolder>() {

        inner class MarcaVehiculoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdMarcaVehiculo: TextView = itemView.findViewById(R.id.tvIdMarcaVehiculo)
            val tvNombreMarcaVehiculo: TextView = itemView.findViewById(R.id.tvNombreMarcaVehiculo)
            val btnEditarMarcaVehiculo: Button = itemView.findViewById(R.id.btnEditarMarcaVehiculo)
            val btnEliminarMarcaVehiculo: Button = itemView.findViewById(R.id.btnEliminarMarcaVehiculo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaVehiculoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_marca_vehiculo, parent, false)
            return MarcaVehiculoViewHolder(view)
        }

        override fun onBindViewHolder(holder: MarcaVehiculoViewHolder, position: Int) {
            val marca = marcas[position]

            holder.tvIdMarcaVehiculo.text = marca.id.toString()
            holder.tvNombreMarcaVehiculo.text = marca.nombreMarca

            holder.btnEditarMarcaVehiculo.setOnClickListener {
                onEditarClick(marca)
            }

            holder.btnEliminarMarcaVehiculo.setOnClickListener {
                onEliminarClick(marca)
            }
        }

        override fun getItemCount(): Int = marcas.size

        fun actualizarLista(nuevaLista: List<MarcaVehiculo>) {
            marcas = nuevaLista
            notifyDataSetChanged()
        }
    }

    private fun mostrarDialogoEliminacion(marca: MarcaVehiculo) {
        verificarDependencias(marca.id) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(marca)
            }
        }
    }

    private fun verificarDependencias(idMarca: Int, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/marca_vehiculo/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_marca", idMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("VerificarDependencias", "Respuesta: $response")

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
                    "lineas_vehiculo" -> mensajeDetallado.append("\n• Líneas de vehículo: $count registro(s)")
                    "vehiculos" -> mensajeDetallado.append("\n• Vehículos: $count registro(s)")
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

    private fun mostrarConfirmacionEliminacion(marca: MarcaVehiculo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar la marca '${marca.nombreMarca}'?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarMarca(marca.id)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun eliminarMarca(idMarca: Int) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/marca_vehiculo/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_marca", idMarca)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    Log.d("Administrar_marcas_vehiculo", "Respuesta eliminación: $response")

                    if (response.getString("success") == "1") {
                        cargarMarcas()
                        Toast.makeText(this, "Marca eliminada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Administrar_marcas_vehiculo", "Error al eliminar: ${response.getString("mensaje")}")
                        Toast.makeText(this, "Error al eliminar marca: ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_marcas_vehiculo", "Error parsing JSON: ${e.message}")
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("Administrar_marcas_vehiculo", "Volley error al eliminar: ${error.message}")
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
