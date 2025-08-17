package com.pdm.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm.quiz.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<QuizResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchHistoryData()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(historyList)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = adapter
    }

    private fun fetchHistoryData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não logado.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.historyProgressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(currentUser.uid)
            .collection("quiz_history")
            .orderBy("completedAt", Query.Direction.DESCENDING) // Mais recentes primeiro
            .get()
            .addOnSuccessListener { snapshot ->
                binding.historyProgressBar.visibility = View.GONE
                if (snapshot.isEmpty) {
                    Toast.makeText(this, "Nenhum histórico encontrado.", Toast.LENGTH_SHORT).show()
                } else {
                    val results = snapshot.toObjects(QuizResult::class.java)
                    historyList.clear()
                    historyList.addAll(results)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                binding.historyProgressBar.visibility = View.GONE
                Log.e("HistoryActivity", "Erro ao buscar histórico", e)
                Toast.makeText(this, "Erro ao carregar o histórico.", Toast.LENGTH_SHORT).show()
            }
    }
}