package transportadora.Administrador.Tel_clientes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Administrador.Tel_administradores.Administrar_tel_administradores
import transportadora.Login.R

class Crear_tel_clientes : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_tel_clientes)

        val btnVolver = findViewById<TextView>(R.id.txt_volver_login)
        btnVolver.setOnClickListener {
            finish()
        }

        val buttonCrear = findViewById<Button>(R.id.buttonCrear)
        buttonCrear.setOnClickListener {
            val intent = Intent(this, Administrar_tel_clientes::class.java)
            startActivity(intent)
        }

    }
}