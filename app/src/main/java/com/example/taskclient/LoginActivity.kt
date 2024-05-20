package com.example.taskclient

import android.content.Intent
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

        apiService.checkUserExists(email, password).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val userExists = response.body() ?: false

                    if (userExists) {
                        // El usuario existe, iniciar MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // El usuario no existe o las credenciales son incorrectas
                        Log.e("LoginActivity", "Usuario no encontrado o credenciales incorrectas")
                        Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Error en la solicitud
                    Log.e("LoginActivity", "Error en la solicitud: ${response.message()}")
                    Toast.makeText(this@LoginActivity, "Failed to check user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Error en la conexión
                Log.e("LoginActivity", "Error de conexión: ${t.message}")
                Toast.makeText(this@LoginActivity, "Connection error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
