package com.example.cookshare

import android.app.Application
import com.example.cookshare.model.Model
import com.example.cookshare.model.local.AppLocalDb

class MyApplication : Application() {

    companion object {
        lateinit var database: AppLocalDb
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = AppLocalDb.getDatabase(this)
        Model.init(this)
    }
}