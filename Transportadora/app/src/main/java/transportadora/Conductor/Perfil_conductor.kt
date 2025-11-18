package transportadora.Conductor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.Conductor.Perfil_conductor_completo_almacenados
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R

class Perfil_conductor : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_conductor)

        //region Inicialización de Vistas
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)

        val txtTipoId = findViewById<TextView>(R.id.txt_tipo_id)
        val txtIdentificacion = findViewById<TextView>(R.id.txt_identificacion)
        val txtNombre = findViewById<TextView>(R.id.txt_nombre)
        val txtGenero = findViewById<TextView>(R.id.txt_genero)
        val txtNacionalidad = findViewById<TextView>(R.id.txt_nacionalidad)
        val txtCorreo = findViewById<TextView>(R.id.txt_correo)
        val txtDireccion = findViewById<TextView>(R.id.txt_direccion)
        val txtPais = findViewById<TextView>(R.id.txt_pais)
        val txtDepartamento = findViewById<TextView>(R.id.txt_departamento)
        val txtCiudad = findViewById<TextView>(R.id.txt_ciudad)
        // Nuevos campos para el vehículo
        val txtPlaca = findViewById<TextView>(R.id.txt_placa)
        val txtLinea = findViewById<TextView>(R.id.txt_linea)
        val txtModelo = findViewById<TextView>(R.id.txt_modelo)
        val txtColor = findViewById<TextView>(R.id.txt_color)
        val txtMarca = findViewById<TextView>(R.id.txt_marca)
        val txtTipoServicio = findViewById<TextView>(R.id.txt_tipo_servicio)
        val txtEstadoVehiculo = findViewById<TextView>(R.id.txt_estado_vehiculo)
        //endregion

        // Obtener el correo de SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val perfil = withContext(Dispatchers.IO) {
                        Perfil_conductor_completo_almacenados.obtenerPerfilCompleto(userEmail)
                    }
                    if (perfil != null) {
                        txtTipoId.text = perfil.tipo_identificacion
                        txtIdentificacion.text = perfil.identificacion
                        txtNombre.text = perfil.nombre
                        txtGenero.text = perfil.genero
                        txtCorreo.text = perfil.correo
                        txtDireccion.text = perfil.direccion
                        txtNacionalidad.text = perfil.nacionalidad
                        txtPais.text = perfil.pais_residencia
                        txtDepartamento.text = perfil.departamento
                        txtCiudad.text = perfil.ciudad
                        // Datos del vehículo
                        txtPlaca.text = perfil.placa
                        txtLinea.text = perfil.linea_vehiculo
                        txtModelo.text = perfil.modelo.toString()
                        txtColor.text = perfil.color
                        txtMarca.text = perfil.marca
                        txtTipoServicio.text = perfil.tipo_servicio
                        txtEstadoVehiculo.text = perfil.estado_vehiculo
                        // TODO: Cargar la imagen de perfil (url_foto) usando una librería como Glide o Picasso

                    } else {
                        Toast.makeText(this@Perfil_conductor, "No se pudo cargar el perfil", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@Perfil_conductor, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad si no hay email
        }

        //region Listeners de Botones
        txtVolverLogin.setOnClickListener {
            val intent = Intent(this@Perfil_conductor, Principal_conductor::class.java)
            startActivity(intent)
            finish()
        }

        botonactualizar.setOnClickListener {
            val intent = Intent(this, Act_perfil_conductor::class.java)
            startActivity(intent)
        }

        botoncontra.setOnClickListener {
            val intent = Intent(this@Perfil_conductor, Preg_seguridad::class.java)
            startActivity(intent)
        }

        botonpreguntas.setOnClickListener {
            val intent = Intent(this, Act_preguntas_conductor::class.java)
            startActivity(intent)
        }
        //endregion
    }
}