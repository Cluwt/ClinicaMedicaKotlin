package br.edu.fatecpg.clinica.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.clinica.databinding.ActivityAdicionarConsultaBinding
import br.edu.fatecpg.clinica.model.Consulta
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AdicionarConsultaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdicionarConsultaBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedPacienteId: String? = null
    private var selectedDataHora: Timestamp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdicionarConsultaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarPacientes()

        binding.btnSelecionarDataHora.setOnClickListener {
            selecionarDataHora()
        }

        binding.btnSalvarConsulta.setOnClickListener {
            salvarConsulta()
        }
    }

    private fun carregarPacientes() {
        db.collection("usuarios")
            .whereEqualTo("tipo", "Paciente")
            .get()
            .addOnSuccessListener { documents ->
                val pacientes = mutableListOf<String>()
                val pacientesIds = mutableListOf<String>()
                for (document in documents) {
                    val nome = document.getString("nome") ?: "Paciente"
                    pacientes.add(nome)
                    pacientesIds.add(document.id)
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pacientes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerPaciente.adapter = adapter

                binding.spinnerPaciente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedPacienteId = pacientesIds[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedPacienteId = null
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selecionarDataHora() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        selectedDataHora = Timestamp(calendar.time)
                        binding.tvDataHora.text = calendar.time.toString()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun salvarConsulta() {
        val medicoId = auth.currentUser?.uid
        val motivo = binding.etMotivoConsulta.text.toString()

        if (selectedPacienteId != null && selectedDataHora != null && medicoId != null && motivo.isNotEmpty()) {
            val novaConsulta = Consulta(
                idPaciente = selectedPacienteId!!,
                idMedico = medicoId,
                dataHora = selectedDataHora!!,
                status = "Agendada",
                motivo = motivo
            )

            db.collection("consultas")
                .add(novaConsulta)
                .addOnSuccessListener {
                    Toast.makeText(this, "Consulta salva com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar consulta: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }
}
