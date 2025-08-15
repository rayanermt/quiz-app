package com.pdm.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.RankingItemRecyclerRowBinding

data class RankingItem(val position: Int, val name: String, val score: Int)

class RankingAdapter(private var rankingList: MutableList<RankingItem>) :
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    inner class RankingViewHolder(val binding: RankingItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val binding = RankingItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = rankingList[position]
        holder.binding.tvPosition.text = item.position.toString()
        holder.binding.tvName.text = item.name
        holder.binding.tvScore.text = item.score.toString()
    }

    override fun getItemCount() = rankingList.size

    fun updateData(newList: List<RankingItem>) {
        rankingList.clear()
        rankingList.addAll(newList)
        notifyDataSetChanged()
    }
}
