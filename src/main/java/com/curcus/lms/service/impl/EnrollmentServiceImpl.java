package com.curcus.lms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.repository.EnrollmentRepository;
import com.curcus.lms.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public Enrollment findById(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(studentId, courseId);
        if (enrollment == null) {
            throw new NotFoundException(
                    "Enrollment has not existed with studentId " + studentId + "and courseId " + courseId);
        }
        return enrollment;
    }
}
