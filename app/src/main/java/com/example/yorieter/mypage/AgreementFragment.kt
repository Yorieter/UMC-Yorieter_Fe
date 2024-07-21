package com.example.yorieter.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yorieter.databinding.FragmentAgreementBinding

class AgreementFragment: Fragment() {

    lateinit var binding: FragmentAgreementBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAgreementBinding.inflate(inflater, container, false)

        return binding.root
    }
}