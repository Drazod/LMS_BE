package com.curcus.lms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.StudentDiscount;
import com.curcus.lms.model.request.DiscountRequest;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.DiscountResponse;
import com.curcus.lms.model.response.StudentDiscountResponse;
import com.curcus.lms.repository.StudentDiscountRepository;
import com.curcus.lms.service.DiscountService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class DiscountController {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private StudentDiscountRepository studentDiscountRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = { "", "/list" })
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getAllDiscountById(
            @RequestParam(value = "discountId", required = false) Long discountId) {
        try {
            List<DiscountResponse> discountResponses = null;
            ApiResponse apiResponse = new ApiResponse<>();
            if (discountId == null) {
                discountResponses = discountService.findAll();
            } else {
                discountResponses.add(discountService.findDiscountById(discountId));
            }
            if (discountResponses.isEmpty()) {
                throw new NotFoundException("No discount exists");
            }
            apiResponse.ok(discountResponses);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping(value = { "/listDiscountFromStudent" })
    public ResponseEntity<ApiResponse<List<StudentDiscountResponse>>> getListDiscountFromStudent(
            @RequestParam(value = "discountId", required = false) Long discountId,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam Long studentId) {
        try {
            List<StudentDiscountResponse> studentDiscountResponses = null;
            ApiResponse apiResponse = new ApiResponse<>();
            if (discountId == null && isUsed == null) {
                studentDiscountResponses = discountService.findAllDiscountFromStudent(studentId);
            } else if (discountId != null) {
                studentDiscountResponses = discountService.findAllDiscountFromDiscountId(discountId, studentId);
            } else if (discountId == null && isUsed != null) {
                studentDiscountResponses = discountService.findAllDiscountByIsUsed(isUsed, studentId);
            }
            if (studentDiscountResponses.isEmpty()) {
                throw new NotFoundException("No discount exists");
            }
            apiResponse.ok(studentDiscountResponses);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<DiscountResponse>> createDiscount(
            @Valid @RequestBody DiscountRequest discountRequest) {
        try {
            DiscountResponse discountResponse = discountService.createDiscount(discountRequest);
            ApiResponse apiResponse = new ApiResponse<>();
            apiResponse.ok(discountResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{discount_id}/updateDiscount")
    public ResponseEntity<ApiResponse<DiscountResponse>> updateCourse(@PathVariable Long discount_id,
            @Valid @RequestBody DiscountRequest discountRequest) {
        try {
            DiscountResponse discountCheck = discountService.findDiscountById(discount_id);
            if (discountCheck == null)
                throw new NotFoundException("Discount has not existed with id " +
                        discount_id);
            DiscountResponse discountResponse = discountService.updateDiscount(discountRequest, discount_id);
            ApiResponse apiResponse = new ApiResponse<>();
            apiResponse.ok(discountResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<DiscountResponse>> deleteCourse(@Valid @PathVariable("id") Long id) {
        try {
            ApiResponse apiResponse = new ApiResponse<>();
            apiResponse.ok(discountService.deleteDisCount(id));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/addDiscountToStudent")
    public ResponseEntity<ApiResponse<StudentDiscount>> addDiscountToStudent(
            @RequestParam Long discountId, @RequestParam Long studentId) {
        try {
            StudentDiscountResponse studentDiscountResponse = discountService.addDiscountToStudent(discountId,
                    studentId);
            ApiResponse apiResponse = new ApiResponse<>();
            apiResponse.ok(studentDiscountResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ValidationException ex) {
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping(value = "/getDiscountByCode/{code}")
    public ResponseEntity<ApiResponse<Long>> getDiscountByCode(
            @RequestParam Long studentId, @PathVariable("code") String discountCode) {
        try {
            Long discountId = discountService.findDiscountByCode(discountCode,
                    studentId);
            ApiResponse<Long> apiResponse = new ApiResponse<>();
            apiResponse.ok(discountId);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }
}