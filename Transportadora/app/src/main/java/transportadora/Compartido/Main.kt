package transportadora.Compartido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.TextPaint
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import transportadora.Login.R
import transportadora.Compartido.Registrar1

class Main : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var backToast: Toast

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Configurar el toast para mostrar el mensaje de salida
        backToast = Toast.makeText(this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT)

        // Configurar el manejador personalizado para el botón de retroceso
        setupBackPressedHandler()

        // Texto clicable + color personalizado
        val textView = findViewById<TextView>(R.id.textView5)
        val fullText = "¿Aún no tienes una cuenta?\nRegistrar una nueva ahora"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("\n") + 1
        val end = fullText.length

        // Click para abrir Registrar1
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@Main, Registrar1::class.java)
                startActivity(intent)
            }

            // Opcional: quitar subrayado y mantener color
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@Main, R.color.amarillo)
            }
        }

        // Aplicar color + clic
        spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.amarillo)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()

        // Botón Continuar → abre Login
        val button = findViewById<Button>(R.id.buttonContinuar)
        button.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun setupBackPressedHandler() {
        // Crear un callback personalizado para manejar el botón de retroceso
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Si ya presionó una vez, salir completamente
                    backToast.cancel() // Cancelar toast anterior si existe
                    finishAffinity() // Cerrar todas las actividades
                    return
                }

                doubleBackToExitPressedOnce = true
                backToast.show()

                // Resetear el estado después de 2 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }

        // Agregar el callback al dispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // Opcional: Sobrescribir onSupportNavigateUp si usas ActionBar/AppBar
    override fun onSupportNavigateUp(): Boolean {
        // Prevenir navegación mediante la flecha de arriba
        return false
    }

    // Ya no necesitas sobrescribir onBackPressed() o onKeyDown()
    // porque OnBackPressedDispatcher maneja todo
}