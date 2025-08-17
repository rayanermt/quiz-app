package com.pdm.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.HistoryItemRecyclerRowBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HistoryAdapter(private val historyList: List<QuizResult>) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: HistoryItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: QuizResult) {
            binding.historyQuizTitle.text = result.quizName
            binding.historyScoreText.text = "Pontuação: ${result.score} de ${result.totalQuestions}"

            // Formata a data para o fuso do Brasil
            val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("pt", "BR"))
            dateFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

            binding.historyDateText.text = result.completedAt?.let { dateFormat.format(it) }
                ?: "Data indisponível"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = HistoryItemRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(historyList[position])
    }
}
