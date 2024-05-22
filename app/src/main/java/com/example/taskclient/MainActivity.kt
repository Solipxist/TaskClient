package com.example.taskclient

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: TaskApiService
    private lateinit var sharedPreferences: SharedPreferences

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
        apiService = RetrofitInstance.taskApiService

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val userName = sharedPreferences.getString("username", "")
        val email = sharedPreferences.getString("email", "")

        if (userId == 0) {
            with(sharedPreferences.edit()) {
                remove("userId")
                apply()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        Log.d("userData", userId.toString() +  " " + userName + " " + email)
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList())
        recyclerView.adapter = taskAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // Load user and tasks for the logged in user
        if (userId != -1) {
            loadUserAndTasks(userId)
        } else {
            Log.e("MainActivity", "User ID not found in SharedPreferences")
        }
    }

    private fun loadUserAndTasks(userId: Int) {
        val call = apiService.getUser(userId)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Log.d("tasks", user.tasks.toString())
                        user.tasks?.let { taskAdapter.updateTasks(it) }
                    } else {
                        Log.e("MainActivity", "Error: User object is null")
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error fetching user: ${t.message}", t)
            }
        })
    }
}

