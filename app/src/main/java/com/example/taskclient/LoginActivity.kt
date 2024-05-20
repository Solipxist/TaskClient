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

class LoginActivity : AppCompatActivity() {


    private lateinit var ETEmail: EditText
    private lateinit var ETPassword: EditText
    private lateinit var BTNLogin: Button
    private lateinit var TVRegistro: TextView
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ETEmail = findViewById(R.id.emailEditText)
        ETPassword = findViewById(R.id.passwordEditText)
        BTNLogin=findViewById(R.id.loginButton)
        TVRegistro=findViewById(R.id.TVRegister)

        retrofit = RetrofitInstance.retrofit
        apiService=RetrofitInstance.api

        TVRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        BTNLogin.setOnClickListener {
            val email = ETEmail.text.toString()
            val password = ETPassword.text.toString()
            loginUser(email, password)
        }


    }

    private fun loginUser(email: String, password: String) {
        val user = User(name = "", email = email, password = password,id=0)


        apiService.loginUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Guardar token o userId para futuras peticiones
                    // Iniciar MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
