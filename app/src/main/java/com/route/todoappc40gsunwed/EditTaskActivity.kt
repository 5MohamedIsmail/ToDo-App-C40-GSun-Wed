package com.route.todoappc40gsunwed

    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import com.route.todoappc40gsunwed.adapter.TaskAdapter
    import com.route.todoappc40gsunwed.database.TaskDatabase
    import com.route.todoappc40gsunwed.database.model.Task
    import com.route.todoappc40gsunwed.databinding.EditTaskActivityBinding
    import kotlinx.coroutines.launch
    import java.text.SimpleDateFormat
    import java.util.Locale

    class EditTaskActivity : AppCompatActivity() {

        private lateinit var binding: EditTaskActivityBinding
        lateinit var adapter: TaskAdapter

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = EditTaskActivityBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val taskId = intent.getIntExtra("TASK_ID", -1)

            if (taskId != -1) {
                lifecycleScope.launch {
                    val task = TaskDatabase.getInstance(this@EditTaskActivity).getTaskDao().getTaskById(taskId)
                    task?.let {
                        binding.taskTitleInput.setText(it.title)
                        binding.taskDescriptionInput.setText(it.description)
                        // Format the date and time as needed and set it to dateSelector
                        val formattedDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(it.date)
                        binding.dateSelector.text = formattedDateTime
                    }
                }
            }

            binding.saveTaskButton.setOnClickListener {
                val updatedTitle = binding.taskTitleInput.text.toString()
                val updatedDescription = binding.taskDescriptionInput.text.toString()
                val updatedDateTimeString = binding.dateSelector.text.toString()

                // Validation
                if (updatedTitle.isBlank() || updatedDescription.isBlank()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val updatedDate = try {
                    dateFormat.parse(updatedDateTimeString)
                } catch (e: Exception) {
                    null
                }

                if (taskId != -1) {
                    lifecycleScope.launch {
                        val updatedTask = Task(taskId, updatedTitle, updatedDescription, updatedDate, updatedDateTimeString)
                        TaskDatabase.getInstance(this@EditTaskActivity).getTaskDao().updateTask(updatedTask)
                        adapter.notifyDataSetChanged()
                        finish()
                    }
                }
            }
        }
    }
