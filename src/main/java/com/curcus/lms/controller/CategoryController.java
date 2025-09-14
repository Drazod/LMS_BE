package com.curcus.lms.controller;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.mapper.OthersMapper;
import com.curcus.lms.model.response.CategoryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.request.CourseRequest;
import com.curcus.lms.model.response.ApiResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.service.CategorySevice;
import com.curcus.lms.service.CourseService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    @Autowired
    private CategorySevice categorySevice;
    @Autowired
    private OthersMapper othersMapper;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.ok(othersMapper.toCategoryResponseList(categorySevice.getAllCategory()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable("id") Long id) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        Category category = categorySevice.findById(id);
        if (category == null) {
            errors.put("message", "Category not found");
            apiResponse.error(errors);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } else {
            apiResponse.ok(othersMapper.toCategoryResponse(category));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
    }
}
