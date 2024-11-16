package br.edu.fatecpg.clinica.ui

import ConsultaAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import br.edu.fatecpg.clinica.databinding.ActivityAgendaBinding
import br.edu.fatecpg.clinica.model.Consulta
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgendaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendaBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val consultas = mutableListOf<Consulta>()
    private lateinit var consultaAdapter: ConsultaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        consultaAdapter = ConsultaAdapter(consultas)
        binding.rvConsultas.layoutManager = LinearLayoutManager(this)
        binding.rvConsultas.adapter = consultaAdapter

        verificarTipoUsuario()
    }

    private fun verificarTipoUsuario() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    val tipo = document.getString("tipo")
                    if (tipo == "Medico") {
                        setupMedicoUI(userId)
                    } else {
                        setupPacienteUI(userId)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao verificar tipo de usuário", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupMedicoUI(medicoId: String) {
        binding.btnAdicionarConsulta.visibility = View.VISIBLE
        binding.btnAdicionarConsulta.setOnClickListener {
            val intent = Intent(this, AdicionarConsultaActivity::class.java)
            startActivity(intent)
        }
        carregarConsultas(medicoId = medicoId)
    }

    private fun setupPacienteUI(pacienteId: String) {
        binding.btnAdicionarConsulta.visibility = View.GONE
        carregarConsultas(pacienteId = pacienteId)
    }

    private fun carregarConsultas(pacienteId: String? = null, medicoId: String? = null) {
        val consultasRef = db.collection("consultas")
        val query = when {
            pacienteId != null -> consultasRef.whereEqualTo("idPaciente", pacienteId)
            medicoId != null -> consultasRef.whereEqualTo("idMedico", medicoId)
            else -> consultasRef
        }

        query.get().addOnSuccessListener { documents ->
            consultas.clear()
            for (document in documents) {
                val consulta = document.toObject(Consulta::class.java)
                consultas.add(consulta)
            }
            consultaAdapter.notifyDataSetChanged()
            binding.tvAgendaVazia.visibility = if (consultas.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun adicionarConsulta(medicoId: String) {
        // Exemplo de criação de uma nova consulta para o médico
        val novaConsulta = Consulta(
            idPaciente = "paciente_exemplo_id",
            idMedico = medicoId,
            dataHora = Timestamp.now(),
            status = "Agendada"
        )
        db.collection("consultas").add(novaConsulta)
            .addOnSuccessListener {
                Toast.makeText(this, "Consulta adicionada com sucesso", Toast.LENGTH_SHORT).show()
                carregarConsultas(medicoId = medicoId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao adicionar consulta: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
