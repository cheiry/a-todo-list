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
        assert(! tasks.map { it.id }.contains(newTask.id)) { "A task with given id already exists" }
        tasks.add(newTask)
    }
    fun getById(taskId: Int): Task {
        assert(tasks.map { it.id }.contains(taskId)) { "A task with given id does not exist" }
        return tasks.filter { it.id == taskId }.first()
    }
}

@Serializable
class Task (val id: Int, val name: String, var status: Status? = Status.NEW)

enum class Status {
    NEW, TODO, DOING, BLOCKED, DONE
}