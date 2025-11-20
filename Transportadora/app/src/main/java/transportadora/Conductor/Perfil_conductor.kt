package transportadora.Conductor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import transportadora.Almacenados.Conductor.Perfil_conductor_completo_almacenados
import transportadora.Compartido.Preg_seguridad
import transportadora.Login.R
import com.bumptech.glide.Glide
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target

class Perfil_conductor : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_conductor)

        //region Inicialización de Vistas
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_login)
        val botonactualizarfoto = findViewById<Button>(R.id.boton_act_foto)

        val botonactualizar = findViewById<Button>(R.id.boton_act_datos)
        val botoncontra = findViewById<Button>(R.id.boton_cambiar_contra)
        val botonpreguntas = findViewById<Button>(R.id.boton_preguntas_seg)

        val txtTipoId = findViewById<TextView>(R.id.txt_tipo_id)
        val txtIdentificacion = findViewById<TextView>(R.id.txt_identificacion)
        val txtNombre = findViewById<TextView>(R.id.txt_nombre)
        val txtGenero = findViewById<TextView>(R.id.txt_genero)
        val txtTel1 = findViewById<TextView>(R.id.txt_tel1)
        val txtTel2 = findViewById<TextView>(R.id.txt_tel2)
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

        val imgPerfil = findViewById<ImageView>(R.id.imgPerfil) // En lugar de CircleImageView

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
                        // Justo después de obtener el perfil
                        Log.d("Perfil_conductor", "=== DEBUG PERFIL ===")
                        Log.d("Perfil_conductor", "Nombre: ${perfil.nombre}")
                        Log.d("Perfil_conductor", "Correo: ${perfil.correo}")
                        Log.d("Perfil_conductor", "URL Foto: ${perfil.url_foto}")
                        Log.d("Perfil_conductor", "¿URL vacía?: ${perfil.url_foto.isNullOrEmpty()}")
                        Log.d("Perfil_conductor", "¿URL null?: ${perfil.url_foto == null}")
                        Log.d("Perfil_conductor", "=== FIN DEBUG ===")

                        // Datos personales
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

                        // Teléfonos
                        txtTel1.text = perfil.telefonos.getOrNull(0) ?: "No registrado"
                        txtTel2.text = perfil.telefonos.getOrNull(1) ?: "No registrado"

                        // Datos del vehículo
                        txtPlaca.text = perfil.placa
                        txtLinea.text = perfil.linea_vehiculo
                        txtModelo.text = perfil.modelo.toString()
                        txtColor.text = perfil.color
                        txtMarca.text = perfil.marca
                        txtTipoServicio.text = perfil.tipo_servicio
                        txtEstadoVehiculo.text = perfil.estado_vehiculo

                        CoroutineScope(Dispatchers.Main).launch {
                            val esUrlValida = verificarUrlImagen(perfil.url_foto)
                            Log.d("Perfil_conductor", "URL accesible: $esUrlValida")

                            if (esUrlValida) {
                                // Cargar con Glide...
                            } else {
                                imgPerfil.setImageResource(R.drawable.fondo_main)
                            }
                        }

                        if (tieneConexionInternet()) {
                            // Cargar imagen de perfil
                            if (!perfil.url_foto.isNullOrEmpty() && perfil.url_foto != "null") {
                                Log.d(
                                    "Perfil_conductor",
                                    "Intentando cargar con Picasso: ${perfil.url_foto}"
                                )

                                try {
                                    Picasso.get()
                                        .load(perfil.url_foto)
                                        .placeholder(R.drawable.fondo_main)
                                        .error(R.drawable.fondo_main)
                                        .networkPolicy(com.squareup.picasso.NetworkPolicy.NO_CACHE) // Forzar red
                                        .memoryPolicy(com.squareup.picasso.MemoryPolicy.NO_CACHE)   // Forzar red
                                        .into(imgPerfil, object : com.squareup.picasso.Callback {
                                            override fun onSuccess() {
                                                Log.d(
                                                    "Perfil_conductor",
                                                    "✅ Picasso cargó la imagen exitosamente"
                                                )
                                            }

                                            override fun onError(e: Exception?) {
                                                Log.e(
                                                    "Perfil_conductor",
                                                    "❌ Picasso error: ${e?.message}"
                                                )
                                                // Intentar cargar desde caché como fallback
                                                Picasso.get()
                                                    .load(perfil.url_foto)
                                                    .into(imgPerfil)
                                            }
                                        })
                                } catch (e: Exception) {
                                    Log.e(
                                        "Perfil_conductor",
                                        "❌ Excepción en Picasso: ${e.message}"
                                    )
                                    imgPerfil.setImageResource(R.drawable.fondo_main)
                                }
                            } else {
                                Log.w("Perfil_conductor", "URL de foto no válida")
                                imgPerfil.setImageResource(R.drawable.fondo_main)
                            }
                        }
                     else {
                        Log.e("Perfil_conductor", "❌ No hay conexión a internet")
                        imgPerfil.setImageResource(R.drawable.fondo_main)
                        Toast.makeText(this@Perfil_conductor, "Sin conexión a internet", Toast.LENGTH_SHORT).show()
                    }


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

        botonactualizarfoto.setOnClickListener {
            val intent = Intent(this, Act_foto_conductor::class.java)

            // Obtener el correo de SharedPreferences
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("user_email", null)

            if (userEmail != null) {
                // Buscar la URL actual en el perfil cargado
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val perfil = withContext(Dispatchers.IO) {
                            Perfil_conductor_completo_almacenados.obtenerPerfilCompleto(userEmail)
                        }
                        if (perfil != null) {
                            // Enviar tanto la URL actual como el correo
                            intent.putExtra("CURRENT_PHOTO_URL", perfil.url_foto)
                            intent.putExtra("USER_EMAIL", userEmail) // ← AÑADIR ESTA LÍNEA
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Enviar al menos el correo incluso si hay error
                        intent.putExtra("USER_EMAIL", userEmail)
                        startActivity(intent)
                    }
                }
            } else {
                startActivity(intent)
            }
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

    private suspend fun verificarUrlImagen(url: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            val responseCode = connection.responseCode
            connection.disconnect()
            responseCode == 200
        } catch (e: Exception) {
            Log.e("Perfil_conductor", "Error verificando URL: ${e.message}")
            false
        }
    }
    private fun tieneConexionInternet(): Boolean {
        return try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (e: Exception) {
            false
        }
    }
}