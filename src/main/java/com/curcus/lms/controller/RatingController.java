package com.curcus.lms.controller;

import com.curcus.lms.exception.*;
import com.curcus.lms.model.request.RatingRequest;
import com.curcus.lms.model.response.CourseRatingResponse;
import com.curcus.lms.repository.EnrollmentRepository;
import com.curcus.lms.service.RatingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;

import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.RatingResponse;
import com.curcus.lms.model.response.ResponseCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class RatingController {
    @Autowired
    private RatingService ratingService;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') " +
            "and authentication.principal.getId() == #ratingRequest.studentId)")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(
            @Valid @RequestBody RatingRequest ratingRequest,
            BindingResult bindingResult) {

        ApiResponse<RatingResponse> apiResponse = new ApiResponse<>();

        try {
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors().stream()
                        .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                System.out.println(errorMessage);
                apiResponse.error(ResponseCode.getError(1));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }

            RatingResponse ratingResponse = ratingService.updateRating(ratingRequest);
            apiResponse.ok(ratingResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            apiResponse.error(ResponseCode.getError(8));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (CourseException c) {
            apiResponse.error(ResponseCode.getError(10));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (ApplicationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student has not been registered yet");
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') " +
            "and authentication.principal.getId() == #studentId)")
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<ApiResponse<RatingResponse>> getRatingByStudentIdAndCourseId(@PathVariable Long studentId, @PathVariable Long courseId) {
        ApiResponse<RatingResponse> apiResponse = new ApiResponse<>();
        try {
            RatingResponse ratingResponse = ratingService.getRatingByStudentIdAndCourseId(studentId, courseId);
            if (ratingResponse != null) {
                apiResponse.ok(ratingResponse);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else
                return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException e) {
            apiResponse.error(ResponseCode.getError(8));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (CourseException c) {
            apiResponse.error(ResponseCode.getError(10));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (EnrollmentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student has not been registered yet");
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            throw e;
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<RatingResponse>>> getRatingByCourseId(@PathVariable Long courseId) {
        ApiResponse<List<RatingResponse>> apiResponse = new ApiResponse<>();
        try {
            List<RatingResponse> ratingResponses = ratingService.getRatingByCourseId(courseId);
            apiResponse.ok(ratingResponses);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CourseException c) {
            apiResponse.error(ResponseCode.getError(10));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (ApplicationException e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') " +
            "and authentication.principal.getId() == #ratingRequest.studentId)")
    @PostMapping
    public ResponseEntity<ApiResponse<RatingResponse>> createRating(
            @Valid @RequestBody RatingRequest ratingRequest,
            BindingResult bindingResult
    ) {
        ApiResponse<RatingResponse> apiResponse = new ApiResponse();
        try {
            if (bindingResult.hasErrors()) {
                apiResponse.error(ResponseCode.getError(1));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }

            // check if user has enrolled the course
            if (!enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(ratingRequest.getStudentId(), ratingRequest.getCourseId())) {
                apiResponse.error(ResponseCode.getError(24));
                return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
            }

            RatingResponse response = ratingService.createRating(ratingRequest);
            apiResponse.ok(response);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);

        } catch (UniqueConstraintException e) {
            apiResponse.error(ResponseCode.getError(25));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') " +
            "and authentication.principal.getId() == #studentId)")
    @DeleteMapping("student/{studentId}/course/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRatingByStudentIdAndCourseId(
            @PathVariable Long studentId,
            @PathVariable Long courseId
    ) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();

        try {
            ratingService.deleteRatingByStudentIdAndCourseId(studentId, courseId);
            apiResponse.ok(true);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (UserNotFoundException u) {
            errors.put("message", "Student not found");
            apiResponse.error(errors);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (CourseException c) {
            errors.put("message", "Course not found");
            apiResponse.error(errors);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (EnrollmentException e) {
            errors.put("message", "This student has not registered this course");
            apiResponse.error(errors);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            throw e;
        }
    }

    @GetMapping("/course-rating/{courseId}")
    public ResponseEntity<ApiResponse<CourseRatingResponse>> getRatingsByCourseId(@PathVariable Long courseId) {
        ApiResponse<CourseRatingResponse> apiResponse = new ApiResponse<>();

        try {
            CourseRatingResponse courseRatingResponse = ratingService.getCourseRatingsByCourseId(courseId);
            apiResponse.ok(courseRatingResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CourseException c) {
            apiResponse.error(ResponseCode.getError(10));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (ApplicationException e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
