package com.example.taskclient

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/TaskItem")
    fun getTasks(): Call<List<Task>>

    @GET("api/TaskItem/{id}")
    fun getTask(@Path("id") taskId: Int): Call<Task>

    @POST("api/TaskItem")
    fun createTask(@Body task: Task): Call<Task>

    @PUT("api/TaskItem/{id}")
    fun updateTask(@Path("id") taskId: Int, @Body task: Task): Call<Task>

    @DELETE("api/TaskItem/{id}")
    fun deleteTask(@Path("id") taskId: Int): Call<Unit>

    @POST("api/User")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/User/Login")
    fun checkUserExists(@Query("email") email: String, @Query("password") password: String): Call<Boolean>

}
