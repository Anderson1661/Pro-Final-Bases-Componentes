package transportadora.Cliente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import transportadora.Login.R
import kotlin.math.round

class Transferencia : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transferencia)

        // Referencias UI
        val tvBank: TextView = findViewById(R.id.tvBank)
        val tvAccount: TextView = findViewById(R.id.tvAccount)
        val tvHolder: TextView = findViewById(R.id.tvHolder)
        val tvAmount: TextView = findViewById(R.id.tvAmount)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val tvState: TextView = findViewById(R.id.tvState)
        val layoutResult: LinearLayout = findViewById(R.id.layoutResult)

        val totalPagar = intent.getDoubleExtra("TOTAL_PAGAR", 0.0)
        tvAmount.text = String.format("$ %,.2f", totalPagar)

        // Capturar el email del Intent para pasarlo al historial
        val userEmail = intent.getStringExtra("USER_EMAIL") // <-- ¡LÍNEA AÑADIDA!

        val dots = listOf(
            findViewById<TextView>(R.id.dot1),
            findViewById<TextView>(R.id.dot2),
            findViewById<TextView>(R.id.dot3),
            findViewById<TextView>(R.id.dot4),
            findViewById<TextView>(R.id.dot5)
        )

        // Cuenta simulada
        fun generateRandomAccount() {
            val bancos = listOf("Banco Bancolombia", "Banco Davivienda", "Banco Caja Social")
            val titulares = listOf("Transportadora PEPFOOD S.A.")
            val bank = bancos.random()
            val holder = titulares.random()
            val length = (10..14).random()
            val accountNumber = (1..length).map { (0..9).random() }.joinToString("")
            tvBank.text = "Banco: $bank"
            tvAccount.text = "Cuenta: $accountNumber"
            tvHolder.text = "Titular: $holder"
        }

        // Actualiza los puntos
        fun updateDots(progress: Int) {
            val total = dots.size
            val exact = progress / 100.0 * (total - 1)
            val activeIndex = round(exact).toInt().coerceIn(0, total - 1)
            val activeColor = ContextCompat.getColor(this, R.color.naranja)
            val inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)

            dots.forEachIndexed { idx, tv ->
                if (idx <= activeIndex) {
                    tv.setTextColor(activeColor)
                    tv.scaleX = if (idx == activeIndex) 1.3f else 1.0f
                    tv.scaleY = if (idx == activeIndex) 1.3f else 1.0f
                } else {
                    tv.setTextColor(inactiveColor)
                    tv.scaleX = 1.0f
                    tv.scaleY = 1.0f
                }
            }
        }

        // Texto de estado
        fun updateStateText(progress: Int) {
            val state = when {
                progress < 33 -> "Estado: Esperando confirmación"
                progress < 66 -> "Estado: Dinero en proceso"
                progress < 100 -> "Estado: Verificando"
                else -> "Estado: Dinero recibido"
            }
            tvState.text = state
        }

        // Aplicar progreso a todo
        fun setProgress(value: Int) {
            progressBar.progress = value
            updateDots(value)
            updateStateText(value)
        }

        // Mostrar éxito y cambiar de pantalla
        fun showSuccessAndFinish() {
            layoutResult.alpha = 0f
            layoutResult.visibility = View.VISIBLE
            layoutResult.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction {
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            val intent = Intent(this@Transferencia, Historial_serv_cliente::class.java)

                            // Adjuntar el email al Intent
                            if (userEmail != null) { // <-- ¡BLOQUE AÑADIDO!
                                intent.putExtra("USER_EMAIL", userEmail)
                            }

                            startActivity(intent)
                        } catch (e: Exception) {
                            // si no existe la Activity, ignorar
                        }
                        finish()
                    }, 300) // pequeña pausa final
                }
                .start()
        }

        // ---- Inicio del proceso ----
        generateRandomAccount()
        progressBar.progress = 0
        updateDots(0)
        tvState.text = "Estado: Esperando confirmación"

        // ANIMACIÓN 4 SEGUNDOS REALES
        lifecycleScope.launch {
            val totalDuration = 6000L
            val steps = 100
            val interval = totalDuration / steps

            for (i in 0..steps) {
                setProgress(i)
                delay(interval)
            }

            showSuccessAndFinish()
        }
    }
}
