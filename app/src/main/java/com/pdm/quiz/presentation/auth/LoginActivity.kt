package com.pdm.quiz.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.pdm.quiz.R
import com.pdm.quiz.app.QuizApp
import com.pdm.quiz.presentation.auth.LoginViewModel.UiState
import com.pdm.quiz.presentation.categories.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: LoginViewModel

    private lateinit var signInButton: SignInButton

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signInButton = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progress_bar)

        signInButton.setSize(SignInButton.SIZE_WIDE)

        val app = application as QuizApp
        val factory = LoginViewModelFactory(app.container.authRepository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        app.container.authRepository.currentUser()?.let {
            navigateToMain()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken.isNullOrBlank()) {
                    Toast.makeText(this, "Token inválido.", Toast.LENGTH_SHORT).show()
                    setLoading(false)
                } else {
                    viewModel.signIn(idToken)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Login cancelado ou com erro.", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is UiState.Idle -> setLoading(false)
                        is UiState.Loading -> setLoading(true)
                        is UiState.Success -> {
                            setLoading(false)
                            navigateToMain()
                        }
                        is UiState.Error -> {
                            setLoading(false)
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        signInButton.setOnClickListener {
            setLoading(true)
            val intent = googleSignInClient.signInIntent
            activityResultLauncher.launch(intent)
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        signInButton.isEnabled = !loading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
