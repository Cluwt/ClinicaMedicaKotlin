package br.edu.fatecpg.clinica.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import br.edu.fatecpg.clinica.model.Usuario

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registrarUsuario(usuario: Usuario, senha: String, sucesso: () -> Unit, falha: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(usuario.email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection("usuarios").document(auth.currentUser!!.uid)
                        .set(usuario)
                        .addOnSuccessListener { sucesso() }
                        .addOnFailureListener { falha(it) }
                } else {
                    falha(task.exception!!)
                }
            }
    }

    fun login(email: String, senha: String, sucesso: () -> Unit, falha: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sucesso()
                } else {
                    falha(task.exception!!)
                }
            }
    }

    fun deslogar() {
        auth.signOut()
    }
}
