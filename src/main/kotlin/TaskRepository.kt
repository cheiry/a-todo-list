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
        dontExists(newTask.id) { "A task with given id already exists" }
        tasks.add(newTask)
    }
    fun getById(taskId: Int): Task {
        exists(taskId) { "A task with given id does not exist" }
        return tasks.filter { it.id == taskId }.first()
    }
    fun delete(id: Int) {
        exists(id) { "A task with given id does not exist" }
        tasks.removeIf { it.id == id }
    }

    fun exists(value: Int, lazyMessage: () -> Any) {
        if (!tasks.map { it.id }.contains(value)) {
            val message = lazyMessage()
            throw TaskNotFoundException(message.toString())
        }
    }

    fun dontExists(value: Int, lazyMessage: () -> Any) {
        if (tasks.map { it.id }.contains(value)) {
            val message = lazyMessage()
            throw TaskAlreadyExistsException(message.toString())
        }
    }
}

@Serializable
class Task (val id: Int, val name: String, var status: Status? = Status.NEW)

enum class Status {
    NEW, TODO, DOING, BLOCKED, DONE
}

class TaskNotFoundException(message: String) : Exception(message)
class TaskAlreadyExistsException(message: String) : Exception(message)