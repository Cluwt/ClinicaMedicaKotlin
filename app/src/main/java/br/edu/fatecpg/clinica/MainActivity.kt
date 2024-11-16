

package br.edu.fatecpg.clinica
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import br.edu.fatecpg.clinica.R
import br.edu.fatecpg.clinica.auth.LoginActivity
import br.edu.fatecpg.clinica.ui.AgendaActivity
import br.edu.fatecpg.clinica.ui.PacienteMainActivity

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Verifica o usuário e redireciona para a atividade apropriada
        verificarUsuario()
    }

    private fun verificarUsuario() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuário autenticado, busca o tipo do usuário no Firestore
            val userId = currentUser.uid
            db.collection("usuarios")
                .document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            val tipoUsuario = documentSnapshot.getString("tipo")
                            when (tipoUsuario) {
                                "Paciente" -> {
                                    startActivity(Intent(this, PacienteMainActivity::class.java))
                                    finish()  // Finaliza a MainActivity para evitar retorno acidental
                                }
                                "Medico" -> {
                                    startActivity(Intent(this, AgendaActivity::class.java))
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Usuário não encontrado no banco de dados", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Erro ao verificar tipo de usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Erro ao verificar tipo de usuário: ${exception.message}", Toast.LENGTH_SHORT).show()
                    // Redireciona para a tela de login caso ocorra um erro
                    startActivity(Intent(this, LoginActivity::class.java))
                }
        } else {
            // Usuário não autenticado, redireciona para a tela de login
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
