package com.pdm.quiz.presentation.categories

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pdm.quiz.R
import com.pdm.quiz.app.QuizApp
import com.pdm.quiz.databinding.ActivityMainBinding
import com.pdm.quiz.presentation.auth.LoginActivity
import com.pdm.quiz.presentation.history.HistoryActivity
import com.pdm.quiz.presentation.ranking.RankingActivity
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: QuizListAdapter
    private lateinit var viewModel: CategoriesViewModel

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as QuizApp
        val factory = CategoriesViewModelFactory(app.container.questionsRepository, app.container.authRepository)
        viewModel = ViewModelProvider(this, factory)[CategoriesViewModel::class.java]

        adapter = QuizListAdapter(onClick = { category ->
            val intent = Intent(this, com.pdm.quiz.presentation.quiz.QuizActivity::class.java)
            intent.putExtra("quizId", category.id)
            intent.putExtra("quizName", category.name)
            startActivity(intent)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.logoutBtn.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            googleSignInClient.signOut().addOnCompleteListener {
                viewModel.onLogoutClicked()

                val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                startActivity(intent)

                finish()
            }
        }

        binding.rankBtn.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }

        binding.statsBtn.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    binding.progressBar.visibility = if (ui.loading) View.VISIBLE else View.GONE
                    if (ui.error != null) {
                        Toast.makeText(this@MainActivity, ui.error, Toast.LENGTH_SHORT).show()
                    }
                    adapter.submit(ui.categories)
                }
            }
        }
    }
}
