package com.pdm.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdm.quiz.databinding.RankingItemRecyclerRowBinding

class RankingAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<RankingAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: RankingItemRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, position: Int) {
            binding.rankingPositionText.text = "${position + 1}º"
            binding.rankingNameText.text = user.displayName
            binding.rankingScoreText.text = "${user.totalScore} pts"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RankingItemRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userList[position], position)
    }
}