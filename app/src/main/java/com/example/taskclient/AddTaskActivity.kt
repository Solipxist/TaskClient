package com.example.taskclient
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.time.LocalDateTime
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

        Log.d(TAG, "DateTime: $dateTime")

        if (title.isEmpty()) {
            editTextTitle.error = "Título requerido"
            editTextTitle.requestFocus()
            return
        }

        if (dateTime.isEmpty()) {
            editTextDateTime.error = "Fecha y hora requeridas"
            editTextDateTime.requestFocus()
            return
        }

        val dueDate = try {
            convertToOffsetDateTime(dateTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date and time: ${e.message}")
            editTextDateTime.error = "Formato de fecha y hora inválido"
            editTextDateTime.requestFocus()
            return
        }

        Log.d(TAG, "DueDate: $dueDate")

        val userId = sharedPreferences.getInt("userId", -1)

        val task = Task(
            taskId = 0,
            title = title,
            description = description,
            creationDate = OffsetDateTime.now(),
            dueDate = dueDate,
            completed = false,
            userId = userId
        )

        Log.d(TAG, "Task: $task")
        createTask(task)
    }

    private fun createTask(task: Task) {
        val call = apiService.createTask(task)
        call.enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    val createdTask = response.body()
                    if (createdTask != null) {
                        Log.d(TAG, "Task created successfully: $createdTask")
                        // Aquí puedes agregar cualquier lógica adicional después de crear la tarea
                    } else {
                        Log.e(TAG, "Error: Task object is null")
                    }
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                    // Aquí puedes manejar los diferentes códigos de error, si es necesario
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                Log.e(TAG, "Error creating task: ${t.message}", t)
                // Aquí puedes manejar los errores de conexión, etc.
            }
        })
    }


    private fun convertToOffsetDateTime(dateTime: String): OffsetDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val localDateTime = LocalDateTime.parse(dateTime, formatter)
        return localDateTime.atOffset(ZoneOffset.UTC)
    }

    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                Log.d(TAG, "Selected date: $selectedDate")

                // Ahora mostrar el TimePickerDialog
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        val selectedDateTime = "$selectedDate $selectedTime"

                        // Actualizar el EditText con la fecha y hora seleccionadas
                        editTextDateTime.setText(selectedDateTime)
                        Log.d(TAG, "Selected date and time: $selectedDateTime")
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