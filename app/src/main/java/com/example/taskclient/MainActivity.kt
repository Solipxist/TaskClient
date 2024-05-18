package com.example.taskclient

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Retrofit and ApiService
        retrofit = RetrofitInstance.retrofit
        apiService = RetrofitInstance.api

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList())
        recyclerView.adapter = taskAdapter

        // Fetch tasks from the API
        fetchTasks()
    }

    private fun fetchTasks() {
        // Usa tu instancia Retrofit con Gson personalizado para hacer la llamada al servicio
        val call = RetrofitInstance.api.getTasks()
        Log.d(TAG, "onCreate called $call")

        call.enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    val tasks = response.body()
                    tasks?.let {
                        taskAdapter.updateTasks(it)
                        Log.d(TAG, "JSON response: $tasks")
                    }
                } else {
                    Log.e(TAG, "Failed to fetch tasks: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e(TAG, "Failed to fetch tasks", t)
            }
        })
    }



    companion object {
        private const val TAG = "MainActivity"
    }
}
