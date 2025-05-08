package com.cheiry

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `get tasks returns ok`() = testApplication {
        val client = setup()
        val response = client.get("/tasks")
        response.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        val tasks: Array<Task> = response.body()
        assertEquals("Get a life", tasks[0].name)
        assertEquals(Status.NEW, tasks[0].status)
    }

    @Test
    fun `post new task return created`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(4, "Test"))
        }.apply { assertEquals(HttpStatusCode.Created, status) }
        val tasks: Array<Task> = client.get("/tasks").body()
        assertEquals("Test", tasks[3].name)
    }

    @Test
    fun `post existing task return error`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(1, "Test"))
        } .apply { assertEquals(HttpStatusCode.BadRequest, status) }
    }

    @Test
    fun `post new task with status`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(5, "Test", Status.TODO))
        } .apply { assertEquals(HttpStatusCode.Created, status) }
        val tasks: Array<Task> = client.get("/tasks").body()
        assertEquals(Status.TODO, tasks.last().status)
    }

    @Test
    fun `put update task with status`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(6, "Test"))
        }
        client.put("/tasks/6?status=TODO") {
            contentType(ContentType.Application.Json)
        } .apply { assertEquals(HttpStatusCode.NoContent, status) } // TODO : maybe change the return code ?
        val tasks: Array<Task> = client.get("/tasks").body()
        assertEquals(Status.TODO, tasks.last().status)
    }

    @Test
    fun `delete a task with id`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(10, "Test", Status.TODO))
        }
        client.delete("/tasks/10") {
            contentType(ContentType.Application.Json)
        }.apply { assertEquals(HttpStatusCode.OK, status) }
    }

    private fun ApplicationTestBuilder.setup(): HttpClient {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        return client
    }
}


