package transportadora.Conductor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import transportadora.Modelos.Conductor.PagoConductor
import transportadora.Login.R
import java.text.SimpleDateFormat
import java.util.Locale

class PagosAdapter(private var servicios: List<PagoConductor>) :
    RecyclerView.Adapter<PagosAdapter.HistorialViewHolder>() {

    // Formateadores de fecha
    private val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pagos, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.bind(servicio)
    }

    override fun getItemCount(): Int = servicios.size

    fun updateData(newServicios: List<PagoConductor>) {
        servicios = newServicios
        notifyDataSetChanged()
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServicio: TextView = itemView.findViewById(R.id.txt_id_servicio)
        private val tvFecha: TextView = itemView.findViewById(R.id.txt_fecha)

        fun bind(servicio: PagoConductor) {
            tvServicio.text = "Servicio #${servicio.id_ruta}"

            // Formatear fecha de finalización
            try {
                val parsedDate = parser.parse(servicio.fecha_finalizacion)
                tvFecha.text = formatter.format(parsedDate)
            } catch (e: Exception) {
                tvFecha.text = servicio.fecha_finalizacion
            }

            // Mapear campos a layout item_pagos
            val cliente = itemView.findViewById<TextView>(R.id.txt_cliente)
            val tipo = itemView.findViewById<TextView>(R.id.txt_tipo)
            val metodo = itemView.findViewById<TextView>(R.id.txt_metodo)
            val total = itemView.findViewById<TextView>(R.id.txt_total)
            val pago = itemView.findViewById<TextView>(R.id.txt_pago_conductor)

            tvServicio.text = "Servicio #${servicio.id_ruta}"
            cliente.text = servicio.nombre_cliente
            tipo.text = "${servicio.tipo_servicio} - ${servicio.categoria_servicio}"
            metodo.text = servicio.metodo_pago
            total.text = "$${String.format("%,.2f", servicio.total)}"
            pago.text = "$${String.format("%,.2f", servicio.total*0.3)}"
        }
    }
}
