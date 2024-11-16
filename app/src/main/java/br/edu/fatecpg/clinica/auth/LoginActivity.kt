package br.edu.fatecpg.clinica.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.clinica.databinding.ActivityLoginBinding
import br.edu.fatecpg.clinica.ui.AgendaActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Botão de login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val senha = binding.etSenhaLogin.text.toString()
            loginUsuario(email, senha)
        }

        // Texto de redirecionamento para a tela de registro
        binding.tvRegistrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }

    private fun loginUsuario(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AgendaActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erro no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
