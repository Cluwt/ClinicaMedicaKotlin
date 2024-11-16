package br.edu.fatecpg.clinica.model

import com.google.firebase.Timestamp

data class Consulta(
    val idPaciente: String = "",
    val idMedico: String = "",
    val dataHora: Timestamp = Timestamp.now(),
    val status: String = "Agendada",
    val motivo: String = "" // Novo campo para o motivo da consulta
)
