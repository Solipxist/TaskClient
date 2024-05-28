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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

class EditTaskActivity : AppCompatActivity() {

    private lateinit var editTextDateTime: EditText
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var addTaskButton: Button
    private lateinit var retrofit: Retrofit
    private lateinit var apiService: TaskApiService
    private lateinit var sharedPreferences: SharedPreferences
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        val taskId = intent.getIntExtra("TASK_ID", -1)

        editTextDateTime = findViewById(R.id.eEditTextDate)
        editTextTitle = findViewById(R.id.eEditTextTitle)
        editTextDescription = findViewById(R.id.eEditTextDescription)
        addTaskButton = findViewById(R.id.editAddTaskButton)

        retrofit = RetrofitInstance.retrofit
        apiService = RetrofitInstance.taskApiService

        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)

        getTask(taskId)

        editTextDateTime.setOnClickListener {
            showDateTimePickerDialog()
        }

        addTaskButton.setOnClickListener {
            editTask()
        }
    }

    private fun editTask() {
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val dateTime = editTextDateTime.text.toString().trim()

        if (title.isEmpty()) {
            editTextTitle.error = "Título requerido"
            editTextTitle.requestFocus()
            return
        }

        if (task != null) {
            task!!.title = title
            task!!.description = description
            task!!.dueDate = convertToOffsetDateTime(dateTime)

            updateTask()
        } else {
            Log.e(TAG, "Task object is null")
        }
    }

    private fun getTask(id: Int) {
        val call = apiService.getTask(id)
        call.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    task = response.body()
                    task?.let {
                        editTextTitle.setText(it.title)
                        editTextDescription.setText(it.description)
                        editTextDateTime.setText(it.dueDate.toString())
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.e(TAG, "Error fetching task: ${t.message}", t)
            }
        })
    }

    private fun updateTask() {
        task?.let {
            val call = apiService.updateTask(taskId = it.taskId, task = it)
            call.enqueue(object : Callback<Task> {
                override fun onResponse(call: Call<Task>, response: Response<Task>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditTaskActivity, "Tarea actualizada exitosamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@EditTaskActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e(TAG, "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Task>, t: Throwable) {
                    Log.e(TAG, "Error updating task: ${t.message}", t)
                }
            })
        }
    }

    private fun convertToOffsetDateTime(dateTime: String): OffsetDateTime {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return OffsetDateTime.parse(dateTime, formatter)
    }

    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = OffsetDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute, 0, 0, ZoneOffset.UTC)
                        val formattedDateTime = selectedDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
                        editTextDateTime.setText(formattedDateTime)
                        Log.d(TAG, "Selected date and time: $formattedDateTime")
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // 24-hour format
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
        private const val TAG = "EDIT"
    }
}
