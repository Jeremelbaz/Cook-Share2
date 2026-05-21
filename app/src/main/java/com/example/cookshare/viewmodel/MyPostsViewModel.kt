package com.example.cookshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.cookshare.model.Model
import com.example.cookshare.model.Post

class MyPostsViewModel : ViewModel() {

    private val currentUserId = Model.instance.getCurrentUserId()

    val myPosts: LiveData<List<Post>> = MediatorLiveData<List<Post>>().apply {
        addSource(Model.instance.getAllPostsLocal()) { posts ->
            value = posts.filter { it.userId == currentUserId }
        }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.deletePost(postId, onSuccess, onFailure)
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.updatePost(post, onSuccess, onFailure)
    }
}