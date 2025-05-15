package fr.cheiry

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun contextLoads() {}

    @Test
    fun `get all tasks is ok`(@Autowired mvc: MockMvc) {
        mvc.get("/tasks").andExpect { status { isOk() } }
    }

    @Test
    fun `get a task returns ok`(@Autowired mvc: MockMvc) {
        mvc.get("/tasks/1").andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { jsonPath("$.id") { value("1")} }
            content { jsonPath("$.name") { value("Get a life")} }
            content { jsonPath("$.status") { value("NEW")} }
        }
    }

    @Test
    fun `get non existing task return error`(@Autowired mvc: MockMvc) {
        mvc.get("/tasks/45").andExpect { status { isNotFound() } }
    }

    @Test
    fun `get task with bad argument return error`(@Autowired mvc: MockMvc) {
        mvc.get("/tasks/abc").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `post creates a new task`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(Task(4, "Test"))
        }.andExpect {
            status { isCreated() }
            header { exists("Location") }
        }
    }

    @Test
    fun `post with existing task return error`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(Task(1, "Test"))
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `post with bad object return error`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString("""{"id"="48", "potato"="carrot"}""")
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `delete existing task deletes`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(Task(10, "Test"))
        }.andExpect { status { isCreated() } }
        mvc.get("/tasks/10").andExpect { status { isOk() } }
        mvc.delete("/tasks/10").andExpect { status { isNoContent() } }
        mvc.get("/tasks/10").andExpect { status { isNotFound() } }
    }

    @Test
    fun `delete non existing task returns error`(@Autowired mvc: MockMvc) {
        mvc.delete("/tasks/45").andExpect { status { isNotFound() } }
    }

    @Test
    fun `delete bad task returns error`(@Autowired mvc: MockMvc) {
        mvc.delete("/tasks/abc").andExpect { status { isBadRequest() } }
    }

    @Test
    fun `change status`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(Task(11, "Test"))
        }
        mvc.put("/tasks/11") { param("status", "TODO") }
            .andExpect { status { isNoContent() } }
        mvc.get("/tasks/11").andExpect {
            content { jsonPath("$.status") { value("TODO") }
            }
        }
        mvc.delete("/tasks/11")
    }

    @Test
    fun `change bad status returns error`(@Autowired mvc: MockMvc) {
        mvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(Task(11, "Test"))
        }
        mvc.put("/tasks/11") { param("status", "ND") }
            .andExpect { status { isBadRequest() } }
        mvc.delete("/tasks/11")
    }

    @Test
    fun `change non existing task returns error`(@Autowired mvc: MockMvc) {
        mvc.put("/tasks/11") { param("status", "TODO") }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `change bad task returns error`(@Autowired mvc: MockMvc) {
        mvc.put("/tasks/abc") { param("status", "TODO") }
            .andExpect { status { isBadRequest() } }
    }
}