package com.curcus.lms.model.mapper;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Rating;
import com.curcus.lms.model.response.CategoryResponse;
import com.curcus.lms.model.response.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OthersMapper {

    // Category
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "categoryName", target = "categoryName")
    public abstract CategoryResponse toCategoryResponse(Category category);

    public abstract List<CategoryResponse> toCategoryResponseList(List<Category> categories);

    // Rating
    @Mapping(source = "ratingId", target = "ratingId")
    @Mapping(source = "student.userId", target = "studentId")
    @Mapping(source = "course.courseId", target = "courseId")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "ratingDate", target = "ratingDate")
    public abstract RatingResponse toRatingResponse(Rating rating);

    public abstract List<RatingResponse> toRatingResponseList(List<Rating> ratings);
}
