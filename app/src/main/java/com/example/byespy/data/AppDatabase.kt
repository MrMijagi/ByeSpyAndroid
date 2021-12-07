package com.example.byespy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.byespy.data.dao.*
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.entity.Device
import com.example.byespy.data.entity.Message

@Database(entities = [
    Contact::class,
    Conversation::class,
    Message::class,
    Device::class
                     ], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactActivityDao(): ContactActivityDao
    abstract fun chatActivityDao(): ChatActivityDao
    abstract fun mainActivityDao(): MainActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}