package com.route.todoappc40gsunwed.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.route.todoappc40gsunwed.database.model.Task
import java.util.Date

// Data Access Object
@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM Task")
    fun getAllTasks(): List<Task>

    //                          if
    @Query("SELECT * FROM Task WHERE date = :date")
    fun getTasksByDate(date: Date): List<Task>

    @Query("SELECT * FROM Task WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?
}