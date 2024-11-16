package br.edu.fatecpg.clinica.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.fatecpg.clinica.model.Consulta
import br.edu.fatecpg.clinica.repository.ConsultaRepository

class ConsultaViewModel : ViewModel() {

    private val consultaRepository = ConsultaRepository()
    private val _consultas = MutableLiveData<List<Consulta>>()
    val consultas: LiveData<List<Consulta>> = _consultas
    private val _consultaStatus = MutableLiveData<String>()
    val consultaStatus: LiveData<String> = _consultaStatus

    fun agendarConsulta(consulta: Consulta) {
        consultaRepository.agendarConsulta(consulta,
            sucesso = { _consultaStatus.value = "Consulta Agendada com Sucesso" },
            falha = { _consultaStatus.value = it.message }
        )
    }

    fun cancelarConsulta(consultaId: String) {
        consultaRepository.cancelarConsulta(consultaId,
            sucesso = { _consultaStatus.value = "Consulta Cancelada" },
            falha = { _consultaStatus.value = it.message }
        )
    }

    fun buscarConsultas(idMedico: String) {
        consultaRepository.buscarConsultas(idMedico,
            sucesso = { consultas -> _consultas.value = consultas },
            falha = { _consultaStatus.value = it.message }
        )
    }
}
