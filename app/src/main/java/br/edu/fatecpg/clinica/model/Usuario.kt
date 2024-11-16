package br.edu.fatecpg.clinica.model

data class Usuario(
    val nome: String = "",
    val email: String = "",
    val tipo: String = ""  // 'Paciente' ou 'Medico'
)
