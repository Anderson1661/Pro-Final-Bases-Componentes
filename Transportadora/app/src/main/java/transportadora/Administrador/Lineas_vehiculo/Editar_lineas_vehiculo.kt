package transportadora.Administrador.Lineas_vehiculo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Administrador.Estados_vehiculo.Administrar_estados_vehiculo
import transportadora.Login.R

class Editar_lineas_vehiculo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_lineas_vehiculo)


        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val botoncrear = findViewById<Button>(R.id.buttonGuardar)
        botoncrear.setOnClickListener {
            val intent = Intent(this, Administrar_lineas_vehiculo::class.java)
            startActivity(intent)
        }

    }
}