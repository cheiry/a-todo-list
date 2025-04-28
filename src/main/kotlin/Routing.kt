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
            if (cause is AssertionError)
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
        post("/tasks") {
            val newTask = call.receive<Task>()
            TaskRepository.add(newTask)
            call.respond(HttpStatusCode.Created)
        }
    }
}