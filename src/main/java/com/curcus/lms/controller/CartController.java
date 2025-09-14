package com.curcus.lms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.Cart;
import com.curcus.lms.model.entity.CartItems;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.CourseResponseForCart;
import com.curcus.lms.model.response.CourseSearchResponse;
import com.curcus.lms.model.response.MetadataResponse;
import com.curcus.lms.service.CartService;
import com.curcus.lms.service.StudentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("api/cart")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private StudentService studentService;

    private MetadataResponse createPaginationMetadata(Page<CourseResponseForCart> coursePage, String baseUrlStr,
            int size) {
        return new MetadataResponse(
                coursePage.getTotalElements(),
                coursePage.getTotalPages(),
                coursePage.getNumber(),
                coursePage.getSize(),
                (coursePage.hasNext() ? baseUrlStr + "page=" + (coursePage.getNumber() + 1) + "&size=" + size : null),
                (coursePage.hasPrevious() ? baseUrlStr + "page=" + (coursePage.getNumber() - 1) + "&size=" + size
                        : null),
                baseUrlStr + "page=" + (coursePage.getTotalPages() - 1) + "&size=" + size,
                baseUrlStr + "page=0&size=" + size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @PostMapping(value = "/createCart")
    public ResponseEntity<ApiResponse<Cart>> createCart(@RequestParam Long studentId) {
        try {
            Cart cart = cartService.createCart(studentId);
            ApiResponse<Cart> apiResponse = new ApiResponse<>();
            apiResponse.ok(cart);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @PostMapping(value = "/addCourse")
    public ResponseEntity<ApiResponse<CartItems>> addCourseToCart(@RequestParam Long studentId,
            @RequestParam Long courseId) {
        try {
            CartItems cartItem = cartService.addCourseToCart(studentId, courseId);
            ApiResponse<CartItems> apiResponse = new ApiResponse<>();
            apiResponse.ok(cartItem);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping(value = "/{studentId}/listCourse")
    public ResponseEntity<ApiResponse<List<CourseResponseForCart>>> getListCourseFromCart(@PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseResponseForCart> coursePage = studentService.getListCourseFromCart(studentId, pageable);
            if (coursePage.isEmpty()) {
                throw new NotFoundException("Course not found.");
            }
            String baseUrlStr = String.format("/api/cart/%d/listCourse?", studentId);
            MetadataResponse metadata = createPaginationMetadata(coursePage, baseUrlStr, size);
            ApiResponse<List<CourseResponseForCart>> apiResponse = new ApiResponse<>();
            Map<String, Object> responseMetadata = new HashMap<>();
            responseMetadata.put("pagination", metadata);
            apiResponse.ok(coursePage.getContent(), responseMetadata);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @GetMapping(value = "/{studentId}/getCartId")
    public ResponseEntity<ApiResponse<Long>> getIdCartFromStudent(@PathVariable Long studentId) {
        try {
            Long cartId = cartService.getIdCartFromStudent(studentId);
            ApiResponse<Long> apiResponse = new ApiResponse<>();
            apiResponse.ok(cartId);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Void>> copyCartToOrder(@RequestParam Long studentId,
            @RequestParam List<Long> courseIds, @RequestParam Long totalPrice) {
        try {
            cartService.copyCartToOrder(studentId, courseIds, totalPrice);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.error(error);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @DeleteMapping(value = "/deleteCourseFromCart")
    public ResponseEntity<ApiResponse<Void>> deleteCourseFromCart(@RequestParam Long studentId,
            @RequestParam Long courseId) {
        try {
            cartService.deleteCourseFromCart(studentId, courseId);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ValidationException ex) {
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @DeleteMapping(value = "/deleteAllCourseFromCart")
    public ResponseEntity<ApiResponse<Void>> deleteAllCourseFromCart(@RequestParam Long studentId) {
        try {
            cartService.deleteAllCourseFromCart(studentId);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ValidationException ex) {
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @DeleteMapping(value = "/deleteCart")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@RequestParam Long studentId) {
        try {
            cartService.deleteCart(studentId);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ValidationException ex) {
            throw ex;
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and authentication.principal.getId() == #studentId)")
    @DeleteMapping(value = "/deleteListCourseFromCart")
    public ResponseEntity<ApiResponse<Void>> deleteListCourse(@RequestParam Long studentId,
            @RequestParam List<Long> listCourseDelete) {
        try {
            cartService.deleteListCourseFromCart(studentId, listCourseDelete);
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.ok();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ValidationException ex) {
            throw ex;
        }
    }
}
