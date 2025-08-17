package com.pdm.quiz

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.QuizItemRecyclerRowBinding

class QuizListAdapter(private val quizCategoryList: List<QuizCategory>) :
    RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: QuizItemRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: QuizCategory) {
            binding.apply {
                quizTitleText.text = model.name
                quizSubtitleText.text = "${model.questions.size} questões"
                quizTimeText.text = "10 min" // Exemplo estático

                root.setOnClickListener {
                    val intent = Intent(root.context, QuizActivity::class.java)

                    // ATUALIZAÇÃO AQUI: Passamos também o ID e o Nome do quiz
                    intent.putExtra("quizId", model.id)
                    intent.putExtra("quizName", model.name)
                    intent.putParcelableArrayListExtra("questionList", ArrayList(model.questions))

                    root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizCategoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizCategoryList[position])
    }
}