package com.example.yorieter.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemChipSearchBinding
import com.google.android.material.chip.Chip

class SearchAdapter(private val context: Context,
                    private val searchTerms: MutableList<String>,
                    private val fragmentCallback: FragmentCallback // 인터페이스 추가
): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChipSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChipSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chipText = searchTerms[position]
        val chip = Chip(context).apply {
            text = chipText
            this.setCloseIconResource(R.drawable.close)
            isCloseIconVisible = true
        }

        chip.setOnCloseIconClickListener {
            searchTerms.removeAt(position)
            notifyDataSetChanged()
        }

        // chip 선택 시 그 결과 보여주기
        chip.setOnClickListener {
            fragmentCallback.onSearchTermClicked(chipText)
        }

        holder.binding.chipContainer.removeAllViews()
        holder.binding.chipContainer.addView(chip)
    }

    override fun getItemCount() = searchTerms.size

    interface FragmentCallback {
        fun onSearchTermClicked(query: String)
    }
}
