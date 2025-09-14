package com.curcus.lms.controller;

import java.util.Map;

import com.curcus.lms.model.response.InstructorStatisticResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.InstructorResponse;
import com.curcus.lms.service.InstructorStatisticService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statisticInstructor")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class InstructorStatisticController {
    private final InstructorStatisticService instructorStatisticService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/totalRevenue" })
    public ResponseEntity<ApiResponse<Long>> recoverInstructorPasswordEntity(@PathVariable Long id) {
        try {
            Long revenue = instructorStatisticService.getTotalrevenue(id);
            ApiResponse<Long> apiResponse = new ApiResponse<>();
            apiResponse.ok(revenue);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/totalCourses" })
    public ResponseEntity<ApiResponse<Long>> getTotalCourses(@PathVariable Long id) {
        try {
            Long totalCourses = instructorStatisticService.getTotalCourses(id);
            ApiResponse<Long> apiResponse = new ApiResponse<>();
            apiResponse.ok(totalCourses);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/totalUsersBuy" })
    public ResponseEntity<ApiResponse<Long>> getTotalUsersBuyedCourses(@PathVariable Long id) {
        try {
            Long totalUsers = instructorStatisticService.getTotalUsersBuyedCourses(id);
            ApiResponse<Long> apiResponse = new ApiResponse<>();
            apiResponse.ok(totalUsers);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/topCourse" })
    public ResponseEntity<ApiResponse<CourseResponse>> GetTheMostPurchasedCourse(@PathVariable Long id) {
        try {
            CourseResponse course = instructorStatisticService.getTheMostPurchasedCourse(id);
            ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(course);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/revenuePerYear" })
    public ResponseEntity<ApiResponse<Map<Long, Long>>> getRevenueStatisticsForYears(@PathVariable Long id,
            @RequestParam(defaultValue = "5") int year) {
        try {
            Map<Long, Long> revenuePerYear = instructorStatisticService.getRevenueStatisticsForYears(id, year);
            ApiResponse<Map<Long, Long>> apiResponse = new ApiResponse<>();
            apiResponse.ok(revenuePerYear);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/coursesPerYear" })
    public ResponseEntity<ApiResponse<Map<Long, Long>>> getTotalCoursesForYears(@PathVariable Long id,
            @RequestParam(defaultValue = "5") int year) {
        try {
            Map<Long, Long> revenuePerYear = instructorStatisticService.getTotalCoursesForYears(id, year);
            ApiResponse<Map<Long, Long>> apiResponse = new ApiResponse<>();
            apiResponse.ok(revenuePerYear);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #id)")
    @GetMapping(value = { "/{id}/statistic" })
    public ResponseEntity<ApiResponse<InstructorStatisticResponse>> getStatistic(@PathVariable Long id,
                                                                                 @RequestParam(defaultValue = "5") int year) {
        try {
            InstructorStatisticResponse statisticResponse = instructorStatisticService.getStatistic(id, year);
            ApiResponse<InstructorStatisticResponse> apiResponse = new ApiResponse<>();
            apiResponse.ok(statisticResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

}
