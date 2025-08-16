package com.pdm.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pdm.quiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Toast.makeText(this, "Falha no login com Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            navigateToMainActivity()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
        binding.progressBar.visibility = View.VISIBLE
        binding.loginBtn.visibility = View.GONE
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                binding.loginBtn.visibility = View.VISIBLE
                if (task.isSuccessful) {
                    // Login bem-sucedido, agora salva/atualiza o perfil do usuário
                    saveUserProfile() // <-- CHAMADA DA NOVA FUNÇÃO
                } else {
                    Toast.makeText(this, "Falha na autenticação com Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // NOVA FUNÇÃO para salvar o perfil do usuário no Firestore
    private fun saveUserProfile() {
        val firebaseUser = auth.currentUser ?: run {
            // Sem usuário autenticado — segue o fluxo do app como você preferir
            navigateToMainActivity()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(firebaseUser.uid)

        // Campos que PODEM ser atualizados a partir do FirebaseUser
        val incoming = mutableMapOf<String, Any>()
        firebaseUser.displayName?.let { incoming["displayName"] = it }

        db.runTransaction { txn ->
            val snap = txn.get(docRef)

            if (!snap.exists()) {
                // --- CRIAÇÃO ---
                // createdAt será preenchido pelo servidor por causa do @ServerTimestamp
                val newUser = User(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: "Usuário",
                    totalScore = 0,         // padrão para novo usuário
                )
                txn.set(docRef, newUser)   // cria doc com padrão
            } else {
                // --- ATUALIZAÇÃO ---
                // Atualiza SOMENTE o que veio no 'incoming' (se houver algo)
                if (incoming.isNotEmpty()) {
                    // Opcional: evitar writes desnecessários comparando com o existente
                    val current = snap.toObject(User::class.java)
                    val diff = mutableMapOf<String, Any>()
                    incoming.forEach { (k, v) ->
                        val currentVal = when (k) {
                            "displayName" -> current?.displayName
                            else          -> null
                        }
                        if (currentVal != v) diff[k] = v
                    }
                    if (diff.isNotEmpty()) txn.update(docRef, diff)
                }
                // Importante: não tocamos em totalScore nem em createdAt -> preservados
            }
            null
        }
            .addOnSuccessListener {
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar perfil.", Toast.LENGTH_SHORT).show()
                // Mesmo com falha, permite que o usuário entre no app
                navigateToMainActivity()
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}