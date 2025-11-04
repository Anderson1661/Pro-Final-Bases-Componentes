package transportadora.Administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Cliente.Perfil_cliente
import transportadora.Login.R

class Act_preguntas_administrador : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_preguntas_administrador)

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

        val buttonDescartar = findViewById<Button>(R.id.buttonDescartar)
        val buttonGuardar = findViewById<Button>(R.id.buttonGuardar)
        // Botón DESCARTAR → Confirmación antes de salir
        buttonDescartar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Descartar cambios")
            builder.setMessage("¿Deseas descartar los cambios realizados?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                finish() // Cierra la actividad
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Solo cierra el diálogo
            }
            builder.show()
        }

        // Botón GUARDAR → Muestra toast y abre la siguiente pantalla
        buttonGuardar.setOnClickListener {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Perfil_cliente::class.java)
            startActivity(intent)
        }
    }
}