package transportadora.Compartido

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R

class Ayuda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ayuda)

        val txtVolver = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolver.setOnClickListener {
            finish()
        }
    }
}