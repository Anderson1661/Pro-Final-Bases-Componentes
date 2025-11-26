package transportadora.Administrador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Cliente.Perfil_cliente
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import java.net.HttpURLConnection
import java.net.URL

class Principal_administrador : AppCompatActivity() {

    private val TAG = "PrincipalAdmin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_administrador)

        setupMenuButtons()
    }

    private fun setupMenuButtons(){
        //Menu lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_administrador::class.java)
            startActivity(intent)
        }
        val txtcambiarcontra = findViewById<TextView>(R.id.cambiocontra)
        txtcambiarcontra.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Compartido.Preg_seguridad::class.java
            )
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent =
                Intent(this@Principal_administrador, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent =
                Intent(this@Principal_administrador, transportadora.Compartido.Ayuda::class.java)
            startActivity(intent)
        }


        //Botones actualizar
        val btn_actualizar1=findViewById<Button>(R.id.btn_actualizar1)
        btn_actualizar1.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val resultado = withContext(Dispatchers.IO) {
                        ejecutarActualizacion_resumen_mensual()
                    }

                    if (resultado) {
                        Toast.makeText(this@Principal_administrador, "Resumen Mensual Actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Principal_administrador, "Error al actualizar datos", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Principal_administrador, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        val btn_actualizar2=findViewById<Button>(R.id.btn_actualizar2)
        btn_actualizar2.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val resultado = withContext(Dispatchers.IO) {
                        ejecutarActualizacion_estadisticas_conductores()
                    }

                    if (resultado) {
                        Toast.makeText(this@Principal_administrador, "Estadisticas de Conductores Actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Principal_administrador, "Error al actualizar datos", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Principal_administrador, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        //Botones reportes
        val btn_reporte1 = findViewById<Button>(R.id.btn_reporte1)
        btn_reporte1.setOnClickListener {
            val intent = Intent(this, Reporte1_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte2 = findViewById<Button>(R.id.btn_reporte2)
        btn_reporte2.setOnClickListener {
            val intent = Intent(this, Reporte2_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte3 = findViewById<Button>(R.id.btn_reporte3)
        btn_reporte3.setOnClickListener {
            val intent = Intent(this, Reporte3_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte4 = findViewById<Button>(R.id.btn_reporte4)
        btn_reporte4.setOnClickListener {
            val intent = Intent(this, Reporte4_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte5 = findViewById<Button>(R.id.btn_reporte5)
        btn_reporte5.setOnClickListener {
            val intent = Intent(this, Reporte5_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte6 = findViewById<Button>(R.id.btn_reporte6)
        btn_reporte6.setOnClickListener {
            val intent = Intent(this, Reporte6_admin::class.java)
            startActivity(intent)
        }
        val btn_reporte7 = findViewById<Button>(R.id.btn_reporte7)
        btn_reporte7.setOnClickListener {
            val intent = Intent(this, Reporte7_admin::class.java)
            startActivity(intent)
        }


        //Botones adminsitrar cruds
        val btn_administradores = findViewById<Button>(R.id.btn_administradores)
        btn_administradores.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Administradores.Administrar_administradores::class.java
            )
            startActivity(intent)
        }
        val btn_cat_servicio = findViewById<Button>(R.id.btn_cat_servicio)
        btn_cat_servicio.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Categorias_servicio.Administrar_categoria_servicio::class.java
            )
            startActivity(intent)
        }
        val btn_clientes = findViewById<Button>(R.id.btn_clientes)
        btn_clientes.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Clientes.Administrar_clientes::class.java
            )
            startActivity(intent)
        }
        val btn_cod_postal = findViewById<Button>(R.id.btn_cod_postal)
        btn_cod_postal.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Codigos_postales.Administrar_codigos_postales::class.java
            )
            startActivity(intent)
        }
        val btn_colores = findViewById<Button>(R.id.btn_colores)
        btn_colores.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Colores_vehiculo.Administrar_colores_vehiculo::class.java
            )
            startActivity(intent)
        }
        val btn_conductores = findViewById<Button>(R.id.btn_conductores)
        btn_conductores.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Conductores.Administrar_conductores::class.java
            )
            startActivity(intent)
        }
        val btn_est_conductor = findViewById<Button>(R.id.btn_est_conductor)
        btn_est_conductor.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Estados_conductor.Administrar_estados_conductor::class.java
            )
            startActivity(intent)
        }
        val btn_est_servicio = findViewById<Button>(R.id.btn_est_servicio)
        btn_est_servicio.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Estados_servicio.Administrar_estados_servicio::class.java
            )
            startActivity(intent)
        }
        val btn_est_vehiculo = findViewById<Button>(R.id.btn_est_vehiculo)
        btn_est_vehiculo.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Estados_vehiculo.Administrar_estados_vehiculo::class.java
            )
            startActivity(intent)
        }
        val btn_generos = findViewById<Button>(R.id.btn_generos)
        btn_generos.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Generos.Administrar_generos::class.java
            )
            startActivity(intent)
        }
        val btn_lineas = findViewById<Button>(R.id.btn_lineas)
        btn_lineas.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Lineas_vehiculo.Administrar_lineas_vehiculo::class.java
            )
            startActivity(intent)
        }
        val btn_marcas = findViewById<Button>(R.id.btn_marcas)
        btn_marcas.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Marcas_vehiculo.Administrar_marcas_vehiculo::class.java
            )
            startActivity(intent)
        }
        val btn_metodos = findViewById<Button>(R.id.btn_metodos)
        btn_metodos.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Metodos_pago.Administrar_metodos_pago::class.java
            )
            startActivity(intent)
        }
        val btn_paises = findViewById<Button>(R.id.btn_paises)
        btn_paises.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Paises.Administrar_paises::class.java
            )
            startActivity(intent)
        }
        val btn_pasajeros = findViewById<Button>(R.id.btn_pasajeros)
        btn_pasajeros.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Pasajeros_servicio.Administrar_pasajeros_servicio::class.java
            )
            startActivity(intent)
        }
        val btn_preguntas = findViewById<Button>(R.id.btn_preguntas)
        btn_preguntas.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Preguntas_seguridad.Administrar_preguntas_seguridad::class.java
            )
            startActivity(intent)
        }
        val btn_respuestas = findViewById<Button>(R.id.btn_respuestas)
        btn_respuestas.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Respuestas_seguridad.Administrar_respuestas_seguridad::class.java
            )
            startActivity(intent)
        }
        val btn_servicios = findViewById<Button>(R.id.btn_servicios)
        btn_servicios.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Servicios.Administrar_servicios::class.java
            )
            startActivity(intent)
        }
        val btn_tel_admin = findViewById<Button>(R.id.btn_tel_admin)
        btn_tel_admin.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tel_administradores.Administrar_tel_administradores::class.java
            )
            startActivity(intent)
        }
        val btn_tel_cliente = findViewById<Button>(R.id.btn_tel_cliente)
        btn_tel_cliente.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tel_clientes.Administrar_tel_clientes::class.java
            )
            startActivity(intent)
        }
        val btn_tel_condu = findViewById<Button>(R.id.btn_tel_condu)
        btn_tel_condu.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tel_conductores.Administrar_tel_conductores::class.java
            )
            startActivity(intent)
        }
        val btn_tipos_id = findViewById<Button>(R.id.btn_tipos_id)
        btn_tipos_id.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tipos_identificacion.Administrar_tipos_identificacion::class.java
            )
            startActivity(intent)
        }
        val btn_tipos_servicio = findViewById<Button>(R.id.btn_tipos_servicio)
        btn_tipos_servicio.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tipos_servicio.Administrar_tipos_servicio::class.java
            )
            startActivity(intent)
        }
        val btn_tipos_usuario = findViewById<Button>(R.id.btn_tipos_usuario)
        btn_tipos_usuario.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Tipos_usuario.Administrar_tipos_usuario::class.java
            )
            startActivity(intent)
        }
        val btn_usuarios = findViewById<Button>(R.id.btn_usuarios)
        btn_usuarios.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Usuarios.Administrar_usuarios::class.java
            )
            startActivity(intent)
        }
        val btn_vehiculos = findViewById<Button>(R.id.btn_vehiculos)
        btn_vehiculos.setOnClickListener {
            val intent = Intent(
                this@Principal_administrador,
                transportadora.Administrador.Vehiculos.Administrar_vehiculos::class.java
            )
            startActivity(intent)
        }
    }
    private suspend fun ejecutarActualizacion_resumen_mensual(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/administrador/actualizar/actualizar_resumen_mensual.php")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET" // o "POST" si necesitas

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                // Si tu PHP retorna JSON, puedes parsearlo:
                val json = JSONObject(response)
                return@withContext json.optString("success") == "1"
            }
            return@withContext false
        } catch (e: Exception) {
            return@withContext false
        }
    }

    private suspend fun ejecutarActualizacion_estadisticas_conductores(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/administrador/actualizar/actualizar_estadisticas_conductores.php")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET" // o "POST" si necesitas

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                // Si tu PHP retorna JSON, puedes parsearlo:
                val json = JSONObject(response)
                return@withContext json.optString("success") == "1"
            }
            return@withContext false
        } catch (e: Exception) {
            return@withContext false
        }
    }
}
