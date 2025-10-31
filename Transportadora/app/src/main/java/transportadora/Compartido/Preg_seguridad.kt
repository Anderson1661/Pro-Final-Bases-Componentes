package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.widget.Toast


class Preg_seguridad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preg_seguridad)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val txtCorreo = findViewById<EditText>(R.id.txt_correo_recuperar)

        val btnHabilitar = findViewById<Button>(R.id.buttonHabilitarRespuestas)
        btnHabilitar.setOnClickListener {
            val correoIngresado = txtCorreo.text.toString()
            // verificar correo ingresado
            Toast.makeText(
                this,
                "Usuario encontrado\nPor favor responda sus preguntas de seguridad",
                Toast.LENGTH_LONG
            ).show()
            findViewById<TextView>(R.id.textView17).visibility = View.VISIBLE
            findViewById<EditText>(R.id.txt_respuesta1).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView18).visibility = View.VISIBLE
            findViewById<EditText>(R.id.txt_respuesta2).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView19).visibility = View.VISIBLE
            findViewById<EditText>(R.id.txt_respuesta3).visibility = View.VISIBLE
            findViewById<Button>(R.id.buttonVerificarPreguntas).visibility = View.VISIBLE
        }

        val btnVerificar = findViewById<Button>(R.id.buttonVerificarPreguntas)
        btnVerificar.setOnClickListener {
            val intent = Intent(this, Cambiar_contra::class.java)
            startActivity(intent)
        }

    }
}