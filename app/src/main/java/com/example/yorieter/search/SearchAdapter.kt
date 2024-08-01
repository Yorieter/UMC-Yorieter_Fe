package com.example.yorieter.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.databinding.ItemChipSearchBinding
import com.google.android.material.chip.Chip

class SearchAdapter(private val context: Context, private val searchTerms: List<String>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChipSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChipSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chipText = searchTerms[position]
        val chip = Chip(context).apply {
            text = chipText
        }
        holder.binding.chipContainer.removeAllViews()
        holder.binding.chipContainer.addView(chip)
    }

    override fun getItemCount() = searchTerms.size
}
