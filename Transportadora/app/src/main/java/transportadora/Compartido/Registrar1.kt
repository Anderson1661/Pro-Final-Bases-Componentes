package transportadora.Compartido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Pais_almacenados
import transportadora.Login.R

class Registrar1 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar1)

        // Referencias a los Spinners
        val spinner_tipos_id = findViewById<Spinner>(R.id.txt_tipo_id_reg1)
        val spinner_paises = findViewById<Spinner>(R.id.spinner_pais)
        val spinner_departamentos = findViewById<Spinner>(R.id.spinner_departamento)
        val spinner_ciudades = findViewById<Spinner>(R.id.spinner_ciudad)
        val spinner_nacionalidad = findViewById<Spinner>(R.id.txt_nacionalidad)

        // --- Tipos de identificación (fijo)
        val tipos_identificacion = listOf("Cédula", "Extranjería")
        val adapter_tipos_id = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos_identificacion)
        adapter_tipos_id.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tipos_id.adapter = adapter_tipos_id

        // --- Departamentos y ciudades (temporalmente fijos)
        val departamentos = listOf("Cundinamarca", "Meta")
        val adapter_departamentos = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
        adapter_departamentos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_departamentos.adapter = adapter_departamentos

        val ciudades = listOf("Bogotá", "Villavicencio")
        val adapter_ciudades = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudades)
        adapter_ciudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_ciudades.adapter = adapter_ciudades

        // --- Cargar países desde el backend
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val paises = withContext(Dispatchers.IO) {
                    Pais_almacenados.obtenerPaises()
                }

                if (paises.isNotEmpty()) {
                    val nombresPaises = paises.map { it.nombre }

                    val adapter_paises = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, nombresPaises)
                    adapter_paises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_paises.adapter = adapter_paises

                    // Reutilizamos el mismo adaptador para nacionalidad
                    val adapter_nacionalidad = ArrayAdapter(this@Registrar1, android.R.layout.simple_spinner_item, nombresPaises)
                    adapter_nacionalidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_nacionalidad.adapter = adapter_nacionalidad
                } else {
                    Toast.makeText(this@Registrar1, "No se encontraron países", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Registrar1, "Error al cargar países: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // --- Botones de navegación
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
