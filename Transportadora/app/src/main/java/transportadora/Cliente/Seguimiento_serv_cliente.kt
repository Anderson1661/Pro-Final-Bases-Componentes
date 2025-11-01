package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R

class Seguimiento_serv_cliente : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seguimiento_serv_cliente)

        //Menu lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
            startActivity(intent)
        }
        val txtcambiarcontra = findViewById<TextView>(R.id.cambiocontra)
        txtcambiarcontra.setOnClickListener {
            val intent = Intent(this@Seguimiento_serv_cliente, transportadora.Compartido.Preg_seguridad::class.java)
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent = Intent(this@Seguimiento_serv_cliente, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent = Intent(this@Seguimiento_serv_cliente, transportadora.Compartido.Ayuda::class.java)
            startActivity(intent)
        }

        //menu inferior
        val txtmenu1 = findViewById<TextView>(R.id.menu1)
        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
        txtmenu1.setOnClickListener {
            val intent = Intent(this, Principal_cliente::class.java)
            startActivity(intent)
        }
        val txtmenu2 = findViewById<TextView>(R.id.menu2)
        txtmenu2.setOnClickListener {
            scrollView.post {
                scrollView.smoothScrollTo(0, 0)
            }
        }
        val txtmenu3 = findViewById<TextView>(R.id.menu3)
        txtmenu3.setOnClickListener {
            val intent = Intent(this, Historial_serv_cliente::class.java)
            startActivity(intent)
        }

        val btnBuscar = findViewById<Button>(R.id.btn_buscar)
        val layoutDatos = findViewById<LinearLayout>(R.id.layout_datos_envio)

        btnBuscar.setOnClickListener {
            layoutDatos.visibility = View.VISIBLE

            // (opcional) hacer scroll automático para ver los resultados
            val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
            scrollView.post {
                scrollView.smoothScrollTo(0, layoutDatos.top)
            }
        }


    }
}