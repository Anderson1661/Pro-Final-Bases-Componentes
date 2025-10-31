package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R

class Cambiar_contra : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contra)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Confirmar cancelación")
            builder.setMessage("¿Estás seguro que quieres cancelar el cambio de contraseña? Si tienes una sesión activa, se cerrará.")

            builder.setPositiveButton("Sí") { dialog, _ ->
                val intent = Intent(this, Main::class.java)
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Cierra el cuadro de diálogo y sigue en la pantalla actual
            }

            val dialog = builder.create()
            dialog.show()
        }

        val btnActContra = findViewById<Button>(R.id.buttonRegistrar)
        btnActContra.setOnClickListener{
            Toast.makeText(
                this,
                "Contraseña actualizada,\nPor favor ingrese nuevamente",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
            finish()
        }

    }
}