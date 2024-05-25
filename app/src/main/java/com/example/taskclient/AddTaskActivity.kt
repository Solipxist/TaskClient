package com.example.taskclient

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var editTextDateTime: EditText
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var addTaskButton: Button
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: TaskApiService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        editTextDateTime = findViewById(R.id.editTextDate)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        addTaskButton = findViewById(R.id.addTaskButton)

        retrofit = RetrofitInstance.retrofit
        apiService = RetrofitInstance.taskApiService

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        editTextDateTime.setOnClickListener {
            showDateTimePickerDialog()
        }

        // Configurar OnClickListener para el botón de agregar tarea
        addTaskButton.setOnClickListener {
            addTask()
        }
    }

    private fun addTask() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val dateTime = editTextDateTime.text.toString().trim()

        if (title.isEmpty()) {
            editTextTitle.error = "Título requerido"
            editTextTitle.requestFocus()
            return
        }

        val userId = sharedPreferences.getInt("userId", -1)

        val dueDate = convertToOffsetDateTime(dateTime)

        val task = Task(
            taskId = 0,
            title = title,
            description = description,
            creationDate = OffsetDateTime.now(ZoneOffset.UTC),
            dueDate = dueDate,
            completed = false,
            userId = userId
        )

        createTask(task)
    }

    private fun createTask(task: Task) {
        val call = apiService.createTask(task)
        call.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    val createdTask = response.body()
                    if (createdTask != null) {
                        Toast.makeText(this@AddTaskActivity, "Tarea creada exitosamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AddTaskActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e(TAG, "Error: Task object is null")
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.e(TAG, "Error creating task: ${t.message}", t)
            }
        })
    }

    private fun convertToOffsetDateTime(dateTime: String): OffsetDateTime {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return OffsetDateTime.parse("$dateTime", formatter)
    }

    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDateTime = OffsetDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0, 0, ZoneOffset.UTC)
                val formatter = DateTimeFormatter.ISO_DATE_TIME

                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedDateTime.plusHours(hourOfDay.toLong()).plusMinutes(minute.toLong())
                        val formattedDateTime = selectedDateTime.format(formatter)

                        // Actualizar el EditText con la fecha y hora seleccionadas
                        editTextDateTime.setText(formattedDateTime)
                        Log.d(TAG, "Selected date and time: $formattedDateTime")
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // Permitir formato de 24 horas
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    companion object {
        private const val TAG = "AddTaskActivity"
    }
}
