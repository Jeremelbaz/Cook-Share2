package com.example.cookshare.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cookshare.model.local.Converters

@Entity(tableName = "posts")
@TypeConverters(Converters::class)
data class Post(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val preparationTime: String = "",
    val notes: String = "",
    val category: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImageUrl: String = "",
    val timestamp: Long = 0,
    val likes: List<String> = emptyList()
)