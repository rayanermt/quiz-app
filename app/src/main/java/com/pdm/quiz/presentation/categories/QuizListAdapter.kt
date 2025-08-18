package com.pdm.quiz.presentation.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.QuizItemRecyclerRowBinding
import com.pdm.quiz.domain.model.Category

class QuizListAdapter(
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {

    private val items = mutableListOf<Category>()

    @SuppressLint("NotifyDataSetChanged")
    fun submit(newItems: List<Category>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: QuizItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Category, onClick: (Category) -> Unit) {
            binding.quizTitleText.text = model.name
            binding.quizSubtitleText.text = "5 questões"
            binding.quizTimeText.text = "10 min"
            binding.root.setOnClickListener { onClick(model) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(items[position], onClick)
}
