package transportadora.Administrador.Codigos_postales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Administrador.Lineas_vehiculo.Administrar_lineas_vehiculo
import transportadora.Login.R

class Crear_codigos_postales : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_codigos_postales)

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botonguardar = findViewById<Button>(R.id.buttonGuardar)
        botonguardar.setOnClickListener {
            val intent = Intent(this, Administrar_lineas_vehiculo::class.java)
            startActivity(intent)
        }

    }
}