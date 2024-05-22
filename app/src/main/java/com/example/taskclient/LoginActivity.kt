package com.example.taskclient

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskclient.User
import com.example.taskclient.RetrofitInstance
import org.mindrot.jbcrypt.BCrypt
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
    private lateinit var apiService: TaskApiService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("userId")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        ETEmail = findViewById(R.id.emailEditText)
        ETPassword = findViewById(R.id.passwordEditText)
        BTNLogin = findViewById(R.id.loginButton)
        TVRegistro = findViewById(R.id.TVRegister)

        retrofit = RetrofitInstance.retrofit
        apiService = RetrofitInstance.taskApiService

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

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
        val call = apiService.login(Auth.LoginRequest(email, password))

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        saveUserToSession(user)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Error: Usuario nulo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("LoginActivity", "Error al realizar la solicitud: ${t.message}", t)
                Toast.makeText(this@LoginActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserToSession(user: User) {
        val editor = sharedPreferences.edit()
        editor.putInt("userId", user.userId)
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.apply()
    }
}

