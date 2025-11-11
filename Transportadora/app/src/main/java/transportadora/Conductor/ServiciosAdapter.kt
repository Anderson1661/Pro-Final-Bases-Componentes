package transportadora.Conductor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import transportadora.Modelos.Conductor.HistorialServicio
import transportadora.Login.R
import java.text.SimpleDateFormat
import java.util.Locale

class ServiciosAdapter(private var servicios: List<HistorialServicio>) :
    RecyclerView.Adapter<ServiciosAdapter.HistorialViewHolder>() {

    // Formateadores de fecha
    private val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.bind(servicio)
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

        fun bind(servicio: HistorialServicio) {
            tvServicio.text = "Servicio: Envío Paquete #${servicio.id_ruta}"

            // Formatear fecha
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
        }
    }
}
