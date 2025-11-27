package transportadora.Administrador.Pasajeros_servicio

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

class Administrar_pasajeros_servicio : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PasajeroRutaAdapter
    private val pasajerosList = mutableListOf<PasajeroRuta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_pasajeros_servicio)
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
            val intent = Intent(this, Crear_pasajeros_servicio::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        cargarPasajeros()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.layoutAdministradores)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PasajeroRutaAdapter(pasajerosList,
            onEditarClick = { pasajero ->
                val intent = Intent(this, Editar_pasajeros_servicio::class.java).apply {
                    putExtra("id_ruta", pasajero.idRuta)
                    putExtra("nombre_pasajero", pasajero.nombrePasajero)
                }
                startActivity(intent)
            },
            onEliminarClick = { pasajero ->
                mostrarDialogoEliminacion(pasajero)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun cargarPasajeros() {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/read.php"
        val jsonObject = JSONObject()

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        val datosArray = response.getJSONArray("datos")
                        pasajerosList.clear()

                        for (i in 0 until datosArray.length()) {
                            val item = datosArray.getJSONObject(i)
                            pasajerosList.add(
                                PasajeroRuta(
                                    idRuta = item.getInt("id_ruta"),
                                    nombrePasajero = item.getString("nombre_pasajero")
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()

                        if (pasajerosList.isEmpty()) {
                            findViewById<TextView>(R.id.textViewTitulo).text = "No hay pasajeros registrados"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Administrar_pasajeros_servicio", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Log.e("Administrar_pasajeros_servicio", "Volley error: ${error.message}")
                findViewById<TextView>(R.id.textViewTitulo).text = "Error al cargar pasajeros"
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        cargarPasajeros()
    }

    data class PasajeroRuta(val idRuta: Int, val nombrePasajero: String)

    inner class PasajeroRutaAdapter(
        private var pasajeros: List<PasajeroRuta>,
        private val onEditarClick: (PasajeroRuta) -> Unit,
        private val onEliminarClick: (PasajeroRuta) -> Unit
    ) : RecyclerView.Adapter<PasajeroRutaAdapter.PasajeroRutaViewHolder>() {

        inner class PasajeroRutaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvIdRutaPasajero: TextView = itemView.findViewById(R.id.tvIdRutaPasajero)
            val tvNombrePasajeroRuta: TextView = itemView.findViewById(R.id.tvNombrePasajeroRuta)
            val btnEditarPasajeroRuta: Button = itemView.findViewById(R.id.btnEditarPasajeroRuta)
            val btnEliminarPasajeroRuta: Button = itemView.findViewById(R.id.btnEliminarPasajeroRuta)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasajeroRutaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pasajero_ruta, parent, false)
            return PasajeroRutaViewHolder(view)
        }

        override fun onBindViewHolder(holder: PasajeroRutaViewHolder, position: Int) {
            val pasajero = pasajeros[position]

            holder.tvIdRutaPasajero.text = pasajero.idRuta.toString()
            holder.tvNombrePasajeroRuta.text = pasajero.nombrePasajero

            holder.btnEditarPasajeroRuta.setOnClickListener { onEditarClick(pasajero) }
            holder.btnEliminarPasajeroRuta.setOnClickListener { onEliminarClick(pasajero) }
        }

        override fun getItemCount(): Int = pasajeros.size
    }

    private fun mostrarDialogoEliminacion(pasajero: PasajeroRuta) {
        verificarDependencias(pasajero.idRuta, pasajero.nombrePasajero) { tieneDependencias, mensaje, dependencias ->
            if (tieneDependencias) {
                mostrarAlertaDependencias(mensaje, dependencias)
            } else {
                mostrarConfirmacionEliminacion(pasajero)
            }
        }
    }

    private fun verificarDependencias(idRuta: Int, nombrePasajero: String, callback: (Boolean, String, Map<String, Int>?) -> Unit) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/verificar_dependencias.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRuta)
            put("nombre_pasajero", nombrePasajero)
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

    private fun mostrarConfirmacionEliminacion(pasajero: PasajeroRuta) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que quieres eliminar al pasajero '${pasajero.nombrePasajero}' de la ruta ${pasajero.idRuta}?")

        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarPasajero(pasajero.idRuta, pasajero.nombrePasajero)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun eliminarPasajero(idRuta: Int, nombrePasajero: String) {
        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/delete.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRuta)
            put("nombre_pasajero", nombrePasajero)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    if (response.getString("success") == "1") {
                        cargarPasajeros()
                        Toast.makeText(this, "Pasajero eliminado correctamente", Toast.LENGTH_SHORT).show()
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
