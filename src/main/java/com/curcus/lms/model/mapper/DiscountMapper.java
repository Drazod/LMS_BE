package com.curcus.lms.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.curcus.lms.model.entity.Discount;
import com.curcus.lms.model.entity.StudentDiscount;
import com.curcus.lms.model.request.DiscountRequest;

import com.curcus.lms.model.response.DiscountResponse;
import com.curcus.lms.model.response.StudentDiscountResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(source = "description", target = "description")
    @Mapping(source = "value", target = "value")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    public Discount toEntity(DiscountRequest discountRequest);

    public List<Discount> toListEntity(List<DiscountRequest> discountRequest);

    public DiscountResponse toResponse(Discount discount);

    public List<DiscountResponse> toListResponse(List<Discount> discountRequest);

    @Mapping(source = "student.userId", target = "studentId")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "isUsed", target = "isUsed")
    public StudentDiscountResponse toResponse(StudentDiscount studentDiscount);

    @Mapping(source = "student.userId", target = "studentId")
    @Mapping(source = "discount.discountId", target = "discountId")
    @Mapping(source = "isUsed", target = "isUsed")
    public List<StudentDiscountResponse> toListResponseDiscountFromStudent(List<StudentDiscount> studentDiscount);
}
