package com.pdm.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var quizCategoryList: MutableList<QuizCategory>
    private lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizCategoryList = mutableListOf()
        setupRecyclerView()
        getDataFromFirebase()
        setupLogoutButton()

        // Botão para Ranking
        binding.rankBtn.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        // Botão para Histórico
        binding.statsBtn.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizCategoryList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        // Passo 1: Buscar as categorias principais (Cinema, História, etc.)
        db.collection("categories")
            .get()
            .addOnSuccessListener { categoriesSnapshot ->
                if (categoriesSnapshot.isEmpty) {
                    // Se não encontrar nenhuma categoria, para aqui.
                    binding.progressBar.visibility = View.GONE
                    return@addOnSuccessListener
                }

                quizCategoryList.clear()
                val categories = categoriesSnapshot.toObjects(QuizCategory::class.java)
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                // Contador para saber quando todas as buscas de questões terminaram
                var categoriesProcessed = 0

                // Passo 2: Para cada categoria, buscar a subcoleção "questions"
                categories.forEach { category ->
                    db.collection("categories").document(category.id)
                        .collection("questions")
                        .get()
                        .addOnSuccessListener { questionsSnapshot ->
                            // Converte os documentos da subcoleção em uma lista de objetos Question
                            val questions = questionsSnapshot.toObjects(Question::class.java)
                            category.questions = questions // Atribui a lista de questões à categoria
                            quizCategoryList.add(category) // Adiciona a categoria completa à lista final

                            categoriesProcessed++
                            // Quando processar a última categoria, atualiza a tela
                            if (categoriesProcessed == categories.size) {
                                adapter.notifyDataSetChanged()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Se falhar em buscar as questões de uma categoria
                            Log.e("MainActivity", "Erro ao buscar questões para ${category.name}", exception)
                            categoriesProcessed++
                            if (categoriesProcessed == categories.size) {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Se falhar em buscar as categorias
                binding.progressBar.visibility = View.GONE
                Log.e("MainActivity", "Erro ao buscar categorias.", exception)
                Toast.makeText(this, "Falha ao carregar os quizzes.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupLogoutButton() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logoutBtn.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}