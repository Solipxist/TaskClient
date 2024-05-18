package com.example.taskclient

import java.time.OffsetDateTime


data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val creationDate: OffsetDateTime, // Usa OffsetDateTime en lugar de LocalDateTime
    val dueDate: OffsetDateTime?,      // Usa OffsetDateTime en lugar de LocalDateTime
    val completed: Boolean
)
