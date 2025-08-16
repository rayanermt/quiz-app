package com.pdm.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm.quiz.databinding.ActivityRankingBinding

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var adapter: RankingAdapter
    private val userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRankingData()
    }

    private fun setupRecyclerView() {
        adapter = RankingAdapter(userList)
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.rankingRecyclerView.adapter = adapter
    }

    private fun fetchRankingData() {
        binding.rankingProgressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .orderBy("totalScore", Query.Direction.DESCENDING)
            .limit(20) // Pega os 20 melhores
            .get()
            .addOnSuccessListener { snapshot ->
                binding.rankingProgressBar.visibility = View.GONE
                if (snapshot.isEmpty) {
                    Toast.makeText(this, "Nenhum jogador no ranking.", Toast.LENGTH_SHORT).show()
                } else {
                    val users = snapshot.toObjects(User::class.java)
                    userList.clear()
                    userList.addAll(users)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                binding.rankingProgressBar.visibility = View.GONE
                Log.e("RankingActivity", "Erro ao buscar ranking", e)
                Toast.makeText(this, "Erro ao carregar o ranking.", Toast.LENGTH_SHORT).show()
            }
    }
}