package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDto
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
class CourseControllerIntgTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()
        val instructor = instructorEntity()
        instructorRepository.save(instructor)
        courseRepository.saveAll(courseEntityList(instructor))
    }

    @Test
    fun addCourse() {
        val instructor = instructorRepository.findAll().first()
        val courseDto = CourseDto(null, "build restful api using springboot and kotlin", "Olga", instructor.id)
        val savedCourseDto =
            webTestClient.post().uri("/v1/courses").bodyValue(courseDto).exchange().expectStatus().isCreated.expectBody(
                CourseDto::class.java
            ).returnResult().responseBody

        Assertions.assertNotNull(savedCourseDto)
        Assertions.assertNotNull(savedCourseDto?.id)
    }

    @Test
    @Disabled
    fun retrieveAllCourses() {
        val courseDtos =
            webTestClient.get().uri("/v1/courses").exchange().expectStatus().isOk.expectBodyList(CourseDto::class.java)
                .returnResult().responseBody
        assertEquals(3, courseDtos?.size)
        println(courseDtos)
    }

    @Test
    fun retrieveAllCoursesByName() {
        val uri = UriComponentsBuilder.fromUriString("/v1/courses")
            .queryParam("course_name", "SpringBoot").toUriString()
        val courseDtos =
            webTestClient.get().uri(uri).exchange().expectStatus().isOk.expectBodyList(CourseDto::class.java)
                .returnResult().responseBody
        assertEquals(2, courseDtos?.size)
        println(courseDtos)
    }

    @Test
    fun updateCourse() {
        val instructor = instructorRepository.findAll().first()

        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development", instructor
        )
        courseRepository.save(course)
        val updatedCourseDto = CourseDto(
            null,
            "Build RestFul APis using SpringBoot, Spring WebFlux and Kotlin", "Development",
            course.instructor?.id
        )
        val updatedCourse =
            webTestClient.put().uri("/v1/courses/{courseId}", course.id).bodyValue(updatedCourseDto).exchange()
                .expectStatus().isOk.expectBody(
                    CourseDto::class.java
                ).returnResult().responseBody
        assertEquals("Build RestFul APis using SpringBoot, Spring WebFlux and Kotlin", updatedCourse?.name)
    }

    @Test
    fun deleteCourse() {
        val instructor = instructorRepository.findAll().first()

        val course = Course(
            null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development",instructor
        )
        courseRepository.save(course)
        webTestClient.delete().uri("/v1/courses/{courseId}", course.id).exchange()
            .expectStatus().isNoContent
    }

}