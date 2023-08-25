package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDto
import com.kotlinspring.service.CourseService
import com.kotlinspring.util.createCourseDto
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

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseService: CourseService

    @Test
    fun addCourse() {
        val courseDto = CourseDto(null, "build restful api using springboot and kotlin", "kotlin", 1)
        every { courseService.addCourse(courseDto) }.returns(createCourseDto(id = 1))
        val savedCourseDto =
            webTestClient.post().uri("/v1/courses").bodyValue(courseDto).exchange().expectStatus().isCreated.expectBody(
                CourseDto::class.java
            ).returnResult().responseBody

        Assertions.assertNotNull(savedCourseDto)
        Assertions.assertNotNull(savedCourseDto?.id)
    }

    @Test
    fun addCourseWithValidation() {
        val courseDto = CourseDto(null, "", "", 1)
        val response = webTestClient.post().uri("/v1/courses").bodyValue(courseDto).exchange()
            .expectStatus().isBadRequest.expectBody(String::class.java)
            .returnResult().responseBody
        assertEquals("CourseDto.category must not be blank,CourseDto.name must not be blank", response)
    }

    @Test
    fun addCourseWithRuntimeException() {
        val courseDto = CourseDto(null, "build restful api using springboot and kotlin", "kotlin", 1)

        val error = "Unexpected error occurred"
        every { courseService.addCourse(courseDto) } throws RuntimeException(error)

        val response = webTestClient.post().uri("/v1/courses").bodyValue(courseDto).exchange()
            .expectStatus().is5xxServerError.expectBody(String::class.java)
            .returnResult().responseBody
        assertEquals(error, response)
    }

//    @Test
//    fun retrieveAllCourses() {
//        every { courseService.retrieveAllCourses() }.returnsMany(
//            listOf(
//                createCourseDto(id = 1),
//                createCourseDto(
//                    id = 2, name =
//                    "Build RestFul APis using SpringBoot and Kotlin", "Development"
//                )
//            )
//        )
//        val courseDtos =
//            webTestClient.get().uri("/v1/courses").exchange().expectStatus().isOk.expectBodyList(CourseDto::class.java)
//                .returnResult().responseBody
//        assertEquals(2, courseDtos?.size)
//    }

    @Test
    fun updateCourse() {
        val courseDto = CourseDto(
            null,
            "Build RestFul APis using SpringBoot, Spring WebFlux and Kotlin", "Development", 1
        )
        val updatedCourseDto = createCourseDto(id = 100)
        every { courseService.updateCourse(100, courseDto) }.returns(updatedCourseDto)

        val updatedCourse =
            webTestClient.put().uri("/v1/courses/{courseId}", 100).bodyValue(courseDto).exchange()
                .expectStatus().isOk.expectBody(
                    CourseDto::class.java
                ).returnResult().responseBody
        Assertions.assertEquals(updatedCourseDto.name, updatedCourse?.name)
    }

    @Test
    fun deleteCourse() {
        every { courseService.deleteCourse(1) } just runs
        webTestClient.delete().uri("/v1/courses/{courseId}", 1).exchange()
            .expectStatus().isNoContent
    }
}