package com.example.cookshare.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.cookshare.model.User
import java.util.UUID

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCurrentUser() {
        val firebaseUser = auth.currentUser ?: return
        _isLoading.value = true

        db.collection("users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                if (user != null) {
                    _user.value = user!!
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    fun uploadProfilePhoto(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "profiles/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUrl)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdate)

                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    db.collection("users")
                        .document(uid)
                        .update("profileImageUrl", downloadUrl.toString())
                        .addOnSuccessListener { onSuccess() }
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCurrentUserName(): String = auth.currentUser?.displayName ?: ""
    fun getCurrentUserEmail(): String = auth.currentUser?.email ?: ""
    fun getCurrentUserPhoto() = auth.currentUser?.photoUrl
}