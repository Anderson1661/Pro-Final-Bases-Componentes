package transportadora.Cliente

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.animation.addListener
import transportadora.Login.R


class Transferencia : AppCompatActivity() {

    private var animator: ValueAnimator? = null
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transferencia)

        // Obtén las vistas con findViewById (ejemplo claro y sin lateinit)
        val tvBank: TextView = findViewById(R.id.tvBank)
        val tvAccount: TextView = findViewById(R.id.tvAccount)
        val tvHolder: TextView = findViewById(R.id.tvHolder)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val tvState: TextView = findViewById(R.id.tvState)
        val layoutResult: LinearLayout = findViewById(R.id.layoutResult)

        val dots = listOf(
            findViewById<TextView>(R.id.dot1),
            findViewById<TextView>(R.id.dot2),
            findViewById<TextView>(R.id.dot3),
            findViewById<TextView>(R.id.dot4),
            findViewById<TextView>(R.id.dot5)
        )

        // generar cuenta inicial (meramente visual)
        fun generateRandomAccount() {
            val bancos = listOf("Banco Ejemplo", "Banco Uno", "Banco Móvil", "Banco Nacional", "Banco Global")
            val titulares = listOf("Transportadora S.A.", "Transportes y Cía")
            val bank = bancos.random()
            val holder = titulares.random()
            val length = (10..14).random()
            val accountNumber = (1..length).map { (0..9).random() }.joinToString("")
            tvBank.text = "Banco: $bank"
            tvAccount.text = "Cuenta: $accountNumber"
            tvHolder.text = "Titular: $holder"
        }

        // update dots helper
        fun updateDots(progress: Int) {
            val total = dots.size
            val exact = progress / 100.0 * (total - 1)
            val activeIndex = kotlin.math.round(exact).toInt().coerceIn(0, total - 1)
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

        fun updateStateText(progress: Int) {
            val state = when {
                progress < 33 -> "Estado: Esperando confirmación"
                progress < 66 -> "Estado: Dinero en proceso"
                progress < 100 -> "Estado: Verificando"
                else -> "Estado: Dinero recibido"
            }
            tvState.text = state
        }

        fun setProgress(value: Int) {
            progressBar.progress = value
            updateDots(value)
            updateStateText(value)
        }

        fun showSuccessAndFinish() {
            layoutResult.alpha = 0f
            layoutResult.visibility = View.VISIBLE
            layoutResult.animate()
                .alpha(1f)
                .setDuration(300)
                .withEndAction {
                    // pequeña pausa visual y luego lanzar Historial
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            // Ajusta el nombre de la Activity de Historial si es distinto
                            val intent = Intent(this@Transferencia, Historial_serv_cliente::class.java)
                            startActivity(intent)
                        } catch (e: Exception) {
                            // si la activity no existe por el nombre, ignora o loggea
                        }
                        finish()
                    }, 700)
                }
                .start()
        }

        // iniciar proceso automáticamente (pequeña espera para que UI se estabilice)
        generateRandomAccount()
        progressBar.post {
            progressBar.progress = 0
            updateDots(0)
            tvState.text = "Estado: Esperando confirmación"

            animator = ValueAnimator.ofInt(0, 100).apply {
                duration = 5000L
                interpolator = LinearInterpolator()
                addUpdateListener { anim ->
                    val value = anim.animatedValue as Int
                    setProgress(value)
                }
                addListener(onEnd = {showSuccessAndFinish()})
                start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
    }
}
