package com.example.taskclient

data class User(
    val userId: Int,
    val username: String,
    val email: String,
    val password: String,
    val tasks: List<Task>?
)
