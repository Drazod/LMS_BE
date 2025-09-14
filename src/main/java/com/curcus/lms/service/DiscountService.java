package com.curcus.lms.service;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.BindingResult;

import com.curcus.lms.model.entity.StudentDiscount;
import com.curcus.lms.model.entity.Discount;
import com.curcus.lms.model.entity.Section;
import com.curcus.lms.model.request.CourseCreateRequest;
import com.curcus.lms.model.request.DiscountRequest;
import com.curcus.lms.model.request.SectionRequest;

import com.curcus.lms.model.response.ContentCreateResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.DiscountResponse;
import com.curcus.lms.model.response.StudentDiscountResponse;

import jakarta.validation.Valid;

public interface DiscountService {

    List<DiscountResponse> findAll();

    List<StudentDiscountResponse> findAllDiscountFromStudent(Long studentId);

    List<StudentDiscountResponse> findAllDiscountFromDiscountId(Long discountId, Long studentId);

    List<StudentDiscountResponse> findAllDiscountByIsUsed(Boolean isUsed, Long studentId);

    DiscountResponse findDiscountById(Long discountId);

    DiscountResponse createDiscount(DiscountRequest discountRequest);

    DiscountResponse deleteDisCount(Long id);

    DiscountResponse updateDiscount(DiscountRequest discountRequest, Long discount_id);

    StudentDiscountResponse addDiscountToStudent(Long discountId, Long studentId);

    void deleteDiscountFromStudent(Long discountId, Long studentId);

    Long findDiscountByCode(String discountCode, Long studentId);

}
