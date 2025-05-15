package fr.cheiry

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
class TaskHandler ( val repository : TaskRepository) {

    fun getAllTasks(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().body(repository.list())
    }

    fun createTask(request: ServerRequest): ServerResponse {
        return try {
            val body = request.body<Task>()
            repository.add(body)
            ServerResponse.created(create("/tasks/${body.id}")).build()
        } catch (e: TaskAlreadyExistsException) {
            ServerResponse.badRequest().build()
        }
    }

    fun getATask(request: ServerRequest): ServerResponse {
        return try {
            val id = request.pathVariable("id").toInt()
            ServerResponse.ok().body(repository.getById(id))
        } catch (e: TaskNotFoundException) {
            ServerResponse.notFound().build()
        } catch (e: NumberFormatException) {
            ServerResponse.badRequest().build()
        }
    }

    fun deleteTask(request: ServerRequest): ServerResponse {
        return try {
            val id = request.pathVariable("id").toInt()
            repository.delete(id)
            ServerResponse.noContent().build()
        } catch (e: TaskNotFoundException) {
            ServerResponse.notFound().build()
        } catch (e: NumberFormatException) {
            ServerResponse.badRequest().build()
        }
    }

    fun changeStatus(request: ServerRequest): ServerResponse {
        return try {
            val id = request.pathVariable("id").toInt()
            val status: Status = request.param("status").map { Status.valueOf(it.uppercase()) }
                .orElseThrow { BadRequestException("Wrong status") }
            repository.getById(id).status = status
            ServerResponse.noContent().build()
        } catch (e: TaskNotFoundException) {
            ServerResponse.notFound().build()
        } catch (e : IllegalArgumentException ) {
            ServerResponse.badRequest().build()
        }
    }
}