package com.pdm.quiz.presentation.ranking

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
import com.pdm.quiz.databinding.ActivityRankingBinding
import kotlinx.coroutines.launch

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var adapter: RankingAdapter
    private lateinit var viewModel: RankingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as QuizApp
        val factory = RankingViewModelFactory(app.container.usersRepository)
        viewModel = ViewModelProvider(this, factory)[RankingViewModel::class.java]

        adapter = RankingAdapter()
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rankingRecyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    binding.rankingProgressBar.visibility = if (ui.loading) View.VISIBLE else View.GONE
                    if (ui.error != null) {
                        Toast.makeText(this@RankingActivity, ui.error, Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.submit(ui.items)
                    }
                }
            }
        }
    }
}
