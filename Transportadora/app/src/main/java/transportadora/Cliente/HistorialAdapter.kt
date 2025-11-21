package transportadora.Cliente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import transportadora.Modelos.Cliente.HistorialServicio
import transportadora.Login.R
import java.text.SimpleDateFormat
import java.util.Locale
import com.squareup.picasso.Picasso

class HistorialAdapter(
    private var servicios: List<HistorialServicio>,
    private val onCancelClick: (Int, String) -> Unit,
    private val onDetailsClick: (Int) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    private val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.bind(servicio, onCancelClick, onDetailsClick)
    }

    override fun getItemCount(): Int = servicios.size

    fun updateData(newServicios: List<HistorialServicio>) {
        servicios = newServicios
        notifyDataSetChanged()
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServicio: TextView = itemView.findViewById(R.id.tvServicio)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvOrigen: TextView = itemView.findViewById(R.id.tvOrigen)
        private val tvDestino: TextView = itemView.findViewById(R.id.tvDestino)
        private val tvTipoServicio: TextView = itemView.findViewById(R.id.tvTipoServicio)
        private val tvMetodoPago: TextView = itemView.findViewById(R.id.tvMetodoPago)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val btnCancelar: Button = itemView.findViewById(R.id.btnCancelar)
        private val btnDetalle: Button = itemView.findViewById(R.id.btnDetalle)

        // Nuevas vistas para la información del conductor
        private val layoutConductor: View = itemView.findViewById(R.id.layoutConductor)
        private val tvNombreConductor: TextView = itemView.findViewById(R.id.tvNombreConductor)
        private val imgConductor: ImageView = itemView.findViewById(R.id.imgConductor)

        fun bind(servicio: HistorialServicio, onCancelClick: (Int, String) -> Unit, onDetailsClick: (Int) -> Unit) {

            val idRutaInt = try { servicio.id_ruta.toInt() } catch (e: NumberFormatException) { 0 }

            tvServicio.text = "Servicio: Envío Paquete #${servicio.id_ruta}"

            try {
                val parsedDate = parser.parse(servicio.fecha_hora_reserva)
                tvFecha.text = "Fecha: ${formatter.format(parsedDate)}"
            } catch (e: Exception) {
                tvFecha.text = "Fecha: N/A"
            }

            tvOrigen.text = "Origen: ${servicio.direccion_origen}, ${servicio.ciudad_origen}"
            tvDestino.text = "Destino: ${servicio.direccion_destino}, ${servicio.ciudad_destino}"
            tvTipoServicio.text = "Tipo: ${servicio.tipo_servicio}"
            tvMetodoPago.text = "Pago: ${servicio.metodo_pago}"
            tvEstado.text = "Estado: ${servicio.estado_servicio}"

            val estadoCancelable = servicio.estado_servicio == "Pendiente"

            // NUEVA LÓGICA: Mostrar información del conductor solo si el estado es "En proceso"
            if (servicio.estado_servicio == "En proceso" && !servicio.nombre_conductor.isNullOrEmpty()) {
                layoutConductor.visibility = View.VISIBLE
                tvNombreConductor.text = servicio.nombre_conductor

                // Cargar imagen con Picasso (similar al perfil del conductor)
                if (!servicio.url_foto_conductor.isNullOrEmpty()) {
                    Picasso.get()
                        .load(servicio.url_foto_conductor)
                        .placeholder(R.drawable.fondo_main)
                        .error(R.drawable.fondo_main)
                        .into(imgConductor)
                } else {
                    imgConductor.setImageResource(R.drawable.fondo_main)
                }
            } else {
                layoutConductor.visibility = View.GONE
            }

            if (idRutaInt > 0) {
                btnDetalle.visibility = View.VISIBLE
                btnDetalle.setOnClickListener {
                    onDetailsClick(idRutaInt)
                }
            } else {
                btnDetalle.visibility = View.GONE
            }

            if (estadoCancelable) {
                btnCancelar.visibility = View.VISIBLE
                btnCancelar.setOnClickListener {
                    onCancelClick(idRutaInt, servicio.metodo_pago)
                }
            } else {
                btnCancelar.visibility = View.GONE
            }
        }
    }
}