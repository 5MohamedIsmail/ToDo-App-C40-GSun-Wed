package com.route.todoappc40gsunwed.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.route.todoappc40gsunwed.EditTaskActivity
import com.route.todoappc40gsunwed.database.TaskDatabase
import com.route.todoappc40gsunwed.database.model.Task
import com.route.todoappc40gsunwed.databinding.ItemTaskBinding

        class TaskAdapter(var tasksList: MutableList<Task>) : Adapter<TaskAdapter.TaskViewHolder>() {

            class TaskViewHolder(val binding: ItemTaskBinding) : ViewHolder(binding.root) {
                fun bind(task: Task) {
                    binding.taskTime.text = task.time
                    binding.taskTitle.text = task.title
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = tasksList.get(position)
        holder.bind(item)
        holder.binding.deleteView.setOnClickListener {
            TaskDatabase.getInstance(holder.binding.root.context).getTaskDao().deleteTask(item)
            tasksList!!.remove(item)
            notifyItemRemoved(position)
        }
        holder.binding.taskCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditTaskActivity::class.java)
            intent.putExtra("TASK_ID", item.id) // Pass the task ID
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateList(tasks: List<Task>) {
        tasksList = tasks.toMutableList()
        notifyDataSetChanged()
    }

}