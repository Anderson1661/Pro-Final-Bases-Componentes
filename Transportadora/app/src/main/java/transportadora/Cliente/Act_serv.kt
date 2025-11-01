package transportadora.Cliente

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import transportadora.Login.R

class Act_serv : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_serv)


        val spinner_direcciones = findViewById<Spinner>(R.id.spinner_origen_tipo)
        val direcciones = listOf("Mi direccion", "Otra direccion")
        val adapter_direcciones = ArrayAdapter(this, android.R.layout.simple_spinner_item, direcciones)
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
        val adapter_departamentos = ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentos)
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

        val spinner_pasajeros = findViewById<Spinner>(R.id.spinner_cantidad_pasajeros)
        val pasajeros = listOf("1", "2", "3")
        val adapter_pasajeros = ArrayAdapter(this, android.R.layout.simple_spinner_item, pasajeros)
        adapter_pasajeros.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_pasajeros.adapter = adapter_pasajeros

        val spinner_tipos = findViewById<Spinner>(R.id.spinner_tipo_servicio)
        val tipos = listOf("Pasajeros", "Alimentos")
        val adapter_tipos = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter_tipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_tipos.adapter = adapter_tipos

        val spinner_categoria = findViewById<Spinner>(R.id.spinner_categoria_servicio)
        val categorias = listOf("Normal", "Especial", "Urgente")
        val adapter_categorias = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
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
            val intent = Intent(this@Act_serv, transportadora.Compartido.Preg_seguridad::class.java)
            startActivity(intent)
        }
        val txtcerrarsesion = findViewById<TextView>(R.id.cerrarsesion)
        txtcerrarsesion.setOnClickListener {
            val intent = Intent(this@Act_serv, transportadora.Compartido.Main::class.java)
            startActivity(intent)
        }
        val txtayuda = findViewById<TextView>(R.id.ayuda)
        txtayuda.setOnClickListener {
            val intent = Intent(this@Act_serv, transportadora.Compartido.Ayuda::class.java)
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
    }
}