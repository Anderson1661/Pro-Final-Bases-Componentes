package transportadora.Compartido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import transportadora.Login.R
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView

class Registrar1 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar1)

        // Asignar valores al spinner de tipos id
        val spinner_tipos_id = findViewById<Spinner>(R.id.txt_tipo_id_reg1)
        val spinner_paises = findViewById<Spinner>(R.id.spinner_pais)
        val spinner_departamentos = findViewById<Spinner>(R.id.spinner_departamento)
        val spinner_ciudades = findViewById<Spinner>(R.id.spinner_ciudad)
        val spinner_nacionalidad = findViewById<Spinner>(R.id.txt_nacionalidad)

        // Lista de opciones (puede venir de un recurso o del código)
        val tipos_identificacion = listOf("Cedula", "Extranjeria")
        val adapter_tipos_id = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos_identificacion)
        adapter_tipos_id.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tipos_id.adapter = adapter_tipos_id

        val paises = listOf("Colombia", "Peru", "Ecuador")
        val adapter_paises = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises)
        adapter_paises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_paises.adapter = adapter_paises
        val adapter_nacionalidad = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises)
        adapter_nacionalidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_nacionalidad.adapter = adapter_nacionalidad

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

        val buttonContinuar = findViewById<Button>(R.id.buttonContinuar)
        buttonContinuar.setOnClickListener {
            val intent = Intent(this, Registrar2::class.java)
            startActivity(intent)
        }


    }
}