package com.kotlinspring.controller

import com.kotlinspring.dto.InstructorDto
import com.kotlinspring.service.InstructorService
import com.kotlinspring.util.createInstructorDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [InstructorController::class])
@AutoConfigureWebTestClient
class InstructorControllerUnitTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var instructorService: InstructorService

    @Test
    fun addInstructor() {
        val instructorDto = InstructorDto(null, "Olga")
        every { instructorService.createInstructor(instructorDto) }.returns(createInstructorDto(id = 1))
        val savedInstructorDto =
            webTestClient.post().uri("/v1/instructors").bodyValue(instructorDto).exchange().expectStatus().isCreated.expectBody(
                InstructorDto::class.java
            ).returnResult().responseBody

        Assertions.assertNotNull(savedInstructorDto)
        Assertions.assertNotNull(savedInstructorDto?.id)
    }

    @Test
    fun addInstructorWithValidation() {
        val instructorDto = InstructorDto(null, "")
        val response = webTestClient.post().uri("/v1/instructors").bodyValue(instructorDto).exchange()
            .expectStatus().isBadRequest.expectBody(String::class.java)
            .returnResult().responseBody
        assertEquals("InstructorDto.name must not be blank", response)
    }

    @Test
    fun addInstructorWithRuntimeException() {
        val instructorDto = InstructorDto(null, "Denis")

        val error = "Unexpected error occurred"
        every { instructorService.createInstructor(instructorDto) } throws RuntimeException(error)

        val response = webTestClient.post().uri("/v1/instructors").bodyValue(instructorDto).exchange()
            .expectStatus().is5xxServerError.expectBody(String::class.java)
            .returnResult().responseBody
        assertEquals(error, response)
    }

//    @Test
//    fun retrieveAllInstructors() {
//        every { instructorService.retrieveAllInstructors() }.returnsMany(
//            listOf(
//                createInstructorDto(id = 1),
//                createInstructorDto(
//                    id = 2, name =
//                    "Build RestFul APis using SpringBoot and Kotlin", "Development"
//                )
//            )
//        )
//        val instructorDtos =
//            webTestClient.get().uri("/v1/instructors").exchange().expectStatus().isOk.expectBodyList(InstructorDto::class.java)
//                .returnResult().responseBody
//        assertEquals(2, instructorDtos?.size)
//    }

    @Test
    fun updateInstructor() {
        val instructorDto = InstructorDto(
            null,
            "Ian"
        )
        val updatedInstructorDto = createInstructorDto(id = 100)
//        every { instructorService.updateInstructor(100, instructorDto) }.returns(updatedInstructorDto)

        val updatedInstructor =
            webTestClient.put().uri("/v1/instructors/{instructorId}", 100).bodyValue(instructorDto).exchange()
                .expectStatus().isOk.expectBody(
                    InstructorDto::class.java
                ).returnResult().responseBody
        Assertions.assertEquals(updatedInstructorDto.name, updatedInstructor?.name)
    }

    @Test
    fun deleteInstructor() {
//        every { instructorService.deleteInstructor(1) } just runs
        webTestClient.delete().uri("/v1/instructors/{instructorId}", 1).exchange()
            .expectStatus().isNoContent
    }
}