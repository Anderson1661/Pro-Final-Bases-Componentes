package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Cliente.Genero_almacen
import transportadora.Almacenados.Cliente.Pais_almacenados
import transportadora.Almacenados.Cliente.Perfil_cliente_completo_almacenados
import transportadora.Almacenados.Cliente.Tipo_identificacion_almacenado
import transportadora.Compartido.Registrar2
import transportadora.Login.R
import transportadora.Modelos.Cliente.Ciudad
import transportadora.Modelos.Cliente.Genero
import transportadora.Modelos.Cliente.Pais
import transportadora.Modelos.Cliente.Tipo_identificacion

class Act_perfil_cliente : AppCompatActivity() {
    private var listaPaisesCompleta: List<Pais> = emptyList()
    private var listatiposid: List<Tipo_identificacion> = emptyList()
    private var listagenero: List<Genero> = emptyList()
    private var departamentosOrigen: List<String> = emptyList()
    private var listaCiudadesOrigen: List<Ciudad> = emptyList()
    private var listaCiudadesDestino: List<Ciudad> = emptyList()
    private lateinit var spinner_ciudades1: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_perfil_cliente)

        // Asignar valores al spinner de tipos id
        val spinner_tipos_id = findViewById<Spinner>(R.id.txt_tipo_id)
        val spinner_paises = findViewById<Spinner>(R.id.txt_pais)
        val spinner_departamentos = findViewById<Spinner>(R.id.txt_departamento)
        val spinner_ciudades = findViewById<Spinner>(R.id.txt_ciudad)
        val spinner_nacionalidad = findViewById<Spinner>(R.id.txt_nacionalidad)
        val spinner_genero = findViewById<Spinner>(R.id.txt_genero)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val paises = withContext(Dispatchers.IO) { Pais_almacenados.obtenerPaises() }
                if (paises.isNotEmpty()) {
                    listaPaisesCompleta = paises
                    val listapaisesNombres = paises.map { it.nombre }
                    spinner_paises.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinner_nacionalidad.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listapaisesNombres).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error al cargar paises: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val generos = withContext(Dispatchers.IO) { Genero_almacen.obtener_generos() }
                if (generos.isNotEmpty()) {
                    listagenero = generos
                    val listageneros = generos.map { it.descripcion }
                    spinner_genero.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listageneros).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron generos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error al cargar generos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tipos_id = withContext(Dispatchers.IO) { Tipo_identificacion_almacenado.obtener_tipos_identificacion() }
                if (tipos_id.isNotEmpty()) {
                    listatiposid = tipos_id
                    val listatipos = tipos_id.map { it.descripcion }
                    spinner_tipos_id.adapter = ArrayAdapter(this@Act_perfil_cliente, android.R.layout.simple_spinner_item, listatipos).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                } else {
                    Toast.makeText(this@Act_perfil_cliente, "No se encontraron paises", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Act_perfil_cliente, "Error al cargar paises: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }




        val txtIdentificacion = findViewById<TextView>(R.id.txt_id)
        val txtNombre = findViewById<TextView>(R.id.txt_nombre)
        val txtTel1 = findViewById<TextView>(R.id.txt_tel1)
        val txtTel2 = findViewById<TextView>(R.id.txt_tel2)
        val txtCorreo = findViewById<TextView>(R.id.txt_email)
        val txtDireccion = findViewById<TextView>(R.id.txt_dir)

        val userEmail = intent.getStringExtra("USER_EMAIL")

        if (userEmail != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val perfil = withContext(Dispatchers.IO) {
                        transportadora.Almacenados.Cliente.Datos_perfil_almacenados.obtener_datos_perfil(userEmail)
                    }

                    if (perfil != null) {
                        // Asignar cada campo del modelo a su respectivo TextView
                        val tipoIdSeleccionado = perfil.tipo_identificacion
                        val listaDescripcionesId = listatiposid.map { it.descripcion }
                        val indexId = listaDescripcionesId.indexOf(tipoIdSeleccionado)

                        if (indexId != -1) {
                            // Selecciona la opción en el Spinner si se encontró el índice
                            spinner_tipos_id.setSelection(indexId)
                        }
                        txtIdentificacion.text = perfil.identificacion
                        txtNombre.text = perfil.nombre
                        txtCorreo.text = perfil.correo
                        txtDireccion.text = perfil.direccion

                        val generoSeleccionado = perfil.genero
                        val listaDescripcionesGenero = listagenero.map { it.descripcion }
                        val indexGenero = listaDescripcionesGenero.indexOf(generoSeleccionado)

                        if (indexGenero != -1) {
                            spinner_genero.setSelection(indexGenero)
                        }

                        val listaNombresPaises = listaPaisesCompleta.map { it.nombre }

                        val nacionalidadSeleccionada = perfil.nacionalidad
                        val indexNacionalidad = listaNombresPaises.indexOf(nacionalidadSeleccionada)
                        if (indexNacionalidad != -1) {
                            spinner_nacionalidad.setSelection(indexNacionalidad)
                        }

                        val paisResidenciaSeleccionado = perfil.pais_residencia
                        val indexPaisResidencia = listaNombresPaises.indexOf(paisResidenciaSeleccionado)
                        if (indexPaisResidencia != -1) {
                            spinner_paises.setSelection(indexPaisResidencia)
                        }
                        //txtDepartamento.text = perfil.departamento
                        //txtCiudad.text = perfil.ciudad

                        // Teléfonos (muestra máximo dos)
                        txtTel1.text = perfil.telefonos.getOrNull(0) ?: "No registrado"
                        txtTel2.text = perfil.telefonos.getOrNull(1) ?: "No registrado"

                    } else {
                        Toast.makeText(this@Act_perfil_cliente, "No se pudo cargar el perfil.", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this@Act_perfil_cliente, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Error: No se ha proporcionado un email de usuario.", Toast.LENGTH_LONG).show()
            finish()
        }


        // escuchar botones y volver
        val txtVolverLogin = findViewById<TextView>(R.id.txt_volver_reg2)
        txtVolverLogin.setOnClickListener {
            finish()
        }

        val buttonDescartar = findViewById<Button>(R.id.buttonDescartar)
        val buttonGuardar = findViewById<Button>(R.id.buttonGuardar)

        // Botón DESCARTAR → Confirmación antes de salir
        buttonDescartar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Descartar cambios")
            builder.setMessage("¿Deseas descartar los cambios realizados?")
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                finish() // Cierra la actividad
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Solo cierra el diálogo
            }
            builder.show()
        }

        // Botón GUARDAR → Muestra toast y abre la siguiente pantalla
        buttonGuardar.setOnClickListener {
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Perfil_cliente::class.java)
            startActivity(intent)
        }

    }
}