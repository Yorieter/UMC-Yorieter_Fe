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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yorieter.R
import com.example.yorieter.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchFragment: Fragment(), SearchWordAdapter.FragmentCallback {

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
        val searchView: androidx.appcompat.widget.SearchView = binding.searchView
        binding.searchRecipeRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = SearchWordAdapter(requireContext(), recentSearches, this)
        binding.searchRecipeRV.adapter = adapter

        // SharedPreferences에서 최근 검색어 불러오기
        loadRecentSearches()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
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

        // 재료 chip 적용
        val ingredientsChipGroup: ChipGroup = binding.ingredientsChipGroup
        val testList = listOf("계란", "토마토", "새우", "마늘", "통밀빵", "닭고기", "돼지 고기", "상추", "배추", "당근", "파프리카", "감자", "오리고기",
            "아보카도", "두부면", "닭가슴살", "오트밀", "우유", "청경채", "무", "양파", "브로콜리", "양상추", "오이", "양배추")

        // chipGroup 설정
        testList.forEachIndexed { index, s ->
            val chip: Chip = Chip(requireContext()).apply {
                text = s
                id = index
                isCheckable = true  // Chip을 선택 가능하게 설정
                checkedIcon
                chipBackgroundColor = ContextCompat.getColorStateList(this.context, R.color.subColor1)
            }

            chip.setOnClickListener { isChecked ->
                if (chip.isChecked) {
                    chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.mainColor)
                }
                else {
                    chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.subColor1)
                }
            }

            ingredientsChipGroup.addView(chip)
        }

        // 뷰 펼치기 닫기
        binding.selectIngredientsIV.setOnClickListener {
            if (binding.ingredientsChipGroup.visibility == View.GONE) {
                ingredientsChipGroup.visibility = View.VISIBLE
                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_up)
            } else {
                ingredientsChipGroup.visibility = View.GONE
                binding.selectIngredientsIV.setImageResource(R.drawable.arrow_down)
            }
        }

        // 임시로 검색 버튼 누르면 search_result_view로 넘어감
        binding.searchBtn.setOnClickListener { // 검색어 검색
            val query = searchView.query.toString()
            if (query.isNotEmpty()) {
                addRecentSearch(query)
                adapter.notifyDataSetChanged()

//                val selectedChips = binding.ingredientsChipGroup.checkedChipIds.joinToString(", ") { id ->
//                    binding.ingredientsChipGroup.findViewById<Chip>(id).text
//                }

                val bundle = Bundle().apply {
                    putString("query", query)
                    // putString("selectedChips", selectedChips)
                }
                val searchResultFragment = SearchResultFragment().apply {
                    arguments = bundle
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, searchResultFragment)
                    .addToBackStack("SearchResultFragment") // 백 스택 추가
                    .commitAllowingStateLoss()
            }
            else { // 재료 필터링 검색
                val selectedChips = binding.ingredientsChipGroup.checkedChipIds.joinToString(", ") { id ->
                    binding.ingredientsChipGroup.findViewById<Chip>(id).text
                }

                val bundle = Bundle().apply {
                    putString("selectedChips", selectedChips)
                }
                val searchResultFragment = SearchResultFragment().apply {
                    arguments = bundle
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, searchResultFragment)
                    .addToBackStack("SearchResultFragment") // 백 스택 추가
                    .commitAllowingStateLoss()
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

        val bundle = Bundle().apply {
            putString("query", query)
            putString("selectedChips", selectedChips)
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

    // 키보드 숨기기
    fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}