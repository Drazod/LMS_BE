package com.curcus.lms.service;

import com.curcus.lms.model.request.RatingRequest;
import com.curcus.lms.model.response.CourseRatingResponse;
import com.curcus.lms.model.response.RatingResponse;

import java.util.List;

public interface RatingService {
    RatingResponse createRating(RatingRequest ratingRequest);
    RatingResponse updateRating(RatingRequest ratingRequest);

    RatingResponse getRatingByStudentIdAndCourseId(Long studentId, Long courseId);

    List<RatingResponse> getRatingByCourseId(Long courseId);

    void deleteRatingByStudentIdAndCourseId(Long studentId, Long courseId);

    CourseRatingResponse getCourseRatingsByCourseId(Long courseId);
}
