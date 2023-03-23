package com.ayustark.todoreminder.db

import androidx.room.Entity
import androidx.room.Ignore
import java.text.SimpleDateFormat
import java.util.*

@Entity("note_tb")
data class NoteEntity(
    var title: String,
    var description: String,
    var dueDate: String,
    val creationDate: String = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault()).format(Date(System.currentTimeMillis())),

    @Ignore
    var expanded: Boolean = false,
    @Ignore
    var clicked: Boolean = false
)