package transportadora.Compartido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import transportadora.Administrador.Principal_administrador
import transportadora.Cliente.Principal_cliente
import transportadora.Conductor.Principal_conductor
import transportadora.Conductor.Registrar_conductor
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R

class Login : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        txtVolverLogin.setOnClickListener { finish() }

        val txtOlvidarcontra = findViewById<TextView>(R.id.txt_olvidar_contra)
        txtOlvidarcontra.setOnClickListener {
            startActivity(Intent(this, Preg_seguridad::class.java))
        }

        val botonIngresar = findViewById<Button>(R.id.boton_ingresar_login)
        val txtCorreo = findViewById<EditText>(R.id.txt_correo_login)
        val txtContra = findViewById<EditText>(R.id.txt_contra_login)

        botonIngresar.setOnClickListener {
            val correoIngresado = txtCorreo.text.toString().trim()
            val contraIngresada = txtContra.text.toString().trim()

            if (correoIngresado.isEmpty() || contraIngresada.isEmpty()) {
                Toast.makeText(this, "Debe ingresar correo y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                autenticarUsuario(correoIngresado, contraIngresada)
            }
        }

        val txtRegistrar = findViewById<TextView>(R.id.txt_registrar_login)
        txtRegistrar.setOnClickListener {
            startActivity(Intent(this, Registrar1::class.java))
        }

        val txtRegistrarConductor = findViewById<TextView>(R.id.txt_registrar_conductor)
        txtRegistrarConductor.setOnClickListener {
            startActivity(Intent(this, Registrar_conductor::class.java))
        }
    }

    private fun autenticarUsuario(correo: String, contrasenia: String) {
        val url = ApiConfig.BASE_URL + "consultas/login.php"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val parametros = "correo=$correo&contrasenia=$contrasenia"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.outputStream.write(parametros.toByteArray(Charsets.UTF_8))

                val response = connection.inputStream.bufferedReader().use { it.readText() }

                withContext(Dispatchers.Main) {
                    val json = JSONObject(response)

                    if (json.getBoolean("success")) {
                        val tipoUsuario = json.getInt("id_tipo_usuario")
                        val idUsuario = json.getInt("id_usuario") // aquí obtenemos el ID

                        // Guardar correo e id_usuario en SharedPreferences
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("user_email", correo)
                            putInt("user_id", idUsuario) // guardamos el ID
                            apply()
                        }

                        // Navegar según el tipo de usuario
                        val intent = when (tipoUsuario) {
                            1 -> Intent(this@Login, Principal_administrador::class.java)
                            2 -> Intent(this@Login, Principal_conductor::class.java)
                            3 -> Intent(this@Login, Principal_cliente::class.java)
                            else -> null
                        }

                        if (intent != null) {
                            startActivity(intent)
                            Toast.makeText(this@Login, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Login, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@Login, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
