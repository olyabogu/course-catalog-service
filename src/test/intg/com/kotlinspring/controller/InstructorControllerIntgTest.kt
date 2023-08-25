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
//        instructorRepository.saveAll(instructorEntityList())
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

    @Test
    fun retrieveAllInstructors() {
        val instructorDtos =
            webTestClient.get().uri("/v1/instructors").exchange()
                .expectStatus().isOk.expectBodyList(InstructorDto::class.java)
                .returnResult().responseBody
        assertEquals(3, instructorDtos?.size)
        println(instructorDtos)
    }

    @Test
    fun retrieveAllInstructorsByName() {
        val uri = UriComponentsBuilder.fromUriString("/v1/instructors")
            .queryParam("instructor_name", "SpringBoot").toUriString()
        val instructorDtos =
            webTestClient.get().uri(uri).exchange().expectStatus().isOk.expectBodyList(InstructorDto::class.java)
                .returnResult().responseBody
        assertEquals(2, instructorDtos?.size)
        println(instructorDtos)
    }

    @Test
    fun updateInstructor() {
        val instructor = Instructor(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", courseEntityList()
        )
        instructorRepository.save(instructor)
        val updatedInstructorDto = InstructorDto(
            null,
            "Olga"
        )
        val updatedInstructor =
            webTestClient.put().uri("/v1/instructors/{instructorId}", instructor.id).bodyValue(updatedInstructorDto)
                .exchange()
                .expectStatus().isOk.expectBody(
                    InstructorDto::class.java
                ).returnResult().responseBody
        assertEquals("Build RestFul APis using SpringBoot, Spring WebFlux and Kotlin", updatedInstructor?.name)
    }

    @Test
    fun deleteInstructor() {
        val instructor = Instructor(
            null,
            "Olga"
        )
        instructorRepository.save(instructor)
        webTestClient.delete().uri("/v1/instructors/{instructorId}", instructor.id).exchange()
            .expectStatus().isNoContent
    }

}