package com.pdm.quiz

import com.pdm.quiz.R
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

        // Registra o callback para o resultado da tela de login do Google
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

        // Ação do botão de login
        binding.loginBtn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        // Verifica se o usuário já está logado e o redireciona
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
                    // Login bem-sucedido, navega para a tela principal
                    navigateToMainActivity()
                } else {
                    // Se o login falhar, mostra uma mensagem
                    Toast.makeText(this, "Falha na autenticação com Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a LoginActivity para que o usuário não possa voltar a ela
    }
}