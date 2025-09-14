package com.curcus.lms.service;

import com.curcus.lms.model.request.SectionCompleteRequest;
import com.curcus.lms.model.request.StudentRequest;
import com.curcus.lms.model.response.*;
import com.curcus.lms.model.request.UserAddressRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Page<StudentResponse> findAll(Pageable pageable);

    Optional<StudentResponse> findById(Long studentId);

    StudentResponse createStudent(StudentRequest student);

    StudentResponse updateStudent(Long studentId, StudentRequest student);

    StudentResponse updateStudentPassword(Long studentId, StudentRequest student);

    void deleteStudent(Long studentId);

    Page<EnrollmentResponse> getCoursesByStudentId(Long studentId, Pageable pageable);

    List<CourseResponse> getListCourseFromCart(Long studentId);

    Page<CourseResponseForCart> getListCourseFromCart(Long studentId, Pageable pageable);

    EnrollmentResponse addStudentToCourse(Long studentId, Long courseId);

    List<EnrollmentResponse> addStudentToCoursesFromCart(Long studentId);

    HashMap<String, Integer> getCoursesPurchasedLastFiveYears(Long studentId);

    Integer getTotalPurchaseCourse(Long studentId);

    Integer totalFinishCourse(Long studentId);

    HashMap<String, Integer> finishCourseFiveYears(Long studentId);

    StudentStatisticResponse studentStatistic(Long studentId);

    UserAddressResponse updateStudentAddress(Long userId, UserAddressRequest addressRequest);

    SectionCompleteResponse completeSection(SectionCompleteRequest request);

    SectionCompleteResponse getCurrentSection(Long studentId, Long courseId);
}
