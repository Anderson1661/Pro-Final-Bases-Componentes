package transportadora.Compartido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class Cambiar_contra : AppCompatActivity() {

    // Agregadas para la lógica de actualización
    private var idUsuario: Int = -1
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contra)

        // 1. CAPTURA DEL ID DE USUARIO
        idUsuario = intent.getIntExtra("USER_ID_RECUPERACION", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "Error de seguridad: Usuario no identificado para cambiar contraseña.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Referencias a las vistas (usando los IDs de tu XML)
        val txtNuevaContra = findViewById<EditText>(R.id.txt_respuesta1) // Nueva Contraseña
        val txtConfirmarContra = findViewById<EditText>(R.id.txt_respuesta2) // Confirmar Contraseña
        val btnActContra = findViewById<Button>(R.id.buttonRegistrar)

        // Lógica del botón Volver (Se mantiene tu implementación original)
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
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // 3. NUEVA LÓGICA: Implementar el clic del botón Registrar
        btnActContra.setOnClickListener{
            cambiarContrasenia(txtNuevaContra.text.toString(), txtConfirmarContra.text.toString())
        }

    }

    // Función para manejar la lógica de actualización de la contraseña
    private fun cambiarContrasenia(nuevaContra: String, confirmarContra: String) {

        // Validación de campos vacíos
        if (nuevaContra.isEmpty() || confirmarContra.isEmpty()) {
            Toast.makeText(this, "Debe ingresar y confirmar la nueva contraseña.", Toast.LENGTH_LONG).show()
            return
        }

        // Validación de coincidencia
        if (nuevaContra != confirmarContra) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show()
            return
        }

        // Llamada a la API
        CoroutineScope(Dispatchers.Main).launch {
            val (success, message) = withContext(Dispatchers.IO) {
                llamarAPIActuContrasenia(idUsuario, nuevaContra)
            }

            Toast.makeText(this@Cambiar_contra, message, Toast.LENGTH_LONG).show()

            if (success) {
                // Navegar a la Activity Main al finalizar con éxito, cerrando las anteriores.
                val intent = Intent(this@Cambiar_contra, Main::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    // Función de red para la actualización
    private fun llamarAPIActuContrasenia(idUsuario: Int, nuevaContra: String): Pair<Boolean, String> {
        // !!! VERIFICAR ESTA RUTA EN TU SERVIDOR !!!
        val url = "${ApiConfig.BASE_URL}/consultas/cambiar_contrasenia.php"

        val requestBody = FormBody.Builder()
            .add("id_usuario", idUsuario.toString())
            .add("contrasenia", nuevaContra)
            .build()

        val request = Request.Builder().url(url).post(requestBody).build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    return Pair(false, "Error de red: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val success = json.optString("success") == "1"
                val mensaje = json.optString("mensaje", "Error desconocido al procesar.")

                Pair(success, mensaje)
            }
        } catch (e: IOException) {
            Log.e("Cambiar_contra", "Error de conexión: ${e.message}")
            Pair(false, "Error de conexión. Verifique su red.")
        } catch (e: Exception) {
            Log.e("Cambiar_contra", "Error al procesar la respuesta: ${e.message}")
            Pair(false, "Error al procesar la respuesta del servidor.")
        }
    }
}