package com.example.yorieter.mypage.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _introduction = MutableLiveData<String>()
    val introduction: LiveData<String> get() = _introduction

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> get() = _profileImageUri

    fun setNickname(nickname: String){
        _nickname.value = nickname
    }

    fun setIntroduction(introduction: String){
        _introduction.value = introduction
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }
}