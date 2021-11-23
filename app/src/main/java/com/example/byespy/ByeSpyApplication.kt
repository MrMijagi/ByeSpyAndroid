package com.example.byespy

import android.app.Application
import com.example.byespy.data.AppDatabase

class ByeSpyApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}