package com.example.yorieter.post.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//데이터 클래스 생성
data class Post(val title: String, val context: String, val imageUri: Uri)
class PostViewModel : ViewModel(){
    private val _post = MutableLiveData<List<Post>>().apply { value = mutableListOf() }
    val posts: LiveData<List<Post>> get() = _post

    private val _postTitle = MutableLiveData<String>()
    val postName: LiveData<String> get() = _postTitle

    private val _postContext = MutableLiveData<String>()
    val postContext: LiveData<String> get() = _postContext

    private val _postImageUri = MutableLiveData<Uri>()
    val postImageUri: LiveData<Uri> get() = _postImageUri

    fun setPostTitle(nickname: String){
        _postTitle.value = nickname
    }

    fun setPostContext(introduction: String){
        _postContext.value = introduction
    }
    fun setPostImageUri(uri: Uri) {
        _postImageUri.value = uri
    }

    fun addPost() {
        val title = _postTitle.value ?: return
        val context = _postContext.value ?: return
        val uri = _postImageUri.value ?: return

        val newPost = Post(title, context, uri)
        val updatedPosts = _post.value?.toMutableList()
        updatedPosts?.add(newPost)
        _post.value = updatedPosts
    }
}

