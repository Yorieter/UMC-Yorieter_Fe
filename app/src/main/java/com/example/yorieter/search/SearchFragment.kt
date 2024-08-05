package com.example.yorieter.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchFragment: Fragment(), SearchAdapter.FragmentCallback {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private val PREFS_NAME = "recent_searches"
    private val KEY_SEARCHES = "key_searches"

    // 수정 01
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // 최신 검색어
        val searchView: SearchView = binding.searchView
        binding.searchRecipeRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = SearchAdapter(requireContext(), recentSearches, this)
        binding.searchRecipeRV.adapter = adapter

        // SharedPreferences에서 최근 검색어 불러오기
        loadRecentSearches()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    addRecentSearch(query)
                    adapter.notifyDataSetChanged()
                    navigateToSearchResult(query)
                }

                return false // 키보드 검색 아이콘 클릭 시 키보드 내림
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

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

            chip.setOnCloseIconClickListener {
                ingredientsChipGroup.removeView(chip)
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
            val query = searchView.query.toString()
            if (query.isNotEmpty()) {
                addRecentSearch(query)
                adapter.notifyDataSetChanged()
                navigateToSearchResult(query)
            }
        }

        // 화면 클릭 시 키보드 내리기
        binding.root?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard(v)
            }
            false
        }

        return binding.root
    }

    override fun onSearchTermClicked(query: String) {
        val bundle = Bundle().apply {
            putString("query", query)
        }
        val searchResultFragment = SearchResultFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, searchResultFragment)
            .addToBackStack("SearchResultFragment") // 백 스택 추가
            .commitAllowingStateLoss()
    }

    private fun navigateToSearchResult(query: String) {
        val selectedChips = binding.ingredientsChipGroup.checkedChipIds.joinToString(", ") { id ->
            binding.ingredientsChipGroup.findViewById<Chip>(id).text
        }

        val minCalories = binding.minCalories.text.toString()
        val maxCalories = binding.maxCalories.text.toString()

        setMinMaxFilter(binding.minCalories, 200, 500)
        setMinMaxFilter(binding.maxCalories, 200, 500)

        val bundle = Bundle().apply {
            putString("query", query)
            putString("selectedChips", selectedChips)
            putString("minCalories", minCalories)
            putString("maxCalories", maxCalories)
        }
        val searchResultFragment = SearchResultFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, searchResultFragment)
            .addToBackStack("SearchResultFragment") // 백 스택 추가
            .commitAllowingStateLoss()
    }

    private fun addRecentSearch(search: String) {
        if (recentSearches.size >= 5) {
            recentSearches.removeAt(0)
        }
        recentSearches.add(search)
        saveRecentSearches()
    }

    private fun saveRecentSearches() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val searchSet = recentSearches.toSet()
        editor.putStringSet(KEY_SEARCHES, searchSet)
        editor.apply()
    }

    private fun loadRecentSearches() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val searchSet = sharedPreferences.getStringSet(KEY_SEARCHES, setOf()) ?: setOf()
        recentSearches.clear()
        recentSearches.addAll(searchSet)
    }

    fun setMinMaxFilter(editText: EditText, minValue: Int, maxValue: Int) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                if (input.isNotEmpty()) {
                    val value = input.toInt()
                    if (value < minValue || value > maxValue) {
                        editText.setText("")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // 키보드 숨기기
    fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
