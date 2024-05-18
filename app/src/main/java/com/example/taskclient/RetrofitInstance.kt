package com.example.taskclient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.LocalDateTime
import java.time.OffsetDateTime

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.14:8095/"

    val gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, JsonDeserializer { json, _, _ ->
            OffsetDateTime.parse(json.asString)
        })
        .create()

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
