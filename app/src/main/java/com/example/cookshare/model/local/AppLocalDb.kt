package com.example.cookshare.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cookshare.model.Post
import com.example.cookshare.model.User
import com.example.cookshare.model.dao.PostDao
import com.example.cookshare.model.dao.UserDao

@Database(entities = [Post::class, User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppLocalDb : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppLocalDb? = null

        fun getDatabase(context: Context): AppLocalDb {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppLocalDb::class.java,
                    "cookshare_database"
                ).build()
                instance = db
                db
            }
        }
    }
}