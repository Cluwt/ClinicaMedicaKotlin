package br.edu.fatecpg.clinica.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.clinica.databinding.ActivityVerConsultasBinding

class VerConsultasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerConsultasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração do View Binding
        binding = ActivityVerConsultasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o botão "Voltar" para fechar a Activity
        binding.btnVoltarVerConsultas.setOnClickListener {
            finish() // Fecha a Activity e retorna para a anterior
        }
    }
}
