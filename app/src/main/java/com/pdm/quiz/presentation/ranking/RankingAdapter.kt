package com.pdm.quiz.presentation.ranking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.RankingItemRecyclerRowBinding
import com.pdm.quiz.domain.model.LeaderboardEntry

class RankingAdapter(
    private val items: MutableList<LeaderboardEntry> = mutableListOf()
) : RecyclerView.Adapter<RankingAdapter.MyViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submit(newItems: List<LeaderboardEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: RankingItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: LeaderboardEntry, position: Int) {
            binding.rankingPositionText.text = "${position + 1}º"
            binding.rankingNameText.text = entry.displayName.ifBlank { "Jogador" }
            binding.rankingScoreText.text = "${entry.totalScore} pts"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RankingItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(items[position], position)
}
