package transportadora.Compartido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import transportadora.Cliente.Principal_cliente
import transportadora.Conductor.Registrar_conductor
import transportadora.Login.R


class Login : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val txtOlvidarcontra = findViewById<TextView>(R.id.txt_olvidar_contra)
        txtOlvidarcontra.setOnClickListener {
            val intent = Intent(this, Preg_seguridad::class.java)
            startActivity(intent)
        }

        val botonIngresar = findViewById<Button>(R.id.boton_ingresar_login)
        val txtCorreo = findViewById<EditText>(R.id.txt_correo_login)
        val txtContra = findViewById<EditText>(R.id.txt_contra_login)


        botonIngresar.setOnClickListener {
            val correoIngresado = txtCorreo.text.toString()
            val contraIngresada = txtContra.text.toString()
            // validar corrreo y contra
            Toast.makeText(
                this,
                "Sesion iniciada exitosamente",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this@Login, Principal_cliente::class.java)
            startActivity(intent)
        }

        val txtRegistrar = findViewById<TextView>(R.id.txt_registrar_login)
        txtRegistrar.setOnClickListener {
            val intent = Intent(this, Registrar1::class.java)
            startActivity(intent)
        }

        val txt_registrar_conductor = findViewById<TextView>(R.id.txt_registrar_conductor)
        txt_registrar_conductor.setOnClickListener {
            val intent = Intent(this@Login, Registrar_conductor::class.java)
            startActivity(intent)
        }
    }
}
