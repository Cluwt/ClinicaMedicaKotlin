package br.edu.fatecpg.clinica.repository

import br.edu.fatecpg.clinica.model.Consulta
import com.google.firebase.firestore.FirebaseFirestore

class ConsultaRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun agendarConsulta(consulta: Consulta, sucesso: () -> Unit, falha: (Exception) -> Unit) {
        firestore.collection("consultas").add(consulta)
            .addOnSuccessListener { sucesso() }
            .addOnFailureListener { falha(it) }
    }

    fun cancelarConsulta(consultaId: String, sucesso: () -> Unit, falha: (Exception) -> Unit) {
        firestore.collection("consultas").document(consultaId)
            .update("status", "Cancelada")
            .addOnSuccessListener { sucesso() }
            .addOnFailureListener { falha(it) }
    }

    fun buscarConsultas(idMedico: String, sucesso: (List<Consulta>) -> Unit, falha: (Exception) -> Unit) {
        firestore.collection("consultas")
            .whereEqualTo("idMedico", idMedico)
            .get()
            .addOnSuccessListener { result ->
                val consultas = result.toObjects(Consulta::class.java)
                sucesso(consultas)
            }
            .addOnFailureListener { falha(it) }
    }
}
