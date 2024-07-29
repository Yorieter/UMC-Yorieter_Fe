package com.example.yorieter.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentSearchBinding
import com.example.yorieter.home.HomeFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.lang.StringBuilder

class SearchFragment: Fragment() {

    lateinit var binding: FragmentSearchBinding

    // 수정 01
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // chip 적용
        val ingredientsChipGroup: ChipGroup = binding.ingredientsChipGroup
        val testTV: TextView = binding.chipTestTV

        val testList = listOf("닭가슴살", "브로콜리", "감자", "당근", "양파", "토마토", "파프리카")

        // chipGroup 설정
        testList.forEachIndexed { index, s ->
            val chip: Chip = Chip(requireContext()).apply {
                text = s
                id = index
                isCheckable = true  // Chip을 선택 가능하게 설정
            }
            ingredientsChipGroup.addView(chip)
        }

        ingredientsChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                // 아무것도 선택 안하면 동작 안함
                testTV.text = ""
            }
            else {
                val selectedIngredients = checkedIds.joinToString(", ") { id ->
                    group.findViewById<Chip>(id).text
                }
                testTV.text = selectedIngredients
            }
        }

        // 뷰 펼치기 닫기
        binding.selectIngredientsIV.setOnClickListener {
            if (binding.ingredientsChipGroup.visibility == View.GONE) {
                ingredientsChipGroup.visibility = View.VISIBLE
                testTV.visibility = View.VISIBLE
                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_up)
            } else {
                ingredientsChipGroup.visibility = View.GONE
                testTV.visibility = View.GONE
                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_down)
            }
        }

        binding.selectCaloriesIV.setOnClickListener {
            if (binding.caloriesOptions.visibility == View.GONE) {
                binding.caloriesOptions.visibility = View.VISIBLE
                binding.selectCaloriesIV.setImageResource(R.drawable.arrow_up)
            } else {
                binding.caloriesOptions.visibility = View.GONE
                binding.selectCaloriesIV.setImageResource(R.drawable.arrow_down)
            }
        }

        // 임시로 검색 버튼 누르면 search_result_view로 넘어감
        binding.searchBtn.setOnClickListener {
            val selectedChips = ingredientsChipGroup.checkedChipIds.joinToString(", ") { id ->
                ingredientsChipGroup.findViewById<Chip>(id).text
            }
            val bundle = Bundle().apply {
                putString("selectedChips", selectedChips)
            }
            val searchResultFragment = SearchResultFragment().apply {
                arguments = bundle
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frm, searchResultFragment)
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}