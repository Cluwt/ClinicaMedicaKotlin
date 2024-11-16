import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.clinica.R
import br.edu.fatecpg.clinica.model.Consulta
import java.text.SimpleDateFormat
import java.util.Locale

class ConsultaAdapter(private val consultas: List<Consulta>) :
    RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consulta, parent, false)
        return ConsultaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = consultas[position]
        holder.bind(consulta)
    }

    override fun getItemCount() = consultas.size

    inner class ConsultaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDataHora: TextView = itemView.findViewById(R.id.tvDataHora)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvMotivo: TextView = itemView.findViewById(R.id.tvMotivo)

        fun bind(consulta: Consulta) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvDataHora.text = sdf.format(consulta.dataHora.toDate())
            tvStatus.text = consulta.status
            tvMotivo.text = consulta.motivo
        }
    }
}
