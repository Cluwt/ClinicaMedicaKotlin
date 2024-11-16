package br.edu.fatecpg.clinica.ui

import AgendarConsultaActivity
import ConsultaAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.clinica.R
import br.edu.fatecpg.clinica.databinding.ActivityPacienteMainBinding
import br.edu.fatecpg.clinica.model.Consulta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PacienteMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val consultasPaciente = mutableListOf<Consulta>()
    private lateinit var consultaAdapter: ConsultaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use root para definir a view principal

        binding.btnMarcarConsulta.setOnClickListener {
            try {
                // Cria um Intent usando apply para deixar o código mais limpo
                val intent = Intent(this, AgendarConsultaActivity::class.java).apply {
                    // Caso queira passar algum extra, é só adicionar aqui
                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Exibe uma mensagem de erro caso a atividade não seja encontrada
                Toast.makeText(this, "Erro: não foi possível abrir a atividade.", Toast.LENGTH_SHORT).show()
            }
        }
        setupRecyclerView()
        carregarConsultasPaciente()
    }

    override fun onResume() {
        super.onResume()
        carregarConsultasPaciente()
    }

    private fun setupRecyclerView() {
        consultaAdapter = ConsultaAdapter(consultasPaciente)
        binding.rvConsultasPaciente.layoutManager = LinearLayoutManager(this)
        binding.rvConsultasPaciente.adapter = consultaAdapter
    }

    private fun carregarConsultasPaciente() {
        val pacienteId = auth.currentUser?.uid
        if (pacienteId != null) {
            db.collection("consultas")
                .whereEqualTo("idPaciente", pacienteId)
                .get()
                .addOnSuccessListener { documents ->
                    consultasPaciente.clear()
                    for (document in documents) {
                        val consulta = document.toObject(Consulta::class.java)
                        consultasPaciente.add(consulta)
                    }
                    consultaAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener {
                    // Tratamento de erro
                }
        }
    }
}
