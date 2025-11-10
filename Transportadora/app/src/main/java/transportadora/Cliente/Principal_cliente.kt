package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import transportadora.Almacenados.Pais_almacenados
import transportadora.Almacenados.Tipo_servicio_almacenados
import transportadora.Compartido.Cambiar_contra
import transportadora.Login.R
import android.view.View

class Principal_cliente : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal_cliente)


        val spinner_direcciones = findViewById<Spinner>(R.id.spinner_origen_tipo)
        val direcciones = listOf("Mi direccion", "Otra direccion")
        val adapter_direcciones =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, direcciones)
        adapter_direcciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_direcciones.adapter = adapter_direcciones

        val spinner_paises = findViewById<Spinner>(R.id.spinner_pais_destino)
        val paises = listOf("Colombia", "Peru", "Ecuador")
        val adapter_paises = ArrayAdapter(this, android.R.layout.simple_spinner_item, paises)
        adapter_paises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_paises.adapter = adapter_paises

        val spinner_departamento1 = findViewById<Spinner>(R.id.spinner_depto_origen)
        val spinner_departamento2 = findViewById<Spinner>(R.id.spinner_depto_destino)
        val departamentos = listOf("Cundinamarca", "Meta")
        val adapter_departamentos =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
        adapter_departamentos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_departamento1.adapter = adapter_departamentos
        spinner_departamento2.adapter = adapter_departamentos

        val spinner_ciudades1 = findViewById<Spinner>(R.id.spinner_ciudad_origen)
        val spinner_ciudades2 = findViewById<Spinner>(R.id.spinner_ciudad_destino)
        val ciudades = listOf("Bogota", "Villavicencio")
        val adapter_ciudades = ArrayAdapter(this, android.R.layout.simple_spinner_item, ciudades)
        adapter_ciudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_ciudades1.adapter = adapter_ciudades
        spinner_ciudades2.adapter = adapter_ciudades


        val txtDireccionOrigen = findViewById<android.widget.EditText>(R.id.txt_direccion_origen)
        // Por defecto los campos están deshabilitados
        txtDireccionOrigen.isEnabled = false
        spinner_departamento1.isEnabled = false
        spinner_ciudades1.isEnabled = false

        val spinner_pasajeros = findViewById<Spinner>(R.id.spinner_cantidad_pasajeros)
        val cantidad_pasajeros = listOf("1", "2", "3", "4")
        val adapter_pasajeros = ArrayAdapter(this, android.R.layout.simple_spinner_item, cantidad_pasajeros)
        adapter_pasajeros.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pasajeros.adapter = adapter_pasajeros

        val spinner_tipos = findViewById<Spinner>(R.id.spinner_tipo_servicio)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tipos = withContext(Dispatchers.IO) {
                    Tipo_servicio_almacenados.obtener_tipo_servicio()
                }

                if (tipos.isNotEmpty()) {
                    val tipos_identificacion = tipos.map { it.descripcion }

                    val adapter_tipos = ArrayAdapter(this@Principal_cliente, android.R.layout.simple_spinner_item, tipos_identificacion)
                    adapter_tipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_tipos.adapter = adapter_tipos
                } else {
                    Toast.makeText(this@Principal_cliente, "No se encontraron tipos de servicio", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@Principal_cliente, "Error al cargar tipos de servicio: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        val spinner_categoria = findViewById<Spinner>(R.id.spinner_categoria_servicio)
        val categorias = listOf("Normal", "Especial", "Urgente")
        val adapter_categorias =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter_categorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_categoria.adapter = adapter_categorias

        val spinner_pago = findViewById<Spinner>(R.id.spinner_metodo_pago)
        val metodos_pago = listOf("Efectivo", "Tarjeta", "Transferencia")
        val adapter_pagos = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodos_pago)
        adapter_pagos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pago.adapter = adapter_pagos

        //Menu lateral
        val txteditarperfil = findViewById<TextView>(R.id.editarperfil)
        txteditarperfil.setOnClickListener {
            val intent = Intent(this, Perfil_cliente::class.java)
            startActivity(intent)
        }
        val txtcambiarcontra = findViewById<TextView>(R.id.cambiocontra)
        txtcambiarcontra.setOnClickListener {
            val intent =
                Intent(this@Principal_cliente, transportadora.Compartido.Preg_seguridad::class.java)
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent = Intent(this@Principal_cliente, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent = Intent(this@Principal_cliente, transportadora.Compartido.Ayuda::class.java)
            startActivity(intent)
        }

        //menu inferior
        val txtmenu1 = findViewById<TextView>(R.id.menu1)
        val scrollView = findViewById<ScrollView>(R.id.scrollContenido)
        txtmenu1.setOnClickListener {
            scrollView.post {
                scrollView.smoothScrollTo(0, 0) //
            }
        }
        val txtmenu2 = findViewById<TextView>(R.id.menu2)
        txtmenu2.setOnClickListener {
            val intent = Intent(this, Seguimiento_serv_cliente::class.java)
            startActivity(intent)
        }
        val txtmenu3 = findViewById<TextView>(R.id.menu3)
        txtmenu3.setOnClickListener {
            val intent = Intent(this, Historial_serv_cliente::class.java)
            startActivity(intent)
        }

        val btncontinuarpago = findViewById<TextView>(R.id.btn_continuar)
        btncontinuarpago.setOnClickListener {
            val intent = Intent(this, Transferencia::class.java)
            startActivity(intent)
        }

        spinner_direcciones.setSelection(0) //por defecto seleccion mi direccion
        spinner_direcciones.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccion = parent.getItemAtPosition(position).toString()
                    val habilitar = seleccion == "Otra direccion"

                    txtDireccionOrigen.isEnabled = habilitar
                    spinner_departamento1.isEnabled = habilitar
                    spinner_ciudades1.isEnabled = habilitar

                    if (habilitar) {
                        txtDireccionOrigen.hint = "Ingresa otra dirección (Ej: Calle 45 #10-23)"
                    } else {
                        txtDireccionOrigen.hint = "Usando tu dirección de residencia"
                    }
                }


                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

        }

        val spinner_cantidad_pasajeros = findViewById<Spinner>(R.id.spinner_cantidad_pasajeros)
        val pasajero_1 = findViewById<EditText>(R.id.txt_pasajero1)
        val pasajero_2 = findViewById<EditText>(R.id.txt_pasajero2)
        val pasajero_3 = findViewById<EditText>(R.id.txt_pasajero3)
        val pasajero_4 = findViewById<EditText>(R.id.txt_pasajero4)

        val txt_pasajeros = listOf(pasajero_1, pasajero_2, pasajero_3, pasajero_4)

        spinner_cantidad_pasajeros.isEnabled = false
        spinner_cantidad_pasajeros.setSelection(0)
        spinner_tipos.setSelection(0)

        txt_pasajeros.forEach {
            it.isEnabled = false
            it.hint = "No disponible"
        }

        // 🔹 Listener del tipo de servicio
        spinner_tipos.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val seleccion = parent.getItemAtPosition(position).toString()
                    val esPasajeros = seleccion == "Pasajeros"

                    spinner_cantidad_pasajeros.isEnabled = esPasajeros

                    if (esPasajeros) {
                        // Habilitar el primer pasajero
                        pasajero_1.isEnabled = true
                        pasajero_1.hint = "Nombre completo"
                    } else {
                        // Desactivar todo si no es "Pasajeros"
                        txt_pasajeros.forEach {
                            it.isEnabled = false
                            it.hint = "No disponible"
                            it.text.clear()
                        }
                        spinner_cantidad_pasajeros.setSelection(0)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

// 🔹 Listener de cantidad de pasajeros
        spinner_cantidad_pasajeros.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val cantidad = parent.getItemAtPosition(position).toString().toIntOrNull() ?: 1

                    for ((index, campo) in txt_pasajeros.withIndex()) {
                        val activo = index < cantidad
                        campo.isEnabled = activo
                        campo.hint = if (activo) "Nombre completo" else "No disponible"
                        if (!activo) campo.text.clear()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }



    }
}