package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R

class Perfil_cliente : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_cliente)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        botonactualizar.setOnClickListener {
            val intent = Intent(this, Act_perfil_cliente::class.java)
            startActivity(intent)
        }

        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        botoncontra.setOnClickListener {
            val intent = Intent(this@Perfil_cliente, Preg_seguridad::class.java)
            startActivity(intent)
        }

        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)
        botonpreguntas.setOnClickListener {
            val intent = Intent(this, Act_preguntas_cliente::class.java)
            startActivity(intent)
        }

    }
}