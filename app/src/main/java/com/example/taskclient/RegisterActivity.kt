package com.example.taskclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskclient.User
import com.example.taskclient.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RegisterActivity : AppCompatActivity() {
    private lateinit var ETName: EditText
    private lateinit var ETEmail: EditText
    private lateinit var ETPassword: EditText
    private lateinit var BTNRegister: Button
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ETName = findViewById(R.id.nameEditText)
        ETEmail = findViewById(R.id.emailEditText)
        ETPassword = findViewById(R.id.passwordEditText)
        BTNRegister=findViewById(R.id.registerButton)

        retrofit = RetrofitInstance.retrofit
        apiService=RetrofitInstance.api

        BTNRegister.setOnClickListener {
            val name = ETName.text.toString()
            val email = ETEmail.text.toString()
            val password = ETPassword.text.toString()
            registerUser(name, email, password)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val user = User(name = name, email = email, password = password, id = 0)


        apiService.registerUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Registro exitoso, iniciar LoginActivity
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}