package com.kotlinspring.util

import com.kotlinspring.dto.CourseDto
import com.kotlinspring.dto.InstructorDto
import com.kotlinspring.entity.Course
import com.kotlinspring.entity.Instructor


fun courseEntityList() = listOf(
    Course(
        null,
        "Build RestFul APis using SpringBoot and Kotlin", "Development"
    ),
    Course(
        null,
        "Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development",
    ),
    Course(
        null,
        "Wiremock for Java Developers", "Development",
    )
)

fun instructorEntityList() = listOf(
    Instructor(
        null,
        "Olga", courseEntityList()
    ),
    Instructor(
        null,
        "Denis", courseEntityList()
    ),
    Instructor(
        null,
        "Ian", courseEntityList()
    )
)

fun createCourseDto(
    id: Int? = null,
    name: String = "Build RestFul APis using Spring Boot and Kotlin",
    category: String = "Development",
    instructorId: Int? = 1
) = CourseDto(
    id,
    name,
    category,
    instructorId
)


fun createInstructorDto(id: Int): InstructorDto =
    InstructorDto(id = id, name = "Olga")


fun courseEntityList(instructor: Instructor? = null) = listOf(
    Course(
        id = null,
        name = "Build RestFul APis using SpringBoot and Kotlin", category = "Development",
        instructor = instructor
    ),
    Course(
        id = null,
        name = "Build Reactive Microservices using Spring WebFlux/SpringBoot", category = "Development", instructor
    ),
    Course(
        id = null,
        name = "Wiremock for Java Developers", category = "Development",
        instructor = instructor
    )
)

fun instructorEntity(name: String = "Dilip Sundarraj") = Instructor(null, name)


