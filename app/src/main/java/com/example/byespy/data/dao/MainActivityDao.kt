package com.example.byespy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.model.ConversationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MainActivityDao {
    @Query(
        "SELECT conversation_table.id AS id, " +
                "conversation_table.name AS title " +
                "FROM conversation_table"
    )
    fun getAllItems(): Flow<List<ConversationItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conversation: Conversation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact: Contact): Long
}