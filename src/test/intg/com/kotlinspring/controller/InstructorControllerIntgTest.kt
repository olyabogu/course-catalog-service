package com.kotlinspring.controller

import com.kotlinspring.dto.InstructorDto
import com.kotlinspring.entity.Instructor
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntityList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class InstructorControllerIntgTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    @BeforeEach
    fun setUp() {
        instructorRepository.deleteAll()
    }

    @Test
    fun addInstructor() {
        val instructorDto = InstructorDto(null, "Olga")
        val savedInstructorDto =
            webTestClient.post().uri("/v1/instructors").bodyValue(instructorDto).exchange()
                .expectStatus().isCreated.expectBody(
                    InstructorDto::class.java
                ).returnResult().responseBody

        Assertions.assertNotNull(savedInstructorDto)
        Assertions.assertNotNull(savedInstructorDto?.id)
    }
}