package com.cheiry

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun `get tasks returns ok`() = testApplication {
        application {
            module()
        }
        client.get("/tasks").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun `post new task return created`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(4, "Test"))
        }.apply { assertEquals(HttpStatusCode.Created, status) }
    }

    @Test
    fun `post existing task return error`() = testApplication {
        val client = setup()
        client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task(1, "Test"))
        } .apply { assertEquals(HttpStatusCode.BadRequest, status) }
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


