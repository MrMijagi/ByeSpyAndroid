package com.example.byespy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.byespy.data.dao.ContactDao
import com.example.byespy.data.dao.ConversationDao
import com.example.byespy.data.dao.MessageDao
import com.example.byespy.data.dao.ThreadDao
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.entity.Message
import com.example.byespy.data.entity.Thread

@Database(entities = [
    Contact::class,
    Conversation::class,
    Message::class,
    Thread::class
                     ], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun threadDao(): ThreadDao

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