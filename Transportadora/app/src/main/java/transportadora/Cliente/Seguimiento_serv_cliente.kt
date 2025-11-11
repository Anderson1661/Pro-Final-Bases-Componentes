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
                    val url = URL("${ApiConfig.BASE_URL}consultas/cliente/ruta/consultar_ruta.php")
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

                                // 🔄 Convertir JSON en modelo Ruta
                                val ruta = Ruta(
                                    id_ruta = datos.optString("id_ruta"),
                                    direccion_origen = datos.optString("direccion_origen"),
                                    direccion_destino = datos.optString("direccion_destino"),
                                    id_codigo_postal_origen = datos.optString("id_codigo_postal_origen"),
                                    id_codigo_postal_destino = datos.optString("id_codigo_postal_destino"),
                                    distancia_km = datos.optString("distancia_km"),
                                    fecha_hora_reserva = datos.optString("fecha_hora_reserva"),
                                    fecha_hora_origen = datos.optString("fecha_hora_origen"),
                                    fecha_hora_destino = datos.optString("fecha_hora_destino"),
                                    id_conductor = datos.optString("id_conductor"),
                                    id_tipo_servicio = datos.optString("id_tipo_servicio"),
                                    id_cliente = datos.optString("id_cliente"),
                                    id_estado_servicio = datos.optString("id_estado_servicio"),
                                    id_categoria_servicio = datos.optString("id_categoria_servicio"),
                                    id_metodo_pago = datos.optString("id_metodo_pago"),
                                    total = datos.optString("total"),
                                    pago_conductor = datos.optString("pago_conductor")
                                )

                                // 🧾 Mostrar datos en pantalla
                                txtFechaReserva.text = ruta.fecha_hora_reserva
                                txtEstadoEnvio.text = "Pendiente"
                                txtDirOrigen.text = ruta.direccion_origen
                                txtPaisOrigen.text = "Colombia"
                                txtDepOrigen.text = datos.optString("departamento_origen")
                                txtCiuOrigen.text = datos.optString("ciudad_origen")
                                txtFechaInicio.text = ruta.fecha_hora_origen
                                txtTipoServ.text = datos.optString("tipo_servicio")
                                txtCantPasajeros.text = datos.optString("cantidad_pasajeros")
                                txtNomPas1.text = datos.optString("nombre_pasajero1")
                                txtNomPas2.text = datos.optString("nombre_pasajero2")
                                txtNomPas3.text = datos.optString("nombre_pasajero3")
                                txtNomPas4.text = datos.optString("nombre_pasajero4")
                                txtCategoria.text = datos.optString("categoria_servicio")
                                txtDirDestino.text = ruta.direccion_destino
                                txtPaisDestino.text = "Colombia"
                                txtDepDestino.text = datos.optString("departamento_destino")
                                txtCiuDestino.text = datos.optString("ciudad_destino")
                                txtPrecioKm.text = datos.optString("valor_km")
                                txtKilometros.text = ruta.distancia_km
                                txtTotalPagado.text = ruta.total
                                txtMetodoPago.text = datos.optString("metodo_pago")
                                txtFechaEntrega.text = ruta.fecha_hora_destino
                                txtConductor.text = datos.optString("nombre_conductor")
                                txtPlaca.text = datos.optString("placa")

                            } else {
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
