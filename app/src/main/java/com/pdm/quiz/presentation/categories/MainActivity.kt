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
import com.pdm.quiz.app.QuizApp
import com.pdm.quiz.databinding.ActivityMainBinding
import com.pdm.quiz.presentation.history.HistoryActivity
import com.pdm.quiz.presentation.ranking.RankingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: QuizListAdapter
    private lateinit var viewModel: CategoriesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as QuizApp
        val factory = CategoriesViewModelFactory(app.container.questionsRepository)
        viewModel = ViewModelProvider(this, factory)[CategoriesViewModel::class.java]

        adapter = QuizListAdapter(onClick = { category ->
            val intent = Intent(this, com.pdm.quiz.presentation.quiz.QuizActivity::class.java)
            intent.putExtra("quizId", category.id)
            intent.putExtra("quizName", category.name)
            startActivity(intent)
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.rankBtn.setOnClickListener { startActivity(Intent(this, RankingActivity::class.java)) }
        binding.statsBtn.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }

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
