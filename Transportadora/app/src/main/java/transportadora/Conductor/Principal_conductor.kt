package transportadora.Conductor

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import transportadora.Almacenados.Conductor.Estado_conductor_almacenados
import transportadora.Almacenados.Conductor.Servicios_conductor_almacenados
import transportadora.Login.R
import transportadora.Modelos.Conductor.ConductorData

class Principal_conductor : AppCompatActivity() {
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var userEmail: String
    private var idConductor: Int? = null
    private lateinit var switchEstado: Switch
    private lateinit var txtEstadoActual: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_conductor)

        // Inicializar vistas del estado
        switchEstado = findViewById(R.id.switch_estado_conductor)
        txtEstadoActual = findViewById(R.id.txt_estado_actual)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email", null)

        if (email == null) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        userEmail = email

        // 1. Configurar RecyclerView
        val recyclerServicios = findViewById<RecyclerView>(R.id.recyclerServicios_hoy)
        recyclerServicios.layoutManager = LinearLayoutManager(this)

        val serviceActionListener: (idRuta: Int, nuevoEstadoId: Int, position: Int) -> Unit =
            { idRuta, nuevoEstadoId, position ->
                if (nuevoEstadoId == 2 && idConductor == null) {
                    Toast.makeText(this@Principal_conductor, "Error: El ID del conductor no se ha cargado. Intente recargar la aplicación.", Toast.LENGTH_LONG).show()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val conductorIdParam = if (nuevoEstadoId == 2) idConductor else null
                        val success = withContext(Dispatchers.IO) {
                            Servicios_conductor_almacenados.actualizarEstadoServicio(idRuta, nuevoEstadoId, conductorIdParam)
                        }

                        if (success) {
                            if (nuevoEstadoId == 2) {
                                val (telefonos, pasajeros) = withContext(Dispatchers.IO) {
                                    Servicios_conductor_almacenados.obtenerDetallesServicio(idRuta)
                                }
                                val servicio = serviciosAdapter.getItem(position)
                                servicio.telefonos_cliente = telefonos
                                servicio.nombres_pasajeros = pasajeros
                                Toast.makeText(this@Principal_conductor, "Servicio ACEPTADO.", Toast.LENGTH_SHORT).show()
                                // Recargar servicios si está conectado
                                if (switchEstado.isChecked) {
                                    loadServicios()
                                }
                            } else if (nuevoEstadoId == 3) {
                                Toast.makeText(this@Principal_conductor, "Servicio FINALIZADO con éxito.", Toast.LENGTH_SHORT).show()
                                // Recargar servicios si está conectado
                                if (switchEstado.isChecked) {
                                    loadServicios()
                                }
                            }
                        } else {
                            Toast.makeText(this@Principal_conductor, "Error al actualizar servicio.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        serviciosAdapter = ServiciosAdapter(emptyList(), serviceActionListener)
        recyclerServicios.adapter = serviciosAdapter

        // Cargar datos del conductor y estado inicial
        cargarDatosConductorYEstado()

        // Menu lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_conductor::class.java)
            startActivity(intent)
        }
        val txtcambiarcontra = findViewById<TextView>(R.id.cambiocontra)
        txtcambiarcontra.setOnClickListener {
            val intent = Intent(this@Principal_conductor, transportadora.Compartido.Preg_seguridad::class.java)
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent = Intent(this@Principal_conductor, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent = Intent(this@Principal_conductor, transportadora.Compartido.Ayuda::class.java)
            startActivity(intent)
        }
        val txthistorialpagos = findViewById<TextView>(R.id.menu3)
        txthistorialpagos.setOnClickListener {
            val intent = Intent(this@Principal_conductor, transportadora.Conductor.Historial_serv_conductor::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }

    private fun cargarDatosConductorYEstado() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener datos del conductor
                val conductorData = withContext(Dispatchers.IO) {
                    Servicios_conductor_almacenados.obtenerDatosConductor(userEmail)
                }

                if (conductorData == null) {
                    Toast.makeText(this@Principal_conductor, "Error: No se pudo obtener los datos del conductor.", Toast.LENGTH_LONG).show()
                    return@launch
                }

                idConductor = conductorData.idConductor

                // Actualizar tipo de servicio en UI
                findViewById<TextView>(R.id.txt_tipo_servicio).text = when(conductorData.idTipoServicio) {
                    1 -> "Alimentos"
                    2 -> "Pasajeros"
                    3 -> "Pasajeros y alimentos"
                    else -> "Desconocido"
                }

                // Obtener estado actual del conductor
                val estadoConductor = withContext(Dispatchers.IO) {
                    Estado_conductor_almacenados.obtenerEstadoConductor(conductorData.idConductor)
                }

                if (estadoConductor != null) {
                    // Configurar el switch según el estado actual
                    val isConectado = estadoConductor.id_estado_conductor == 1 // 1 = Conectado, 2 = Desconectado
                    switchEstado.isChecked = isConectado
                    txtEstadoActual.text = if (isConectado) "Conectado" else "Desconectado"

                    // Si está conectado, cargar servicios
                    if (isConectado) {
                        loadServicios()
                    }
                }

                // Configurar listener del switch
                switchEstado.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (idConductor != null) {
                        val nuevoEstadoId = if (isChecked) 1 else 2 // 1 = Conectado, 2 = Desconectado
                        actualizarEstadoEnServidor(nuevoEstadoId)
                    } else {
                        Toast.makeText(this@Principal_conductor, "Error: ID del conductor no disponible", Toast.LENGTH_SHORT).show()
                        switchEstado.isChecked = !isChecked // Revertir el cambio
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@Principal_conductor, "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun actualizarEstadoEnServidor(nuevoEstadoId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (idConductor != null) {
                    val success = withContext(Dispatchers.IO) {
                        Estado_conductor_almacenados.actualizarEstadoConductor(idConductor!!, nuevoEstadoId)
                    }

                    if (success) {
                        val nuevoEstado = if (nuevoEstadoId == 1) "Conectado" else "Desconectado"
                        txtEstadoActual.text = nuevoEstado
                        Toast.makeText(this@Principal_conductor, "Estado actualizado: $nuevoEstado", Toast.LENGTH_SHORT).show()

                        // Si se conectó, cargar servicios; si se desconectó, limpiar lista
                        if (nuevoEstadoId == 1) {
                            loadServicios()
                        } else {
                            serviciosAdapter.updateData(emptyList())
                            Toast.makeText(this@Principal_conductor, "Modo desconectado - No se mostrarán servicios", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Principal_conductor, "Error al actualizar estado", Toast.LENGTH_SHORT).show()
                        // Revertir el switch
                        switchEstado.isChecked = !switchEstado.isChecked
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_conductor, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                // Revertir el switch
                switchEstado.isChecked = !switchEstado.isChecked
            }
        }
    }

    private fun loadServicios() {
        // Solo cargar servicios si está conectado
        if (!switchEstado.isChecked) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val conductorData = withContext(Dispatchers.IO) {
                    Servicios_conductor_almacenados.obtenerDatosConductor(userEmail)
                }

                if (conductorData == null) {
                    Toast.makeText(this@Principal_conductor, "Error: No se pudo obtener los datos del conductor.", Toast.LENGTH_LONG).show()
                    serviciosAdapter.updateData(emptyList())
                    return@launch
                }

                idConductor = conductorData.idConductor
                val idTipoServicio = conductorData.idTipoServicio

                // Obtener servicios
                val servicios = withContext(Dispatchers.IO) {
                    Servicios_conductor_almacenados.obtenerServiciosConductor(userEmail, idConductor, idTipoServicio)
                }

                if (servicios.isNotEmpty()) {
                    serviciosAdapter.updateData(servicios)
                    Toast.makeText(this@Principal_conductor, "Se encontraron ${servicios.size} servicios", Toast.LENGTH_SHORT).show()
                } else {
                    serviciosAdapter.updateData(emptyList())
                    Toast.makeText(this@Principal_conductor, "Aún no hay reservas en tu ciudad o servicios en curso para hoy.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Principal_conductor, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}