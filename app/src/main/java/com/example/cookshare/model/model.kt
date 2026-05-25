package com.example.cookshare.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.cookshare.model.dao.PostDao
import com.example.cookshare.model.local.AppLocalDb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class Model private constructor(context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val localDb = AppLocalDb.getDatabase(context)
    private val postDao: PostDao = localDb.postDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        @Volatile
        private var INSTANCE: Model? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = Model(context.applicationContext)
            }
        }

        val instance: Model
            get() = INSTANCE ?: throw IllegalStateException("Model not initialized. Call init() first.")
    }

    // ───── POSTS ─────

    fun getAllPostsLocal(): LiveData<List<Post>> {
        return postDao.getAllPosts()
    }

    fun addPost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .document(post.id)
            .set(post)
            .addOnSuccessListener {
                scope.launch { postDao.insertPost(post) }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updatePost(post: Post, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .document(post.id)
            .set(post)
            .addOnSuccessListener {
                scope.launch { postDao.insertPost(post) }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .document(postId)
            .delete()
            .addOnSuccessListener {
                scope.launch { postDao.deletePost(Post(id = postId)) }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun fetchAllPostsFromFirestore(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val posts = result.toObjects(Post::class.java)
                scope.launch { posts.forEach { postDao.insertPost(it) } }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ───── STORAGE ─────

    fun uploadPostImage(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "posts/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ───── COMMENTS ─────

    fun addComment(
        comment: Comment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("comments")
            .document(comment.id)
            .set(comment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCommentsForPost(
        postId: String,
        onSuccess: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                val comments = result.toObjects(Comment::class.java)
                onSuccess(comments)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ───── USERS ─────

    fun saveUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                scope.launch { localDb.userDao().insertUser(user) }
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ───── CURRENT USER ─────

    fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""

    fun getCurrentUserName(): String = auth.currentUser?.displayName ?: ""
}