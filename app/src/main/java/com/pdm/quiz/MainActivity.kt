package com.pdm.quiz

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
// Importações necessárias para o Google Sign-In
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
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

        // --- INÍCIO DA LÓGICA DE LOGOUT ATUALIZADA ---

        // 1. Configura o cliente do Google Sign-In para poder deslogar
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logoutBtn.setOnClickListener {
            // 2. Faz o logout da Conta Google no dispositivo
            googleSignInClient.signOut().addOnCompleteListener {
                // 3. Faz o logout do Firebase
                FirebaseAuth.getInstance().signOut()

                // 4. Redireciona para a tela de login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        // --- FIM DA LÓGICA DE LOGOUT ATUALIZADA ---

        // Botão para Ranking
        binding.rankBtn.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        // Sua lógica original para o botão de histórico está aqui, caso precise
        binding.statsBtn.setOnClickListener {
            // Lógica do botão de histórico
        }
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        val listQuestionModel = mutableListOf<QuestionModel>()
        listQuestionModel.add(QuestionModel("Qual o diretor de Star Wars?", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))
        listQuestionModel.add(QuestionModel("Pergunta 2", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))
        listQuestionModel.add(QuestionModel("Pergunta 3", mutableListOf("1","Resposta Certa","3","4"), "Resposta Certa"))

        quizModelList.add(QuizModel("1", "Cinema", "Descrição do Quiz de Cinema", "20", listQuestionModel))
        quizModelList.add(QuizModel("2", "História", "Descrição do Quiz de Hostória", "10", emptyList()))
        quizModelList.add(QuizModel("3", "Ciências", "Descrição do Quiz de Ciências", "15", emptyList()))

        adapter.notifyDataSetChanged()
    }
}