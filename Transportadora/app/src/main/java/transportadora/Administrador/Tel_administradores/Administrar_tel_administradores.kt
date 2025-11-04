package transportadora.Administrador.Tel_administradores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Administrador.Estados_servicio.Crear_estados_servicio
import transportadora.Administrador.Estados_servicio.Editar_estados_servicio
import transportadora.Administrador.Principal_administrador
import transportadora.Login.R

class Administrar_tel_administradores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_tel_administradores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            val intent = Intent(this, Principal_administrador::class.java)
            startActivity(intent)
        }

        val btnCrear = findViewById<Button>(R.id.btnCrear)
        btnCrear.setOnClickListener {
            val intent = Intent(this, Crear_tel_administradores::class.java)
            startActivity(intent)
        }

        val btnEditar = findViewById<Button>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            val intent = Intent(this, Editar_tel_administradores::class.java)
            startActivity(intent)
        }
    }
}