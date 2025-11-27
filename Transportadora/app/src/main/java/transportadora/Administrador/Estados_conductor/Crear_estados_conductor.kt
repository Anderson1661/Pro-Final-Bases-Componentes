package transportadora.Administrador.Estados_conductor

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

class Crear_estados_conductor : AppCompatActivity() {
    private lateinit var txtDescripcion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_estados_conductor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtDescripcion = findViewById(R.id.txt_descripcion)

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botoncrear = findViewById<Button>(R.id.buttonCrear)
        botoncrear.setOnClickListener {
            crearEstadoConductor()
        }
    }

    private fun crearEstadoConductor() {
        val descripcion = txtDescripcion.text.toString().trim()

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es requerida", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.BASE_URL + "consultas/administrador/tablas/estado_conductor/create.php"
        val jsonObject = JSONObject().apply {
            put("descripcion", descripcion)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                if (response.getString("success") == "1") {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Administrar_estados_conductor::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, response.getString("mensaje"), Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al crear estado de conductor: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}