import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.clinica.databinding.ActivityAgendarConsultaBinding
import br.edu.fatecpg.clinica.model.Consulta
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AgendarConsultaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendarConsultaBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedMedicoId: String? = null
    private var dataConsulta: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendarConsultaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMedicoSpinner()

        binding.tvDataConsulta.setOnClickListener {
            abrirDatePickerDialog()
        }

        binding.tvHoraConsulta.setOnClickListener {
            abrirTimePickerDialog()
        }

        binding.btnAgendarConsulta.setOnClickListener {
            agendarConsulta()
        }
    }

    private fun abrirDatePickerDialog() {
        val calendarioAtual = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, ano, mes, dia ->
                dataConsulta = Calendar.getInstance()
                dataConsulta?.set(ano, mes, dia)
                val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvDataConsulta.text = formatoData.format(dataConsulta?.time)
            },
            calendarioAtual.get(Calendar.YEAR),
            calendarioAtual.get(Calendar.MONTH),
            calendarioAtual.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun abrirTimePickerDialog() {
        val calendarioAtual = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hora, minuto ->
                dataConsulta?.set(Calendar.HOUR_OF_DAY, hora)
                dataConsulta?.set(Calendar.MINUTE, minuto)
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
                binding.tvHoraConsulta.text = formatoHora.format(dataConsulta?.time)
            },
            calendarioAtual.get(Calendar.HOUR_OF_DAY),
            calendarioAtual.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun setupMedicoSpinner() {
        // Carregar lista de médicos
        val medicosIds = mutableListOf<String>()
        val medicosNomes = mutableListOf<String>()

        db.collection("usuarios")
            .whereEqualTo("tipo", "Medico")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    medicosIds.add(document.id)
                    medicosNomes.add(document.getString("nome") ?: "Nome desconhecido")
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicosNomes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerMedico.adapter = adapter

                binding.spinnerMedico.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedMedicoId = medicosIds[position] // Atribui o ID do médico selecionado
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedMedicoId = null // Define como nulo caso nada seja selecionado
                    }
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar médicos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun agendarConsulta() {
        val motivo = binding.etMotivoConsulta.text.toString()

        if (motivo.isEmpty() || dataConsulta == null || selectedMedicoId == null) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val consulta = Consulta(
            idPaciente = auth.currentUser?.uid ?: "",
            idMedico = selectedMedicoId!!,
            dataHora = Timestamp(dataConsulta?.time ?: Date()),
            motivo = motivo,
            status = "Agendada"
        )

        db.collection("consultas")
            .add(consulta)
            .addOnSuccessListener {
                Toast.makeText(this, "Consulta agendada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao agendar consulta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
