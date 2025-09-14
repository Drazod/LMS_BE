package com.curcus.lms.repository;

import com.curcus.lms.model.entity.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.response.CourseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
        Page<Course> findByCategory(Category category, Pageable pageable);

        List<Course> findByCategory(Category category);

        Page<Course> findAll(Pageable pageable);

        @Query("SELECT c " +
                        "FROM Course c " +
                        "JOIN OrderItems oi ON oi.course.courseId = c.courseId " +
                        "WHERE c.instructor.userId = :instructorId " +
                        "AND c.price > 0 " +
                        "GROUP BY c " +
                        "ORDER BY COUNT(c) DESC")
        Page<Course> getTheMostPurchasedCourses(@Param("instructorId") Long instructorId, Pageable pageable);

        Boolean existsByInstructor_UserIdAndCourseId(Long userId, Long courseId);

        @EntityGraph(attributePaths = { "sections", "sections.contents", "instructor", "category" })
        Course findWithSectionsByCourseId(Long courseId);

        @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.userId = :userId")
        Long countCoursesOfInstructor(@Param("userId") Long userId);

        @Query(nativeQuery = true, value = "select count(*) from courses c where c.instructor_id = :instructorId and c.created_at <= :endDate and c.created_at >= :startDate")
        Long getTotalCoursesForYears(Long instructorId, LocalDate startDate, LocalDate endDate);

        // @Query(value = "SELECT * FROM courses c WHERE c.instructor_id =
        // :instructorId", nativeQuery = true)
        Page<Course> findByInstructorUserId(@Param("instructorId") Long instructorId, Pageable pageable);

        @Query(value = "select * from courses a where a.status = :status ", nativeQuery = true)
        List<Course> getCourseByIsApproved(@Param("status") String status, Pageable pageable);

}
