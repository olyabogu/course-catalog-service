package com.kotlinspring.service

import com.kotlinspring.dto.InstructorDto
import com.kotlinspring.entity.Instructor
import com.kotlinspring.repository.InstructorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService(val instructorRepository: InstructorRepository) {
    fun createInstructor(instructorDto: InstructorDto): InstructorDto {
        val instructorEntity = instructorDto.let { Instructor(it.id, it.name) }
        instructorRepository.save(instructorEntity)
        return instructorEntity.let { InstructorDto(it.id, it.name) }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> {
        return instructorRepository.findById(instructorId)
    }
}