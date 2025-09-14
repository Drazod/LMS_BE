package com.curcus.lms.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.response.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "categoryName", target = "categoryName")
    CategoryResponse toResponse(Category category);
}
