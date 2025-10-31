package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R

class Registrar2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar2)

        // Asignar valores al spinner de tipos id
        val spinner_pregunta1 = findViewById<Spinner>(R.id.txt_pregunta1)
        val spinner_pregunta2 = findViewById<Spinner>(R.id.txt_pregunta2)
        val spinner_pregunta3 = findViewById<Spinner>(R.id.txt_pregunta3)

        // Lista de opciones (puede venir de un recurso o del código)
        val preguntas = listOf("Pregunta 1", "Pregunta 2", "Pregunta 3")
        val adapter_preguntas = ArrayAdapter(this, android.R.layout.simple_spinner_item, preguntas)
        adapter_preguntas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pregunta1.adapter = adapter_preguntas

        val adapter_paises = ArrayAdapter(this, android.R.layout.simple_spinner_item, preguntas)
        adapter_preguntas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pregunta2.adapter = adapter_preguntas

        val adapter_departamentos = ArrayAdapter(this, android.R.layout.simple_spinner_item, preguntas)
        adapter_preguntas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pregunta3.adapter = adapter_preguntas


        // escuchar botones y volver
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val buttonRegistrar = findViewById<Button>(R.id.buttonRegistrar)
        buttonRegistrar.setOnClickListener {
            Toast.makeText(
                this,
                "Cliente registrado exitosamente, \npor favor ahora inicie sesión",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }

    }
}