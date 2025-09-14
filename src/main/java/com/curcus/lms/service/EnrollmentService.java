package com.curcus.lms.service;

import com.curcus.lms.model.entity.Enrollment;

public interface EnrollmentService {
    Enrollment findById(Long studentId, Long courseId);
}
