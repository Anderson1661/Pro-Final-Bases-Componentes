package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import transportadora.Login.R
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Configuracion.ApiConfig
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

data class VerificationResult(
    val success: Boolean,
    val message: String,
    val idUsuario: Int,
    val preguntas: List<JSONObject>
)

class Preg_seguridad : AppCompatActivity() {

    private var idUsuarioActual: Int = -1
    private var listaIdsPreguntas: MutableList<Int> = mutableListOf()
    private val client = OkHttpClient()

    private lateinit var txtCorreo: EditText
    private lateinit var btnHabilitar: Button
    private lateinit var btnVerificar: Button

    private lateinit var tvPregunta1: TextView
    private lateinit var etRespuesta1: EditText
    private lateinit var tvPregunta2: TextView
    private lateinit var etRespuesta2: EditText
    private lateinit var tvPregunta3: TextView
    private lateinit var etRespuesta3: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preg_seguridad)

        // Inicializar vistas
        txtCorreo = findViewById(R.id.txt_correo_recuperar)
        btnHabilitar = findViewById(R.id.buttonHabilitarRespuestas)
        btnVerificar = findViewById(R.id.buttonVerificarPreguntas)

        tvPregunta1 = findViewById(R.id.textView17)
        etRespuesta1 = findViewById(R.id.txt_respuesta1)
        tvPregunta2 = findViewById(R.id.textView18)
        etRespuesta2 = findViewById(R.id.txt_respuesta2)
        tvPregunta3 = findViewById(R.id.textView19)
        etRespuesta3 = findViewById(R.id.txt_respuesta3)

        // Inicialmente, ocultar los campos de preguntas/respuestas
        mostrarCamposPreguntas(false)

        // Botón Volver
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        // Lógica de los botones
        btnHabilitar.setOnClickListener {
            verificarCorreoYCargarPreguntas()
        }

        btnVerificar.setOnClickListener {
            verificarRespuestas()
        }
    }

    // =================================================================
    // FUNCIÓN 1: VERIFICAR CORREO Y CARGAR PREGUNTAS (EN CLIC HABILITAR)
    // =================================================================
    private fun verificarCorreoYCargarPreguntas() {
        val correo = txtCorreo.text.toString().trim()
        if (correo.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese su correo electrónico.", Toast.LENGTH_LONG).show()
            return
        }

        mostrarCamposPreguntas(false)

        CoroutineScope(Dispatchers.Main).launch {
            // CORREGIDO: La desestructuración usa la nueva data class
            val (success, message, idUsuario, preguntas) = withContext(Dispatchers.IO) {
                llamarAPIVerificarCorreo(correo)
            }

            if (success && preguntas.size == 3) {
                Toast.makeText(this@Preg_seguridad, "Por favor responde tus preguntas de seguridad.", Toast.LENGTH_LONG).show()

                idUsuarioActual = idUsuario
                listaIdsPreguntas.clear()

                // Cargar preguntas en TextViews
                tvPregunta1.text = preguntas[0].optString("descripcion")
                tvPregunta2.text = preguntas[1].optString("descripcion")
                tvPregunta3.text = preguntas[2].optString("descripcion")

                listaIdsPreguntas.add(preguntas[0].optInt("id_pregunta"))
                listaIdsPreguntas.add(preguntas[1].optInt("id_pregunta"))
                listaIdsPreguntas.add(preguntas[2].optInt("id_pregunta"))

                // Habilitar campos
                mostrarCamposPreguntas(true)

            } else {
                Toast.makeText(this@Preg_seguridad, message, Toast.LENGTH_LONG).show()
                mostrarCamposPreguntas(false)
            }
        }
    }

    // CORREGIDO: Retorna la data class VerificationResult
    private fun llamarAPIVerificarCorreo(correo: String): VerificationResult {
        val url = "${ApiConfig.BASE_URL}/consultas/consultar_preguntas_seguridad.php"

        val requestBody = FormBody.Builder()
            .add("correo", correo)
            .build()

        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return VerificationResult(false, "Error de red: ${response.code}", -1, emptyList())
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Error desconocido.")
                val idUsuario = json.optInt("id_usuario", -1)

                val preguntasJson = json.optJSONArray("preguntas")
                val preguntasList = mutableListOf<JSONObject>()

                if (success && preguntasJson != null) {
                    for (i in 0 until preguntasJson.length()) {
                        preguntasList.add(preguntasJson.getJSONObject(i))
                    }
                }

                return VerificationResult(success, mensaje, idUsuario, preguntasList)
            }
        } catch (e: IOException) {
            Log.e("Preg_seguridad", "Error de conexión: ${e.message}")
            return VerificationResult(false, "Error de conexión: Verifique su red.", -1, emptyList())
        } catch (e: Exception) {
            Log.e("Preg_seguridad", "Error al procesar la respuesta: ${e.message}")
            return VerificationResult(false, "Error al procesar la respuesta del servidor.", -1, emptyList())
        }
    }

    private fun mostrarCamposPreguntas(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        tvPregunta1.visibility = visibility
        etRespuesta1.visibility = visibility
        tvPregunta2.visibility = visibility
        etRespuesta2.visibility = visibility
        tvPregunta3.visibility = visibility
        etRespuesta3.visibility = visibility
        btnVerificar.visibility = visibility
    }


    // =================================================================
    // FUNCIÓN 2: VERIFICAR RESPUESTAS (EN CLIC VERIFICAR)
    // =================================================================
    private fun verificarRespuestas() {
        if (idUsuarioActual == -1 || listaIdsPreguntas.size != 3) {
            Toast.makeText(this, "Primero debe verificar su correo.", Toast.LENGTH_LONG).show()
            return
        }

        val respuestas = listOf(
            etRespuesta1.text.toString(), // No se usa .trim() ya que la BD espera el valor literal
            etRespuesta2.text.toString(),
            etRespuesta3.text.toString()
        )

        if (respuestas.any { it.isEmpty() }) {
            Toast.makeText(this, "Debe responder las 3 preguntas.", Toast.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val (success, message) = withContext(Dispatchers.IO) {
                llamarAPIVerificarRespuestas(idUsuarioActual, listaIdsPreguntas, respuestas)
            }

            Toast.makeText(this@Preg_seguridad, message, Toast.LENGTH_LONG).show()

            if (success) {
                val intent = Intent(this@Preg_seguridad, Cambiar_contra::class.java)
                intent.putExtra("USER_ID_RECUPERACION", idUsuarioActual)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun llamarAPIVerificarRespuestas(idUsuario: Int, idsPreguntas: List<Int>, respuestas: List<String>): Pair<Boolean, String> {
        val url = "${ApiConfig.BASE_URL}/consultas/verificar_respuestas_seguridad.php"

        val requestBody = FormBody.Builder()
            .add("id_usuario", idUsuario.toString())
            .add("id_pregunta1", idsPreguntas[0].toString())
            .add("respuesta1", respuestas[0])
            .add("id_pregunta2", idsPreguntas[1].toString())
            .add("respuesta2", respuestas[1])
            .add("id_pregunta3", idsPreguntas[2].toString())
            .add("respuesta3", respuestas[2])
            .build()

        val request = Request.Builder().url(url).post(requestBody).build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return Pair(false, "Error de red: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Error desconocido.")

                return Pair(success, mensaje)
            }
        } catch (e: IOException) {
            Log.e("Preg_seguridad", "Error de conexión (Verif Resp): ${e.message}")
            return Pair(false, "Error de conexión: Verifique su red.")
        } catch (e: Exception) {
            Log.e("Preg_seguridad", "Error al procesar la respuesta (Verif Resp): ${e.message}")
            return Pair(false, "Error al procesar la respuesta del servidor.")
        }
    }
}