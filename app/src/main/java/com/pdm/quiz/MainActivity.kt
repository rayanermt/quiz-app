package com.pdm.quiz
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdm.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        setupRecyclerView()
        getDataFromFirebase()

        // Botão para Ranking
        binding.rankBtn.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        /* Botão para Histórico
        binding.statsBtn.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }*/
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Utilizando dados de teste
    private fun getDataFromFirebase() {
        val listQuestionModel = mutableListOf<QuestionModel>()
        listQuestionModel.add(QuestionModel("Qual o diretor de Star Wars?", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))
        listQuestionModel.add(QuestionModel("Pergunta 2", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))
        listQuestionModel.add(QuestionModel("Pergunta 3", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))

        quizModelList.add(QuizModel("1", "Cinema", "Descrição do Quiz de Cinema", "20", listQuestionModel))
        quizModelList.add(QuizModel("2", "História", "Descrição do Quiz de Hostória", "10", emptyList()))
        quizModelList.add(QuizModel("3", "Ciências", "Descrição do Quiz de Ciências", "15", emptyList()))

        setupRecyclerView()
    }
}
