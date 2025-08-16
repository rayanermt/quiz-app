package com.pdm.quiz

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue // IMPORTAÇÃO NECESSÁRIA
import com.google.firebase.firestore.FirebaseFirestore
import com.pdm.quiz.databinding.ActivityQuizBinding
import com.pdm.quiz.databinding.ScoreDialogBinding
import java.util.Date

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var questionList: List<Question>

    private var quizId: String? = null
    private var quizName: String? = null

    private var currentQuestionIndex = 0
    private var selectedAnswerIndex = -1
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizId = intent.getStringExtra("quizId")
        quizName = intent.getStringExtra("quizName")
        questionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("questionList", Question::class.java)
        } else {
            intent.getParcelableArrayListExtra("questionList")
        } ?: emptyList()

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        loadQuestions()
    }

    private fun loadQuestions() {
        selectedAnswerIndex = -1
        if (currentQuestionIndex == questionList.size) {
            finishQuiz()
            return
        }
        resetButtonColors()
        binding.apply {
            questionIndicatorTextview.text = "Pergunta ${currentQuestionIndex + 1}/${questionList.size}"
            questionProgressIndicator.progress =
                ((currentQuestionIndex.toFloat() / questionList.size.toFloat()) * 100).toInt()
            questionTextview.text = questionList[currentQuestionIndex].text
            btn0.text = questionList[currentQuestionIndex].options[0]
            btn1.text = questionList[currentQuestionIndex].options[1]
            btn2.text = questionList[currentQuestionIndex].options[2]
            btn3.text = questionList[currentQuestionIndex].options[3]
        }
    }

    override fun onClick(view: View?) {
        val clickedBtn = view as Button
        if (clickedBtn.id == R.id.next_btn) {
            if (selectedAnswerIndex == -1) {
                Toast.makeText(applicationContext, "Por favor, escolha uma resposta.", Toast.LENGTH_SHORT).show()
                return
            }
            if (selectedAnswerIndex == questionList[currentQuestionIndex].correctIndex) {
                score++
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            resetButtonColors()
            clickedBtn.setBackgroundColor(getColor(R.color.purple_100))
            selectedAnswerIndex = when (clickedBtn.id) {
                R.id.btn0 -> 0
                R.id.btn1 -> 1
                R.id.btn2 -> 2
                R.id.btn3 -> 3
                else -> -1
            }
        }
    }

    private fun resetButtonColors() {
        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.grey))
            btn1.setBackgroundColor(getColor(R.color.grey))
            btn2.setBackgroundColor(getColor(R.color.grey))
            btn3.setBackgroundColor(getColor(R.color.grey))
        }
    }

    private fun finishQuiz() {
        val totalQuestions = questionList.size
        val percentage = if (totalQuestions > 0) {
            ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()
        } else { 0 }

        saveQuizResult(totalQuestions, percentage)

        val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if (percentage > 60) {
                scoreTitle.text = "Parabéns! Você passou no quiz 🎉"
                scoreTitle.setTextColor(Color.GREEN)
            } else {
                scoreTitle.text = "Opa, você não passou no quiz 😓"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$score de $totalQuestions perguntas corretas!"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()
    }

    private fun saveQuizResult(totalQuestions: Int, percentage: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado para salvar o resultado.", Toast.LENGTH_SHORT).show()
            return
        }

        val result = QuizResult(
            quizId = quizId ?: "",
            quizName = quizName ?: "",
            score = this.score,
            totalQuestions = totalQuestions,
            percentage = percentage
        )

        val db = FirebaseFirestore.getInstance()

        // Salva o resultado individual no histórico do usuário
        db.collection("users").document(currentUser.uid)
            .collection("quiz_history")
            .add(result)
            .addOnSuccessListener {
                Log.d("QuizActivity", "Resultado salvo com sucesso no histórico!")
            }
            .addOnFailureListener { e ->
                Log.e("QuizActivity", "Erro ao salvar resultado no histórico", e)
                Toast.makeText(this, "Erro ao salvar o resultado.", Toast.LENGTH_SHORT).show()
            }

        // Incrementa o placar total do usuário
        val userDocRef = db.collection("users").document(currentUser.uid)
        userDocRef.update("totalScore", FieldValue.increment(this.score.toLong()))
            .addOnSuccessListener {
                Log.d("QuizActivity", "Placar total atualizado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("QuizActivity", "Erro ao atualizar placar total", e)
            }
    }
}