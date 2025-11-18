package transportadora.Conductor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import transportadora.Modelos.Conductor.HistorialServicio
import transportadora.Login.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ServiciosAdapter(
    private var servicios: List<HistorialServicio>,
    // El callback necesita la posición para que la Activity actualice solo ese item
    private val onItemAction: (idRuta: Int, nuevoEstadoId: Int, position: Int) -> Unit
) : RecyclerView.Adapter<ServiciosAdapter.HistorialViewHolder>() {

    private val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormatter = DecimalFormat("#,##0")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        // Asegúrate de usar item_servicios
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicios, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.bind(servicio, position)
    }

    override fun getItemCount(): Int = servicios.size

    fun updateData(newServicios: List<HistorialServicio>) {
        servicios = newServicios
        notifyDataSetChanged()
    }

    // Función para obtener un item por posición (útil para la Activity)
    fun getItem(position: Int): HistorialServicio {
        return servicios[position]
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Vistas existentes
        private val tvServicio: TextView = itemView.findViewById(R.id.tvServicio)
        private val tvOrigen: TextView = itemView.findViewById(R.id.tvOrigen)
        private val tvDestino: TextView = itemView.findViewById(R.id.tvDestino)
        // private val tvTipoServicio: TextView = itemView.findViewById(R.id.tvTipoServicio) // No se usa en el binding
        // private val tvMetodoPago: TextView = itemView.findViewById(R.id.tvMetodoPago) // No se usa en el binding

        // Vistas de pago y botones
        private val tvPagoConductor: TextView = itemView.findViewById(R.id.tvPagoConductor)
        private val btnAceptar: Button = itemView.findViewById(R.id.btnAceptarServicio)
        private val btnFinalizar: Button = itemView.findViewById(R.id.btnFinalizarServicio)

        // Vistas para la información sensible del cliente (asumiendo IDs en item_servicios.xml)
        private val tvClienteNombre: TextView = itemView.findViewById(R.id.tvClienteNombre)
        private val tvClienteTelefonos: TextView = itemView.findViewById(R.id.tvClienteTelefonos)
        private val tvPasajeros: TextView = itemView.findViewById(R.id.tvPasajeros)

        private val context = itemView.context

        fun bind(servicio: HistorialServicio, position: Int) {
            // Binding de campos básicos
            tvServicio.text = "Servicio: ${servicio.tipo_servicio} #${servicio.id_ruta}"
            tvOrigen.text = "Origen: ${servicio.direccion_origen}, ${servicio.ciudad_origen}"
            tvDestino.text = "Destino: ${servicio.direccion_destino}, ${servicio.ciudad_destino}"

            // Mostrar Pago al Conductor
            val pagoFormateado = "$ ${currencyFormatter.format(servicio.pago_conductor)}"
            tvPagoConductor.text = pagoFormateado
            tvPagoConductor.setTextColor(ContextCompat.getColor(context, R.color.naranja))

            // Lógica de botones y visibilidad de datos sensibles (4: Pendiente, 2: En proceso, 3: Finalizado)

            if (servicio.id_estado == 4) { // PENDIENTE
                // Botón ACEPTAR: Visible, activo y Naranja
                btnAceptar.visibility = View.VISIBLE
                btnAceptar.text = "Aceptar"
                btnAceptar.isEnabled = true
                btnAceptar.backgroundTintList = ContextCompat.getColorStateList(context, R.color.naranja)

                // Botón FINALIZAR: Oculto
                btnFinalizar.visibility = View.GONE

                // Ocultar info sensible
                tvClienteNombre.visibility = View.GONE
                tvClienteTelefonos.visibility = View.GONE
                tvPasajeros.visibility = View.GONE

            } else if (servicio.id_estado == 2) { // EN PROCESO
                // Botón ACEPTAR: Visible, deshabilitado y Gris oscuro
                btnAceptar.visibility = View.VISIBLE
                btnAceptar.text = "En Proceso"
                btnAceptar.isEnabled = false
                btnAceptar.backgroundTintList = ContextCompat.getColorStateList(context, R.color.grisoscuro)

                // Botón FINALIZAR: Visible y Naranja
                btnFinalizar.visibility = View.VISIBLE
                btnFinalizar.backgroundTintList = ContextCompat.getColorStateList(context, R.color.naranja)

                // Mostrar info sensible (llena desde Principal_conductor.kt)
                tvClienteNombre.visibility = View.VISIBLE
                tvClienteTelefonos.visibility = View.VISIBLE

                tvClienteNombre.text = "Cliente: ${servicio.nombre_cliente}"

                // Mostrar teléfonos
                val telefonosStr = servicio.telefonos_cliente.joinToString(separator = ", ")
                tvClienteTelefonos.text = "Teléfonos: $telefonosStr"

                // Mostrar pasajeros si el tipo es "Personas" y la lista no está vacía
                if (servicio.tipo_servicio.equals("Personas", ignoreCase = true) && servicio.nombres_pasajeros.isNotEmpty()) {
                    tvPasajeros.visibility = View.VISIBLE
                    val pasajerosStr = servicio.nombres_pasajeros.joinToString(separator = ", ")
                    tvPasajeros.text = "Pasajeros: $pasajerosStr"
                } else {
                    tvPasajeros.visibility = View.GONE
                }

            } else {
                // Otros estados (ej. 3 Finalizado), ocultar botones y datos sensibles
                btnAceptar.visibility = View.GONE
                btnFinalizar.visibility = View.GONE
                tvClienteNombre.visibility = View.GONE
                tvClienteTelefonos.visibility = View.GONE
                tvPasajeros.visibility = View.GONE
            }

            // Listeners para las acciones

            // Acción ACEPTAR: Cambia a estado 2 ("En proceso")
            btnAceptar.setOnClickListener {
                if (servicio.id_estado == 4) {
                    // Llama al callback de la Activity para manejar la lógica de aceptación/datos
                    onItemAction(servicio.id_ruta, 2, position)
                }
            }

            // Acción FINALIZAR: Cambia a estado 3 ("Finalizado")
            btnFinalizar.setOnClickListener {
                if (servicio.id_estado == 2) {
                    // Llama al callback de la Activity para manejar la lógica de finalización
                    onItemAction(servicio.id_ruta, 3, position)
                }
            }
        }
    }
}