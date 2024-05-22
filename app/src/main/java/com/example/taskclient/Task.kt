package com.example.taskclient


import java.time.OffsetDateTime

data class Task(
    val taskId: Int,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime,
    val dueDate: OffsetDateTime?,
    var completed: Boolean,
    val userId: Int
)
