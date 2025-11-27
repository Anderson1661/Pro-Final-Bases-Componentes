package transportadora.Administrador.Categorias_servicio

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Crear_categoria_servicio : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var txtDetalles: EditText
    private lateinit var txtKm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_categoria_servicio)

        txtDetalles = findViewById(R.id.txt_detalles)
        txtKm = findViewById(R.id.txt_km)

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonCrear)
        botonguardar.setOnClickListener {
            crearCategoriaServicio()
        }
    }

    private fun crearCategoriaServicio() {
        val descripcion = txtDetalles.text.toString().trim()
        val valorKm = txtKm.text.toString().trim()

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        if (valorKm.isEmpty()) {
            Toast.makeText(this, "El valor por km es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        val valorKmDouble = try {
            valorKm.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El valor por km debe ser un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/categoria_servicio/create.php"
        val jsonObject = JSONObject().apply {
            put("descripcion", descripcion)
            put("valor_km", valorKmDouble)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_categoria_servicio::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear categoría de servicio: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}