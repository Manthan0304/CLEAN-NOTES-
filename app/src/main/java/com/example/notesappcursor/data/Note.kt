package com.example.notesappcursor.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val timestamp: LocalDateTime = LocalDateTime.now()
) 