package com.example.cookshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cookshare.model.Model
import com.example.cookshare.model.Post

class PostsViewModel : ViewModel() {

    val posts: LiveData<List<Post>> = Model.instance.getAllPostsLocal()

    fun refreshPosts(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.fetchAllPostsFromFirestore(onSuccess, onFailure)
    }

    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.addPost(post, onSuccess, onFailure)
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.deletePost(postId, onSuccess, onFailure)
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Model.instance.updatePost(post, onSuccess, onFailure)
    }
}