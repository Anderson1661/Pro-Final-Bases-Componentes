package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import transportadora.Login.R

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1️⃣ Volver a la activity anterior
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener {
            // finaliza esta activity y vuelve a la anterior
            finish()
        }

        // 2️⃣ Ingresar: tomar correo y guardar en variable
        val botonIngresar = findViewById<Button>(R.id.boton_ingresar_login)
        val txtCorreo = findViewById<EditText>(R.id.txt_correo_login)

        botonIngresar.setOnClickListener {
            val correoIngresado = txtCorreo.text.toString()
            // Por ahora solo guardamos en variable
            // Puedes usar Log o Toast para probar
            println("Correo ingresado: $correoIngresado")
        }

        // 3️⃣ Ir a la activity Registrar
        val txtRegistrar = findViewById<TextView>(R.id.txt_registrar_login)
        txtRegistrar.setOnClickListener {
            val intent = Intent(this, Registrar1::class.java)
            startActivity(intent)
        }
    }
}
