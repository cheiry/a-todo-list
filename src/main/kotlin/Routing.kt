package com.cheiry

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is TaskNotFoundException)
                call.respondText(text = "404: ${cause.message}", status = HttpStatusCode.NotFound)
            if (cause is IllegalStateException || cause is TaskAlreadyExistsException)
                call.respondText(text = "403: ${cause.message}", status = HttpStatusCode.BadRequest)
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/tasks") {
            call.respond(TaskRepository.list())
        }
        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toInt() ?:return@get call.respond(HttpStatusCode.NotFound)
            val task = TaskRepository.getById(id)
            call.respond(task)
        }
        post("/tasks") {
            val newTask = call.receive<Task>()
            TaskRepository.add(newTask)
            call.respond(HttpStatusCode.Created)
        }
        delete("/tasks/{id}") {
            val taskId: Int = call.request.pathVariables["id"]?.toInt() ?:return@delete call.respond(HttpStatusCode.NotFound)
            TaskRepository.delete(taskId)
            call.respond(HttpStatusCode.OK)
        }
        put("/tasks/{id}") {
            val taskId: Int = call.request.pathVariables["id"]?.toInt() ?:return@put call.respond(HttpStatusCode.NotFound)
            val status: Status = call.request.queryParameters["status"]?.let { Status.valueOf(it) } ?:return@put call.respond(HttpStatusCode.BadRequest)
            TaskRepository.getById(taskId).status = status
            call.respond(HttpStatusCode.NoContent)
        }
    }
}