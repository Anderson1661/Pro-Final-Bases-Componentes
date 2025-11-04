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

class Act_perfil_administrador : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_administrador)

        // Asignar valores al spinner de tipos id
        val spinner_tipos_id = findViewById<Spinner>(R.id.txt_tipo_id)
        val spinner_paises = findViewById<Spinner>(R.id.txt_pais)
        val spinner_departamentos = findViewById<Spinner>(R.id.txt_departamento)
        val spinner_ciudades = findViewById<Spinner>(R.id.txt_ciudad)

        // Lista de opciones (puede venir de un recurso o del código)
        val tipos_identificacion = listOf("Cedula", "Extranjeria")
        val adapter_tipos_id = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos_identificacion)
        adapter_tipos_id.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tipos_id.adapter = adapter_tipos_id

        val paises = listOf("Colombia", "Peru", "Ecuador")
        val adapter_paises = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises)
        adapter_paises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_paises.adapter = adapter_paises

        val departamentos = listOf("Cundinamarca", "Meta")
        val adapter_departamentos = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
        adapter_departamentos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_departamentos.adapter = adapter_departamentos

        val ciudades = listOf("Bogota", "Villavicencio")
        val adapter_ciudades = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudades)
        adapter_ciudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_ciudades.adapter = adapter_ciudades

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