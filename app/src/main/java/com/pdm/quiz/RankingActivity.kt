package com.pdm.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdm.quiz.databinding.ActivityRankingBinding

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var adapter: RankingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dados de teste (mock)
        val dummyData = listOf(
            RankingItem(1, "Alice", 1500),
            RankingItem(2, "Bob", 1400),
            RankingItem(3, "Carol", 1300),
            RankingItem(4, "David", 1200),
            RankingItem(5, "Eve", 1100)
        )


        binding.rvRanking.layoutManager = LinearLayoutManager(this)
        adapter = RankingAdapter(dummyData.toMutableList())
        binding.rvRanking.adapter = adapter
    }
}
