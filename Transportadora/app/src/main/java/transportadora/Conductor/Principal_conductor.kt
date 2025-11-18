package transportadora.Conductor

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import transportadora.Conductor.Perfil_conductor
import transportadora.Almacenados.Conductor.Servicios_conductor_almacenados
import transportadora.Login.R
import transportadora.Modelos.Conductor.ConductorData // Importación del modelo

class Principal_conductor : AppCompatActivity() {
    private lateinit var serviciosAdapter: ServiciosAdapter
    private lateinit var userEmail: String
    private var idConductor: Int? = null

    // Función que encapsula la lógica de recarga para poder reusarla
    // Función que encapsula la lógica de recarga para poder reusarla
    private fun loadServicios(email: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Obtener datos del conductor (ID y CP)
                val conductorData = withContext(Dispatchers.IO) {
                    Servicios_conductor_almacenados.obtenerDatosConductor(email)
                }

                if (conductorData == null) {
                    Toast.makeText(this@Principal_conductor, "Error: No se pudo obtener el ID/CP del conductor.", Toast.LENGTH_LONG).show()
                    serviciosAdapter.updateData(emptyList())
                    return@launch
                }
                idConductor = conductorData.idConductor

                // Obtener servicios - ✅ AHORA PASAMOS EL idConductor
                val servicios = withContext(Dispatchers.IO) {
                    Servicios_conductor_almacenados.obtenerServiciosConductor(email, idConductor) // ← AÑADIR idConductor
                }

                if (servicios.isNotEmpty()) {
                    serviciosAdapter.updateData(servicios)
                    // Opcional: mostrar cuántos servicios se encontraron
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_conductor)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email", null)

        if (email == null) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        userEmail = email

        // <<-- IMPLEMENTACIÓN DE LA LÓGICA DE ACCIÓN DE BOTONES
        val serviceActionListener: (idRuta: Int, nuevoEstadoId: Int, position: Int) -> Unit =
            { idRuta, nuevoEstadoId, position ->

                // Validación para la acción ACEPTAR
                if (nuevoEstadoId == 2 && idConductor == null) {
                    Toast.makeText(this@Principal_conductor, "Error: El ID del conductor no se ha cargado. Intente recargar la aplicación.", Toast.LENGTH_LONG).show()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Pasar el ID del conductor solo si el estado es 2 (Aceptar)
                        val conductorIdParam = if (nuevoEstadoId == 2) idConductor else null

                        val success = withContext(Dispatchers.IO) {
                            Servicios_conductor_almacenados.actualizarEstadoServicio(idRuta, nuevoEstadoId, conductorIdParam)
                        }

                        if (success) {
                            if (nuevoEstadoId == 2) { // Estado "En proceso" (Aceptado)
                                // Obtener detalles del servicio (teléfonos y pasajeros)
                                val (telefonos, pasajeros) = withContext(Dispatchers.IO) {
                                    Servicios_conductor_almacenados.obtenerDetallesServicio(idRuta)
                                }

                                // Actualizar el servicio en el adapter con los detalles
                                val servicio = serviciosAdapter.getItem(position)
                                servicio.telefonos_cliente = telefonos
                                servicio.nombres_pasajeros = pasajeros

                                Toast.makeText(this@Principal_conductor, "Servicio ACEPTADO.", Toast.LENGTH_SHORT).show()
                                loadServicios(userEmail) // Recargar para reflejar cambios

                            } else if (nuevoEstadoId == 3) { // Estado "Finalizado"
                                Toast.makeText(this@Principal_conductor, "Servicio FINALIZADO con éxito.", Toast.LENGTH_SHORT).show()
                                loadServicios(userEmail)
                            }
                        } else {
                            Toast.makeText(this@Principal_conductor, "Error al actualizar servicio.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        // FIN IMPLEMENTACIÓN DE LA LÓGICA DE ACCIÓN DE BOTONES -->>

        // 1. Configurar RecyclerView
        val recyclerServicios = findViewById<RecyclerView>(R.id.recyclerServicios_hoy)
        recyclerServicios.layoutManager = LinearLayoutManager(this)
        // Se inicializa el adapter con el listener
        serviciosAdapter = ServiciosAdapter(emptyList(), serviceActionListener)
        recyclerServicios.adapter = serviciosAdapter

        // 2. Cargar Datos (usando la nueva función)
        loadServicios(userEmail)

        //Menu lateral (sin cambios)
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
            // Pasar el correo del conductor al Activity de historial
            intent.putExtra("USER_EMAIL", userEmail ?: "")
            startActivity(intent)
            // Pasar el correo del conductor al Activity de historial
            intent.putExtra("USER_EMAIL", userEmail ?: "")
        }
    }
}