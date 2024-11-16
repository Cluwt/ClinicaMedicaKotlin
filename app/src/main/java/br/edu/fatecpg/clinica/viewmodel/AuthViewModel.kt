package br.edu.fatecpg.clinica.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.fatecpg.clinica.model.Usuario
import br.edu.fatecpg.clinica.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> = _authStatus

    fun registrarUsuario(nome: String, email: String, senha: String, tipo: String) {
        val usuario = Usuario(nome, email, tipo)
        authRepository.registrarUsuario(usuario, senha,
            sucesso = { _authStatus.value = "Sucesso" },
            falha = { _authStatus.value = it.message }
        )
    }

    fun login(email: String, senha: String) {
        authRepository.login(email, senha,
            sucesso = { _authStatus.value = "Sucesso" },
            falha = { _authStatus.value = it.message }
        )
    }

    fun deslogar() {
        authRepository.deslogar()
        _authStatus.value = "Deslogado"
    }
}
