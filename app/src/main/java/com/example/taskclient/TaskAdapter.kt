package com.example.taskclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private var tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val creationDate: TextView = itemView.findViewById(R.id.taskCreationDate)
        val dueDate: TextView = itemView.findViewById(R.id.taskDueDate)
        val completed: TextView = itemView.findViewById(R.id.taskCompleted)
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
        holder.dueDate.text = "Due Date: ${task.dueDate}"
        holder.completed.text = "Completed: ${task.completed}"
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}

