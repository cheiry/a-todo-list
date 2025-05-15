package fr.cheiry

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTest {

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
}