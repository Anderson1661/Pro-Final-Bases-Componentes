package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R
import kotlinx.coroutines.*
import transportadora.Almacenados.Perfil_cliente_completo_almacenados

class Perfil_cliente : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_cliente)

        //region Inicialización de Vistas
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)

        val txtTipoId = findViewById<TextView>(R.id.txt_tipo_id)
        val txtIdentificacion = findViewById<TextView>(R.id.txt_identificacion)
        val txtNombre = findViewById<TextView>(R.id.txt_nombre)
        val txtTel1 = findViewById<TextView>(R.id.txt_tel1)
        val txtTel2 = findViewById<TextView>(R.id.txt_tel2)
        val txtNacionalidad = findViewById<TextView>(R.id.txt_nacionalidad)
        val txtCorreo = findViewById<TextView>(R.id.txt_correo)
        val txtDireccion = findViewById<TextView>(R.id.txt_direccion)
        val txtPais = findViewById<TextView>(R.id.txt_pais)
        val txtDepartamento = findViewById<TextView>(R.id.txt_departamento)
        val txtCiudad = findViewById<TextView>(R.id.txt_ciudad)
        //endregion

        //region Lógica de Carga de Datos
        val userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val perfil = withContext(Dispatchers.IO) {
                        Perfil_cliente_completo_almacenados.obtenerPerfilCompleto(userEmail)
                    }
                    if (perfil != null) {
                        txtTipoId.text = perfil.tipo_identificacion
                        txtIdentificacion.text = perfil.identificacion
                        txtNombre.text = perfil.nombre
                        txtCorreo.text = perfil.correo
                        txtDireccion.text = perfil.direccion
                        txtNacionalidad.text = perfil.nacionalidad
                        txtPais.text = perfil.pais_residencia
                        txtDepartamento.text = perfil.departamento
                        txtCiudad.text = perfil.ciudad

                        txtTel1.text = perfil.telefonos.getOrNull(0) ?: "No registrado"
                        txtTel2.text = perfil.telefonos.getOrNull(1) ?: "No registrado"

                    } else {
                        Toast.makeText(this@Perfil_cliente, "No se pudo cargar el perfil", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Perfil_cliente, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad si no hay email
        }
        //endregion

        //region Listeners de Botones
        txtVolverLogin.setOnClickListener {
            finish()
        }

        botonactualizar.setOnClickListener {
            val intent = Intent(this, Act_perfil_cliente::class.java)
            startActivity(intent)
        }

        botoncontra.setOnClickListener {
            val intent = Intent(this@Perfil_cliente, Preg_seguridad::class.java)
            startActivity(intent)
        }

        botonpreguntas.setOnClickListener {
            val intent = Intent(this, Act_preguntas_cliente::class.java)
            startActivity(intent)
        }
        //endregion
    }
}