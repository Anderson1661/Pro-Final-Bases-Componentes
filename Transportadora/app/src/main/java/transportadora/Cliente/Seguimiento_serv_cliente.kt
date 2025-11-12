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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seguimiento_serv_cliente)

        // 📋 Menú lateral
        findViewById<TextView>(R.id.editarperfil).setOnClickListener {
            startActivity(Intent(this, Perfil_cliente::class.java))
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
            startActivity(Intent(this, Principal_cliente::class.java))
        }
        findViewById<TextView>(R.id.menu2).setOnClickListener {
            scrollView.post { scrollView.smoothScrollTo(0, 0) }
        }
        findViewById<TextView>(R.id.menu3).setOnClickListener {
            startActivity(Intent(this, Historial_serv_cliente::class.java))
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

            layoutDatos.visibility = View.VISIBLE
            scrollView.post { scrollView.smoothScrollTo(0, layoutDatos.top) }

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

                                /*
                                val idClienteServidor = datos.optString("id_cliente")
                                val userId = intent.getIntExtra("USER_ID", -1)
                                if (userId == -1) {
                                    Toast.makeText(this, "Error: No se recibió el ID del cliente.", Toast.LENGTH_LONG).show()
                                    return
                                }

                                // 🔍 Comprobamos si la ruta pertenece al usuario logueado
                                if (idClienteServidor != userId.toString()) {
                                    Toast.makeText(this@Seguimiento_serv_cliente, "⚠️ Este servicio no pertenece a tu cuenta.", Toast.LENGTH_LONG).show()
                                    layoutDatos.visibility = View.GONE
                                    return@withContext
                                }
                                */
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
                                Toast.makeText(this@Seguimiento_serv_cliente, mensaje, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@Seguimiento_serv_cliente, "Error al procesar JSON: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Seguimiento_serv_cliente, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
