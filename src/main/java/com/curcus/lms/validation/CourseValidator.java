package com.curcus.lms.validation;

import com.curcus.lms.service.CategorySevice;
import com.curcus.lms.service.InstructorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.request.CourseRequest;
import com.curcus.lms.repository.InstructorRepository;

@Component
public class CourseValidator implements Validator {
    @Autowired
    private CategorySevice categoryService;
    @Autowired
    private InstructorService instructorService;
    @Autowired
    private InstructorRepository instructorRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CourseRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CourseRequest product = (CourseRequest) target;
        Category category = categoryService.findById(product.getCategoryId());
        if (category == null) {
            errors.rejectValue("categoryId", "error.categoryId",
                    "Category has not existed with id " + product.getCategoryId());
        }
        Instructor instructor = instructorRepository.findById(product.getInstructorId()).orElse(null);
        if (instructor == null) {
            errors.rejectValue("instructorId", "error.instructorId",
                    "Instructor has not existed with id " + product.getInstructorId());
        }

    }

}
