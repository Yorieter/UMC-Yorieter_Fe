package com.example.yorieter.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentSearchResultBinding
import com.google.android.material.chip.Chip

class SearchResultFragment: Fragment() {
    lateinit var binding: FragmentSearchResultBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        // 선택한 chips 나타나게 함 / 선택하지 않았으면 안나타나게
        val selectedChips = arguments?.getString("selectedChips")

        if (selectedChips.isNullOrEmpty()) {
            binding.filteredChipGroup.visibility = View.GONE
        } else {
            selectedChips.split(", ").forEach { chipText ->
                val chip = Chip(requireContext()).apply {
                    text = chipText
                    isClickable = false
                    chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1) // 배경 컬러 설정
                }
                binding.filteredChipGroup.addView(chip)
            }
        }

        // 검색어 불러오기
        val searchWord = arguments?.getString("query")

        if (searchWord.isNullOrEmpty()) {
            binding.searchWordTv.visibility = View.GONE
        }
        else {
            binding.searchWordTv.text = "$searchWord"
        }

        // 칼로리 불러오기 / 선택하지 않았으면 안보이게ㅁㅇ
        val minCalories = arguments?.getString("minCalories")
        val maxCalories = arguments?.getString("maxCalories")

        if (minCalories.isNullOrEmpty() && maxCalories.isNullOrEmpty()) {
            binding.searchCaloriesTv.visibility = View.GONE
        }
        else if (minCalories.isNullOrEmpty() && !maxCalories.isNullOrEmpty()) {
            binding.searchCaloriesTv.text = "0 Kcal ~ $maxCalories Kcal"
        }
        else if (!minCalories.isNullOrEmpty() && maxCalories.isNullOrEmpty()) {
            binding.searchCaloriesTv.text = "$minCalories Kcal ~ 500 Kcal"
        }
        else {
            binding.searchCaloriesTv.text = "$minCalories Kcal ~ $maxCalories Kcal" // 칼로리
        }

        // 뒤로가기 버튼을 누르면 다시 검색창으로 돌아감
        binding.searchBackIV.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, SearchFragment())
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}
