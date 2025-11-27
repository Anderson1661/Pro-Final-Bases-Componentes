package transportadora.Administrador.Pasajeros_servicio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Almacenados.Administrador.RutaSimple
import transportadora.Almacenados.Administrador.Ruta_almacenados
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_pasajeros_servicio : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var spinnerRuta: Spinner
    private lateinit var txtNombrePasajero: EditText
    private var listaRutas: List<RutaSimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_pasajeros_servicio)

        spinnerRuta = findViewById(R.id.spinner_ruta)
        txtNombrePasajero = findViewById(R.id.txt_descripcion)

        cargarRutas()

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonGuardar)
        botonguardar.setOnClickListener {
            crearPasajeroRuta()
        }
    }

    private fun cargarRutas() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                listaRutas = withContext(Dispatchers.IO) {
                    Ruta_almacenados.obtenerRutas()
                }
                if (listaRutas.isNotEmpty()) {
                    val descripcionesRutas = listaRutas.map { it.descripcion }
                    spinnerRuta.adapter = ArrayAdapter(
                        this@Crear_pasajeros_servicio,
                        android.R.layout.simple_spinner_item,
                        descripcionesRutas
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Crear_pasajeros_servicio, "No hay rutas disponibles", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Crear_pasajeros_servicio, "Error al cargar rutas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearPasajeroRuta() {
        val nombrePasajero = txtNombrePasajero.text.toString().trim()
        val posicionRuta = spinnerRuta.selectedItemPosition

        if (nombrePasajero.isEmpty()) {
            Toast.makeText(this, "El nombre del pasajero es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaRutas.isEmpty() || posicionRuta < 0) {
            Toast.makeText(this, "Debe seleccionar una ruta", Toast.LENGTH_SHORT).show()
            return
        }

        val idRuta = listaRutas[posicionRuta].id_ruta

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/pasajero_ruta/create.php"
        val jsonObject = JSONObject().apply {
            put("id_ruta", idRuta)
            put("nombre_pasajero", nombrePasajero)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_pasajeros_servicio::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear pasajero: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}