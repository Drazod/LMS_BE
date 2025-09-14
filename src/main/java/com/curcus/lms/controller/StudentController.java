package com.curcus.lms.controller;

import com.curcus.lms.model.request.SectionCompleteRequest;
import com.curcus.lms.model.request.StudentRequest;
import com.curcus.lms.model.request.UserAddressRequest;
import com.curcus.lms.model.response.*;
import com.curcus.lms.service.impl.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.CourseSearchResponse;
import com.curcus.lms.model.response.StudentResponse;
import com.curcus.lms.model.response.EnrollmentResponse;
import com.curcus.lms.model.response.MetadataResponse;
import com.curcus.lms.model.response.StudentStatisticResponse;
import com.curcus.lms.exception.DuplicatePhoneNumberException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.service.StudentService;

import jakarta.validation.Valid;
import jakarta.validation.Validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = { "", "/list" })
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentResponse> studentPage = studentService.findAll(pageable);
            if (studentPage.isEmpty()) {
                throw new NotFoundException("Student not found.");
            }

            String baseUrlStr = String.format("/api/students/list?");

            MetadataResponse metadata = new MetadataResponse(
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.getNumber(),
                studentPage.getSize(),
                (studentPage.hasNext() ? baseUrlStr + (studentPage.getNumber() + 1) + "&size=" + size : null),
                (studentPage.hasPrevious() ? baseUrlStr + (studentPage.getNumber() - 1) + "&size=" + size : null),
                baseUrlStr + "page=" + (studentPage.getTotalPages() - 1) + "&size=" + size,
                baseUrlStr + "page=0&size=" + size
            );

            ApiResponse<List<StudentResponse>> apiResponse = new ApiResponse<>();
            Map<String, Object> responseMetadata = new HashMap<>();
            responseMetadata.put("pagination", metadata);
            apiResponse.ok(studentPage.getContent(), responseMetadata);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #id)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        try {
            Optional<StudentResponse> studentResponse = studentService.findById(id);
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            if (studentResponse.isPresent()) {
                apiResponse.ok(studentResponse.get());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Student not found");
                apiResponse.error(error);
                return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@RequestBody @Valid StudentRequest studentRequest,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                throw new Exception("Request is not valid");
            StudentResponse studentResponse = studentService.createStudent(studentRequest);
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(studentResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "An error occurred while creating the student");
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #id)")
    @PostMapping(value = "/{id}/update-address")
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateStudentAddress(@PathVariable Long id,
            @RequestBody @Valid UserAddressRequest studentAddressRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                throw new Exception("Request invalid");
            UserAddressResponse studentResponse = studentService.updateStudentAddress(id, studentAddressRequest);
            ApiResponse<UserAddressResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(studentResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (DuplicatePhoneNumberException ex) {
            throw ex;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<UserAddressResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(@PathVariable Long id,
                                                                      @RequestBody @Valid StudentRequest studentRequest) {
        try {
            StudentResponse studentResponse = studentService.updateStudent(id, studentRequest);
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(studentResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #id)")
    @PutMapping("/{id}/changePassword")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentPassword(@PathVariable Long id,
            @RequestBody StudentRequest studentRequest) {
        try {
            StudentResponse studentResponse = studentService.updateStudentPassword(id, studentRequest);
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(studentResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<StudentResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #id)")
    @GetMapping("/{id}/courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getCoursesByStudentId(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EnrollmentResponse> enrollmentPage = studentService.getCoursesByStudentId(id ,pageable);
            // if (enrollmentPage.isEmpty()) {
            //     throw new NotFoundException("Enrollment not found.");
            // }

            String baseUrlStr = String.format("/api/students/%d/courses?", id);

            MetadataResponse metadata = new MetadataResponse(
                enrollmentPage.getTotalElements(),
                enrollmentPage.getTotalPages(),
                enrollmentPage.getNumber(),
                enrollmentPage.getSize(),
                (enrollmentPage.hasNext() ? baseUrlStr + (enrollmentPage.getNumber() + 1) + "&size=" + size : null),
                (enrollmentPage.hasPrevious() ? baseUrlStr + (enrollmentPage.getNumber() - 1) + "&size=" + size : null),
                baseUrlStr + "page=" + (enrollmentPage.getTotalPages() - 1) + "&size=" + size,
                baseUrlStr + "page=0&size=" + size
            );

            ApiResponse<List<EnrollmentResponse>> apiResponse = new ApiResponse<>();
            Map<String, Object> responseMetadata = new HashMap<>();
            responseMetadata.put("pagination", metadata);
            apiResponse.ok(enrollmentPage.getContent(), responseMetadata);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<List<EnrollmentResponse>> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> addStudentToCourse(@PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            EnrollmentResponse enrollmentResponse = studentService.addStudentToCourse(studentId, courseId);
            ApiResponse<EnrollmentResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(enrollmentResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<EnrollmentResponse> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{studentId}/enrollFromCart")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> addStudentToCoursesFromCart(
            @PathVariable Long studentId) {
        try {
            List<EnrollmentResponse> enrollmentResponses = studentService.addStudentToCoursesFromCart(studentId);
            ApiResponse<List<EnrollmentResponse>> apiResponse = new ApiResponse<>();
            apiResponse.ok(enrollmentResponses);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<List<EnrollmentResponse>> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping("/{studentId}/statistic")
    public ResponseEntity<ApiResponse<StudentStatisticResponse>> studentStatistic(@PathVariable Long studentId) {
        try{
            StudentStatisticResponse temp=studentService.studentStatistic(studentId);
            ApiResponse<StudentStatisticResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(temp);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (NotFoundException ex){
            throw ex;
        }
        catch (Exception ex){
            throw new ApplicationException();
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping("/{studentId}/courses/{courseId}/current-section")
    ResponseEntity<ApiResponse<SectionCompleteResponse>> getCurrentSection(@PathVariable Long studentId,
                                                                           @PathVariable Long courseId) {
        ApiResponse<SectionCompleteResponse> apiResponse = new ApiResponse<>();
        apiResponse.ok(studentService.getCurrentSection(studentId, courseId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #request.studentId)")
    @PutMapping("/complete-section")
    ResponseEntity<ApiResponse<SectionCompleteResponse>> completeCurrentSection(@Valid @RequestBody SectionCompleteRequest request) {
        ApiResponse<SectionCompleteResponse> apiResponse = new ApiResponse<>();
        apiResponse.ok(studentService.completeSection(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
