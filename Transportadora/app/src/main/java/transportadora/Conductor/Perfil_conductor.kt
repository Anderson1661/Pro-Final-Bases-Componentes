package transportadora.Conductor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Administrador.Act_perfil_administrador
import transportadora.Administrador.Act_preguntas_administrador
import transportadora.Cliente.Act_perfil_conductor
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R

class Perfil_conductor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_conductor)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        botonactualizar.setOnClickListener {
            val intent = Intent(this, Act_perfil_conductor::class.java)
            startActivity(intent)
        }

        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        botoncontra.setOnClickListener {
            val intent = Intent(this@Perfil_conductor, Preg_seguridad::class.java)
            startActivity(intent)
        }

        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)
        botonpreguntas.setOnClickListener {
            val intent = Intent(this, Act_preguntas_conductor::class.java)
            startActivity(intent)
        }

    }
}