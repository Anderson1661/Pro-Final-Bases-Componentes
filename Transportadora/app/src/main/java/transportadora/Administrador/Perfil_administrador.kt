package transportadora.Administrador

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Cliente.Act_perfil_cliente
import transportadora.Cliente.Act_preguntas_cliente
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R

class Perfil_administrador : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_administrador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        botonactualizar.setOnClickListener {
            val intent = Intent(this, Act_perfil_administrador::class.java)
            startActivity(intent)
        }

        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        botoncontra.setOnClickListener {
            val intent = Intent(this@Perfil_administrador, Preg_seguridad::class.java)
            startActivity(intent)
        }

        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)
        botonpreguntas.setOnClickListener {
            val intent = Intent(this, Act_preguntas_administrador::class.java)
            startActivity(intent)
        }
    }
}