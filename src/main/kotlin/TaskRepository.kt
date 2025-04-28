package com.cheiry

import kotlinx.serialization.Serializable

object TaskRepository {
    private val tasks: MutableList<Task> = mutableListOf(
        Task(1, "Get a life"),
        Task(2, "Add a post"),
        Task(3, "Control you ids")
    )
    fun list(): List<Task> {
        return tasks
    }
    fun add(newTask: Task) {
        tasks.add(newTask)
    }
}

@Serializable
class Task (val id: Int, val name: String)
