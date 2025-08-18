package com.pdm.quiz.presentation.history

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
import com.pdm.quiz.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as QuizApp
        val factory = HistoryViewModelFactory(app.container.sessionsRepository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        adapter = HistoryAdapter()
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    binding.historyProgressBar.visibility = if (ui.loading) View.VISIBLE else View.GONE
                    if (ui.error != null) {
                        Toast.makeText(this@HistoryActivity, ui.error, Toast.LENGTH_SHORT).show()
                    } else {
                        adapter.submit(ui.items)
                    }
                }
            }
        }
    }
}
