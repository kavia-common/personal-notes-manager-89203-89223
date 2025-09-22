package org.example.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * PUBLIC_INTERFACE
 * A note entity stored in Room.
 */
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val updatedAt: Long = Date().time
)
