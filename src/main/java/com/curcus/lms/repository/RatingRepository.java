package com.curcus.lms.repository;

import com.curcus.lms.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Boolean existsByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    Rating findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    List<Rating> findAllByStudent_UserId(Long studentId);
    List<Rating> findAllByCourse_CourseId(Long courseId);
    void deleteByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    Long countByCourse_CourseId(Long courseId);
}
