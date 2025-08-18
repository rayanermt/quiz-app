package com.pdm.quiz.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.HistoryItemRecyclerRowBinding
import com.pdm.quiz.domain.model.SessionItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private val items = mutableListOf<SessionItem>()
    fun submit(newItems: List<SessionItem>) {
        items.clear(); items.addAll(newItems); notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: HistoryItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", Locale("pt", "BR")).apply {
            timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
        }

        fun bind(it: SessionItem) {
            binding.historyQuizTitle.text = it.quizName
            // Exibe "Pontuação: 3 de 5 • 2:31"
            binding.historyScoreText.text = "Pontuação: ${it.correctCount} de ${it.total} • ${formatDuration(it.durationMs)}"
            binding.historyDateText.text = dateFormat.format(Date(it.completedAtMs))
        }

        private fun formatDuration(ms: Long): String {
            val totalSec = ms / 1000
            val min = totalSec / 60
            val sec = totalSec % 60
            return String.format("%d:%02d", min, sec)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(HistoryItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(items[position])
}
