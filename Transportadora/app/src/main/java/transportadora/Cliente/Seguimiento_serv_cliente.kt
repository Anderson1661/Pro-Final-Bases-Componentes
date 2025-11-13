package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ruta
import java.net.HttpURLConnection
import java.net.URL
import android.widget.Toast;

class Seguimiento_serv_cliente : AppCompatActivity() {
    private var id_cliente_actual: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seguimiento_serv_cliente)

        // Seguimiento_serv_cliente.kt (Añadir al inicio de onCreate, cerca de la línea 24)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", null)

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "No se pudo identificar el correo del usuario.", Toast.LENGTH_LONG).show()
        } else {
            // 1. Obtener el id_cliente por correo (nueva lógica)
            CoroutineScope(Dispatchers.Main).launch {
                id_cliente_actual = obtenerIdClientePorCorreo(userEmail)
                if (id_cliente_actual == -1) {
                    Toast.makeText(this@Seguimiento_serv_cliente, "Error: No se pudo obtener el ID del cliente. Verifica tu sesión.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 📋 Menú lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
            // AÑADIR: Pasamos el email del usuario
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.cambiocontra).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Preg_seguridad::class.java))
        }
        findViewById<TextView>(R.id.cerrarsesion).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Main::class.java))
        }
        findViewById<TextView>(R.id.ayuda).setOnClickListener {
            startActivity(Intent(this, transportadora.Compartido.Ayuda::class.java))
        }

        // 📱 Menú inferior
        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
        findViewById<TextView>(R.id.menu1).setOnClickListener {
            val intent = Intent(this, Principal_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail) // <-- Se añade el email
            startActivity(intent)
        }
        findViewById<TextView>(R.id.menu2).setOnClickListener {
            scrollView.post { scrollView.smoothScrollTo(0, 0) }
        }
        findViewById<TextView>(R.id.menu3).setOnClickListener {
            val intent = Intent(this, Historial_serv_cliente::class.java)
            intent.putExtra("USER_EMAIL", userEmail) // <-- Se añade el email
            startActivity(intent)
        }

        // 🔍 Buscar Ruta
        val btnBuscar = findViewById<Button>(R.id.btn_buscar)
        val layoutDatos = findViewById<LinearLayout>(R.id.layout_datos_envio)
        val idRuta = findViewById<EditText>(R.id.txt_id_servicio)

        // 🧾 Referencias a los campos del layout donde se mostrarán los datos
        val txtFechaReserva = findViewById<TextView>(R.id.txt_fecha_reserva)
        val txtEstadoEnvio = findViewById<TextView>(R.id.txt_estado_envio)
        val txtDirOrigen = findViewById<TextView>(R.id.txt_dir_origen)
        val txtPaisOrigen = findViewById<TextView>(R.id.txt_pais_origen)
        val txtDepOrigen = findViewById<TextView>(R.id.txt_dep_origen)
        val txtCiuOrigen = findViewById<TextView>(R.id.txt_ciu_origen)
        val txtFechaInicio = findViewById<TextView>(R.id.txt_fehca_inicio)
        val txtTipoServ = findViewById<TextView>(R.id.txt_tipo_serv)
        val txtCantPasajeros = findViewById<TextView>(R.id.txt_cantidad_pasajeros)
        val txtNomPas1 = findViewById<TextView>(R.id.txt_nombre_pas1)
        val txtNomPas2 = findViewById<TextView>(R.id.txt_nombre_pas2)
        val txtNomPas3 = findViewById<TextView>(R.id.txt_nom_pas3)
        val txtNomPas4 = findViewById<TextView>(R.id.txt_nombre_pas4)
        val txtCategoria = findViewById<TextView>(R.id.txt_categoria)
        val txtDirDestino = findViewById<TextView>(R.id.txt_dir_destino)
        val txtPaisDestino = findViewById<TextView>(R.id.txt_pais_destino)
        val txtDepDestino = findViewById<TextView>(R.id.txt_dep_destino)
        val txtCiuDestino = findViewById<TextView>(R.id.txt_ciu_destino)
        val txtPrecioKm = findViewById<TextView>(R.id.txt_precio_km)
        val txtKilometros = findViewById<TextView>(R.id.txt_kilometros)
        val txtTotalPagado = findViewById<TextView>(R.id.txt_total_pagado)
        val txtMetodoPago = findViewById<TextView>(R.id.txt_metodo_pago)
        val txtFechaEntrega = findViewById<TextView>(R.id.txt_fecha_entrega)
        val txtConductor = findViewById<TextView>(R.id.txt_conductor)
        val txtPlaca = findViewById<TextView>(R.id.txt_placa)

        btnBuscar.setOnClickListener {
            val id_ruta = idRuta.text.toString().trim()
            if (id_ruta.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa el ID de la ruta", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // AÑADIDO: Ocultar el layout inmediatamente para limpiar la pantalla
            // mientras se hace la consulta.
            layoutDatos.visibility = View.GONE

            // 🧠 Consulta al servidor
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("${ApiConfig.BASE_URL}/consultas/cliente/ruta/consultar_ruta.php")
                    val params = "id_ruta=$id_ruta"

                    val connection = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        doOutput = true
                        setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                        outputStream.write(params.toByteArray(Charsets.UTF_8))
                    }

                    val response = connection.inputStream.bufferedReader().use { it.readText() }

                    withContext(Dispatchers.Main) {
                        try {
                            val json = JSONObject(response)
                            val success = json.optString("success")
                            val mensaje = json.optString("mensaje")

                            if (success == "1") {
                                val datos = json.getJSONObject("datos")
                                val idClienteServidor = datos.optString("id_cliente")

                                // 🔍 Comprobamos si la ruta pertenece al usuario logueado
                                if (idClienteServidor != id_cliente_actual.toString()) {
                                    val toast = Toast.makeText(
                                        this@Seguimiento_serv_cliente,
                                        "⚠️ Este servicio no pertenece a tu cuenta. ⚠️",
                                        Toast.LENGTH_LONG
                                    )
                                    val view = toast.view
                                    val text = view?.findViewById<TextView>(android.R.id.message)
                                    text?.textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    toast.show()
                                    layoutDatos.visibility = View.GONE // Importante: ocultar si no pertenece
                                    return@withContext
                                }

                                // ✅ SOLO AQUÍ HACEMOS VISIBLE EL LAYOUT Y HACEMOS SCROLL
                                layoutDatos.visibility = View.VISIBLE
                                scrollView.post { scrollView.smoothScrollTo(0, layoutDatos.top) }

                                // ✅ Si pertenece al usuario, mostramos los datos
                                fun limpiar(valor: String?): String {
                                    return if (valor.isNullOrEmpty() || valor == "null") "" else valor
                                }

                                txtFechaReserva.text = limpiar(datos.optString("fecha_reserva"))
                                txtEstadoEnvio.text = limpiar(datos.optString("estado"))
                                txtDirOrigen.text = limpiar(datos.optString("direccion_origen"))
                                txtPaisOrigen.text = limpiar(datos.optString("origen_pais"))
                                txtDepOrigen.text = limpiar(datos.optString("origen_departamento"))
                                txtCiuOrigen.text = limpiar(datos.optString("origen_ciudad"))
                                txtFechaInicio.text = limpiar(datos.optString("fecha_inicio"))
                                txtTipoServ.text = limpiar(datos.optString("tipo_servicio"))
                                txtCantPasajeros.text = limpiar(datos.optString("cantidad_pasajeros"))

                                txtNomPas1.text = limpiar(datos.optString("nombre_pasajero1"))
                                txtNomPas2.text = limpiar(datos.optString("nombre_pasajero2"))
                                txtNomPas3.text = limpiar(datos.optString("nombre_pasajero3"))
                                txtNomPas4.text = limpiar(datos.optString("nombre_pasajero4"))

                                txtCategoria.text = limpiar(datos.optString("categoria_servicio"))
                                txtDirDestino.text = limpiar(datos.optString("direccion_destino"))
                                txtPaisDestino.text = limpiar(datos.optString("destino_pais"))
                                txtDepDestino.text = limpiar(datos.optString("destino_departamento"))
                                txtCiuDestino.text = limpiar(datos.optString("destino_ciudad"))

                                val precioKm = limpiar(datos.optString("precio_km"))
                                val distanciaKm = limpiar(datos.optString("distancia_km"))
                                txtPrecioKm.text = if (precioKm.isNotEmpty()) "$$precioKm" else ""
                                txtKilometros.text = if (distanciaKm.isNotEmpty()) distanciaKm else ""

                                txtTotalPagado.text = limpiar(datos.optString("total"))
                                txtMetodoPago.text = limpiar(datos.optString("metodo_pago"))
                                txtFechaEntrega.text = limpiar(datos.optString("fecha_entrega"))
                                txtConductor.text = limpiar(datos.optString("nombre_conductor"))
                                txtPlaca.text = limpiar(datos.optString("placa_vehiculo"))

                                // 👇 Ocultamos labels vacíos (opcional)
                                val nombres = listOf(txtNomPas1, txtNomPas2, txtNomPas3, txtNomPas4)
                                nombres.forEach { it.visibility = if (it.text.isEmpty()) View.GONE else View.VISIBLE }
                            }
                            else {
                                // ❌ Si la ruta no existe o hubo otro error del servidor
                                layoutDatos.visibility = View.GONE // Aseguramos que quede oculto
                                Toast.makeText(this@Seguimiento_serv_cliente, mensaje, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@Seguimiento_serv_cliente, "Error al procesar JSON: ${e.message}", Toast.LENGTH_LONG).show()
                            layoutDatos.visibility = View.GONE // Ocultar en caso de error de JSON
                        }
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Seguimiento_serv_cliente, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        layoutDatos.visibility = View.GONE // Ocultar en caso de error de red
                    }
                }
            }
        }
    }

    private suspend fun obtenerIdClientePorCorreo(email: String): Int = withContext(Dispatchers.IO) {
        var idCliente = -1
        try {
            val url = java.net.URL(transportadora.Configuracion.ApiConfig.BASE_URL + "consultas/cliente/ruta/obtener_id_cliente_por_correo.php")
            val params = "correo=${java.net.URLEncoder.encode(email, "UTF-8")}"

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(Charsets.UTF_8))

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val json = org.json.JSONObject(response)
            if (json.getString("success") == "1") {
                // El campo se llama 'id_cliente' en el JSON de respuesta
                idCliente = json.getInt("id_cliente")
                android.util.Log.d("Principal_cliente", "ID Cliente obtenido: $idCliente")
            } else {
                android.util.Log.e("Principal_cliente", "Error PHP al obtener ID: ${json.getString("mensaje")}")
            }
        } catch (e: Exception) {
            android.util.Log.e("Principal_cliente", "Error de red/JSON al obtener ID: ${e.message}", e)
        }
        return@withContext idCliente
    }
}
