package com.example.taskclient

import retrofit2.Call
import retrofit2.http.*

interface TaskApiService {

    // Endpoints para la entidad Task

    @GET("/api/task")
    fun getTasks(): Call<List<Task>>

    @GET("/api/task/{id}")
    fun getTask(@Path("id") taskId: Int): Call<Task>

    @POST("/api/task")
    fun createTask(@Body task: Task): Call<Task>

    @PUT("/api/task/{id}")
    fun updateTask(@Path("id") taskId: Int, @Body task: Task): Call<Task>

    @DELETE("/api/task/{id}")
    fun deleteTask(@Path("id") taskId: Int): Call<Void>

    // Endpoints para la entidad User (si están disponibles)

    @GET("/api/user")
    fun getUsers(): Call<List<User>>

    @GET("/api/user/{id}")
    fun getUser(@Path("id") userId: Int): Call<User>

    @POST("/api/user")
    fun createUser(@Body user: User): Call<User>

    @PUT("/api/user/{id}")
    fun updateUser(@Path("id") userId: Int, @Body user: User): Call<User>

    @DELETE("/api/user/{id}")
    fun deleteUser(@Path("id") userId: Int): Call<Void>

    @POST("/api/auth/login")
    fun login(@Body loginRequest: Auth.LoginRequest): Call<User>

}
