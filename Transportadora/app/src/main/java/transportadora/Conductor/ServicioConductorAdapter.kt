package transportadora.Conductor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import transportadora.Login.R

class ServicioConductorAdapter(private val servicios: JSONArray) : RecyclerView.Adapter<ServicioConductorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewOrigen: TextView = view.findViewById(R.id.textViewOrigen)
        val textViewDestino: TextView = view.findViewById(R.id.textViewDestino)
        val textViewFecha: TextView = view.findViewById(R.id.textViewFecha)
        val textViewEstado: TextView = view.findViewById(R.id.textViewEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio_conductor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servicio = servicios.getJSONObject(position)
        holder.textViewOrigen.text = "Origen: ${servicio.getString("origen")}"
        holder.textViewDestino.text = "Destino: ${servicio.getString("destino")}"
        holder.textViewFecha.text = "Fecha: ${servicio.getString("fecha")}"
        holder.textViewEstado.text = "Estado: ${servicio.getString("estado")}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleServicioConductorActivity::class.java).apply {
                putExtra("servicio_json", servicio.toString())
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = servicios.length()
}
