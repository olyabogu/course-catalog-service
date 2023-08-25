package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDto
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import com.kotlinspring.util.courseEntityList
import com.kotlinspring.util.instructorEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Testcontainers
class CourseControllerIntgTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    companion object {

        @Container
        val postgresDB = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:latest")).apply {
            withDatabaseName("testdb")
            withUsername("postgres")
            withPassword("secret")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresDB::getJdbcUrl)
            registry.add("spring.datasource.username", postgresDB::getUsername)
            registry.add("spring.datasource.password", postgresDB::getPassword)
        }
    }


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

        assertNotNull(savedCourseDto)
        assertNotNull(savedCourseDto?.id)
    }

    @Test
    fun retrieveAllCourses() {
        val courseDtos =
            webTestClient.get().uri("/v1/courses").exchange().expectStatus().isOk.expectBodyList(CourseDto::class.java)
                .returnResult().responseBody
        assertEquals(3, courseDtos?.size)
        println(courseDtos)
    }

    @Test
    @Disabled
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
            "Build RestFul APis using SpringBoot and Kotlin", "Development", instructor
        )
        courseRepository.save(course)
        webTestClient.delete().uri("/v1/courses/{courseId}", course.id).exchange()
            .expectStatus().isNoContent
    }

}