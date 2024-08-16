package com.example.yorieter.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemChipSearchBinding
import com.google.android.material.chip.Chip

class SearchWordAdapter(private val context: Context,
                        private val searchTerms: MutableList<String>,
                        private val fragmentCallback: SearchFragment // 인터페이스 추가
): RecyclerView.Adapter<SearchWordAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChipSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChipSearchBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chipText = searchTerms[position]
        val chip = Chip(context).apply {
            text = chipText
            isCloseIconVisible = true
            chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1)
        }

        chip.setOnCloseIconClickListener {
            removeSearchTerm(position)
        }

        // chip 선택 시 그 결과 보여주기
        chip.setOnClickListener { isChecked ->
            if (chip.isChecked) {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.mainColor)
            }
            else {
                chip.chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1)
            }
            fragmentCallback.onSearchTermClicked(chipText)
        }

        holder.binding.chipContainer.removeAllViews()
        holder.binding.chipContainer.addView(chip)
    }

    override fun getItemCount() = searchTerms.size

    private fun removeSearchTerm(position: Int) {
        searchTerms.removeAt(position)  // 목록에서 항목 제거
        notifyItemRemoved(position)
        updateSharedPreferences()
    }

    private fun updateSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("recent_searches", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("key_searches", searchTerms.toSet())
        editor.apply()
    }

    interface FragmentCallback {
        fun onSearchTermClicked(query: String)
    }
}