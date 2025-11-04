package transportadora.Administrador.Estados_vehiculo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import transportadora.Administrador.Principal_administrador
import transportadora.Login.R

class Administrar_estados_vehiculo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_estados_vehiculo)

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            val intent = Intent(this, Principal_administrador::class.java)
            startActivity(intent)
        }

        val btnCrear = findViewById<Button>(R.id.btnCrear)
        btnCrear.setOnClickListener {
            val intent = Intent(this, Crear_estados_vehiculo::class.java)
            startActivity(intent)
        }

        val btnEditar = findViewById<Button>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            val intent = Intent(this, Editar_estados_vehiculo::class.java)
            startActivity(intent)
        }
    }
}