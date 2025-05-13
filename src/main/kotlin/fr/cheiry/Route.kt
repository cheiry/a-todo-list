package com.cheiry.fr.cheiry

import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.net.URI.create

@Component
class Route ( val handler: TaskHandler ) {
    val taskRouter = router {
        "/tasks".nest {
            GET("/{id}", handler::getATask)
            DELETE("/{id}",handler::deleteTask)
            PUT("/{id}", handler::changeStatus)
            GET(handler::getAllTasks)
            POST(handler::createTask)
        }
    }
}

@Component
class TaskHandler ( val repository : TaskRepository ) {

    fun getAllTasks(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().body(repository.list())
    }

    fun createTask(request: ServerRequest): ServerResponse {
        val body = request.body<Task>()
        repository.add(body)
        return ServerResponse.created(create("/tasks/${body.id}")).build()
    }

    fun getATask(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        return ServerResponse.ok().body(repository.getById(id))
    }

    fun deleteTask(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        repository.delete(id)
        return ServerResponse.noContent().build()
    }

    fun changeStatus(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toInt()
        request.param("status")
            .ifPresentOrElse( { Status.valueOf(it.uppercase()).let { status -> repository.getById(id).status = status } }
                , { throw BadRequestException("Wrong status") } )
        return ServerResponse.noContent().build()
    }
}