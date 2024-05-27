package com.example.taskclient

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class TaskAdapter(private var tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val creationDate: TextView = itemView.findViewById(R.id.taskCreationDate)
        val dueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val completedCheckbox: CheckBox = itemView.findViewById(R.id.taskCompletedCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = "Title: ${task.title}"
        holder.description.text = "Description: ${task.description}"
        holder.creationDate.text = "Creation Date: ${task.creationDate}"
        holder.dueDate.text = getDueDateText(task.dueDate)
        holder.completedCheckbox.isChecked = task.completed
        holder.completedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            task.completed = isChecked
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditTaskActivity::class.java).apply {
                putExtra("TASK_ID", task.taskId)
            }
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    private fun getDueDateText(dueDate: OffsetDateTime?): String {
        dueDate?.let {
            val currentDate = LocalDateTime.now()
            val difference = ChronoUnit.MINUTES.between(currentDate, it)
            if (difference > 0) {
                val days = difference / (60 * 24)
                val hours = (difference % (60 * 24)) / 60
                val minutes = difference % 60
                return "Due Date: $days days, $hours hours, $minutes minutes"
            } else {
                return "Overdue"
            }
        } ?: return "No Due Date"
    }
}
