package com.kotlinspring.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CourseDto(
    val id: Int?,
    @get:NotBlank(message = "CourseDto.name must not be blank") val name: String,
    @get:NotBlank(message = "CourseDto.category must not be blank") val category: String,
    @get:NotNull(message = "CourseDto.instructorId must not be blank") val instructorId: Int? = null
)