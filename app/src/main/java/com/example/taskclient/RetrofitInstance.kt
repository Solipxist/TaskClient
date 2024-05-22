package com.example.taskclient

import Iso8601OffsetDateTimeAdapter
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime

object RetrofitInstance {
    val baseUrl = "http://192.168.0.14:8090/" // Cambia esto a la URL de tu API

    val gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, Iso8601OffsetDateTimeAdapter())
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val taskApiService = retrofit.create(TaskApiService::class.java)
}
