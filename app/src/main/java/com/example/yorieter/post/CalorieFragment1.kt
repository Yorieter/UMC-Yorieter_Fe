package com.example.yorieter.post

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.yorieter.R
import com.example.yorieter.databinding.Fragment1CalorieBinding

class CalorieFragment1: Fragment() {

    lateinit var binding: Fragment1CalorieBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = Fragment1CalorieBinding.inflate(inflater, container, false)

        // + 버튼 클릭 시 레이아웃 추가
        binding.caloriePlusIv.setOnClickListener {
            addNewEditTexts()
        }
        return binding.root
    }

    private fun addNewEditTexts(){ // 새로운 EditText를 동적으로 추가하는 함수

        // 새로운 ConstraintLayout 생성하여 EditText들을 담을 예정
        val newEditTextLayout = LinearLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 8, 0, 10) // 상단에 8dp 마진을 추가
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        // 기존 EditText들을 가져오기
        val existingEditTexts = binding.edittextContainer.children.toList() // children: 레이아웃의 모든 자식 뷰들을 나타냄
        val firstEditText = existingEditTexts.firstOrNull() as? EditText
        val layoutParams = if (firstEditText != null) {
            LinearLayout.LayoutParams(firstEditText.layoutParams).apply {
                // 기존 EditText가 있으면 해당 레이아웃 파라미터를 사용
                setMargins(15, 0, 10, 0)
            }
        } else {
            // 기존 EditText가 없으면 기본 레이아웃 파라미터 설정
            LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(15, 0, 10, 0)
            }
        }

        // 새로 생성한 레이아웃에 새로운 EditText들 추가
        newEditTextLayout.addView(createEditText("식재료", layoutParams, android.view.inputmethod.EditorInfo.IME_ACTION_NEXT))
        newEditTextLayout.addView(createEditText("g/ml", layoutParams, android.view.inputmethod.EditorInfo.IME_ACTION_NEXT))
        newEditTextLayout.addView(createEditText("칼로리(Kcal)", layoutParams, android.view.inputmethod.EditorInfo.IME_ACTION_DONE))

        // 새로 만든 레이아웃을 기존 calorieLayout에 추가
        binding.calorieLayout.addView(newEditTextLayout)

    }

    // 지정된 속성으로 EditText를 생성하는 함수
    private fun createEditText(hint: String, layoutParams: LinearLayout.LayoutParams, imeOptions: Int): EditText {
        return EditText(requireContext()).apply {
            this.hint = hint
            setHintTextColor(resources.getColor(R.color.gray, null))
            textSize = 10f
            val paddingLeftPx = dpToPx(10f, requireContext())
            setPadding(paddingLeftPx,0,0,0)
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            this.imeOptions = imeOptions
            setBackgroundResource(R.drawable.calorie_edittext_background_color)
            this.layoutParams = layoutParams
        }
    }

    // DP를 PX(픽셀)로 변환하는 함수
    private fun dpToPx(dp: Float, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}