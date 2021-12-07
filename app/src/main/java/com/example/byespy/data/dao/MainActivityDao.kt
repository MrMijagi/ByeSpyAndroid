package com.example.byespy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.byespy.data.entity.Contact
import com.example.byespy.data.entity.Conversation
import com.example.byespy.data.entity.Message
import com.example.byespy.data.model.ConversationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MainActivityDao {
    // one query to fetch them all
    @Query(
        "SELECT conversation_table.id AS id, " +
                "contact_table.email AS email, " +
                "contact_table.username AS username, " +
                "contact_table.image AS image, " +
                "message_table.content AS lastMessage, " +
                "message_table.is_own_message AS lastMessageOwn " +
                "FROM conversation_table " +
                "INNER JOIN contact_table ON contact_table.id = conversation_table.contact_id " +
                "INNER JOIN ( " +
                "   SELECT message_table.conversation_id AS conversation_id, " +
                "          MIN(message_table.sent_at) AS sent_at " +
                "   FROM message_table " +
                "   GROUP BY message_table.conversation_id " +
                ") message_grouped ON conversation_table.id = message_grouped.conversation_id " +
                "INNER JOIN message_table ON message_table.conversation_id = conversation_table.id " +
                "                        AND message_table.sent_at = message_grouped.sent_at"
    )
    fun getAllItems(): Flow<List<ConversationItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(conversation: Conversation): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact: Contact): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: Message): Long
}