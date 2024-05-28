package com.example.taskclient


import java.time.OffsetDateTime

data class Task(
    val taskId: Int,
    var title: String,
    var description: String,
    val creationDate: OffsetDateTime,
    var dueDate: OffsetDateTime?,
    var completed: Boolean,
    val userId: Int
)
