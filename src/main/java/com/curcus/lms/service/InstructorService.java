package com.curcus.lms.service;

import java.util.List;
import java.util.Optional;

import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.request.InstructorRequest;
import com.curcus.lms.model.request.InstructorUpdateRequest;
import com.curcus.lms.model.response.InstructorGetCourseResponse;
import com.curcus.lms.model.request.UserAddressRequest;
import com.curcus.lms.model.response.InstructorResponse;
import com.curcus.lms.model.response.UserAddressResponse;

public interface InstructorService {
    List<InstructorResponse> findAll();

    List<InstructorResponse> findByName(String name);

    Optional<InstructorResponse> findById(Long instructorId);

    InstructorResponse createInstructor(InstructorRequest instructorRequest);

    InstructorResponse updateInstructor(InstructorUpdateRequest instructorUpdateRequest, Long id);

    InstructorResponse updateInstructorPassword(Long id, String password);

    InstructorResponse recoverInstructorPassword(Long id, String password);

    void deleteInstructor(Long instructorId);

    // List<InstructorGetCourseResponse> getCoursesByInstructor(Long instructorId);

    UserAddressResponse updateInstructorAddress(Long userId, UserAddressRequest addressRequest);
}
