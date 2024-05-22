package com.example.taskclient

class Auth {
    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class AuthToken(
        val token: String
    )

}