package com.kotlinspring.service

import com.kotlinspring.dto.CourseDto
import com.kotlinspring.entity.Course
import com.kotlinspring.exception.CourseNotFoundExcpetion
import com.kotlinspring.exception.InstructorNotValidException
import com.kotlinspring.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepository: CourseRepository, val instructorService: InstructorService) {
    companion object : KLogging()

    fun addCourse(courseDto: CourseDto): CourseDto {
        val instructorOptional = instructorService.findByInstructorId(courseDto.instructorId!!)
        if (!instructorOptional.isPresent) {
            throw InstructorNotValidException("instructor not valid id=${courseDto.instructorId}")
        }
        val courseEntity = courseDto.let { Course(null, it.name, it.category, instructorOptional.get()) }
        courseRepository.save(courseEntity)
        logger.info { "saved course is: $courseEntity " }
        return courseEntity.let { CourseDto(it.id, it.name, it.category, it.instructor?.id) }
    }

    fun retrieveAllCourses(): List<CourseDto> {
        return courseRepository.findAll().map { CourseDto(it.id, it.name, it.category) }
    }

    fun retrieveAllCoursesByName(courseName: String?): List<CourseDto> {
        val courses = courseName?.let { courseRepository.findCoursesByName(courseName) } ?: courseRepository.findAll()
        return courses.map { CourseDto(it.id, it.name, it.category) }
    }

    fun updateCourse(courseId: Int, courseDto: CourseDto): CourseDto {
        val existingCourse = courseRepository.findById(courseId)
        return if (existingCourse.isPresent) {
            existingCourse.get().let {
                it.name = courseDto.name
                it.category = courseDto.category
                courseRepository.save(it)
                CourseDto(it.id, it.name, it.category)
            }
        } else {
            throw CourseNotFoundExcpetion("no course found with id:$courseId")
        }
    }

    fun deleteCourse(courseId: Int) {
        val existingCourse = courseRepository.findById(courseId)
        return if (existingCourse.isPresent) {
            existingCourse.get().let {
                courseRepository.deleteById(courseId)
            }
        } else {
            throw CourseNotFoundExcpetion("no course found with id:$courseId")
        }
    }
}
