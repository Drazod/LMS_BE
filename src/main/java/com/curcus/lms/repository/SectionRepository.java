package com.curcus.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Section;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Boolean existsByCourse_Instructor_UserIdAndSectionId(Long instructorId, Long sectionId);
    Optional<Section> findByCourse_CourseIdAndPosition(Long courseId, Long position);
    Optional<Section> findTopByCourse_CourseIdOrderByPositionDesc(Long courseId);
    Section findWithContentsBySectionId(Long sectionId);
    List<Section> findByCourseOrderByPosition(Course course);
    @Modifying
    @Transactional
    @Query("DELETE FROM Section s WHERE s.sectionId = :sectionId")
    void deleteSectionById(@Param("sectionId") Long sectionId);

}