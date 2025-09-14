package com.curcus.lms.repository;

import com.curcus.lms.model.entity.Content;
import com.curcus.lms.model.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Content c WHERE c.id = :contentId")
    void deleteContentById(@Param("contentId") Long contentId);
    List<Content> findBySectionOrderByPosition(Section section);

    @Modifying
    @Transactional
    @Query("DELETE FROM Content c WHERE c.section.sectionId = :sectionId")
    void deleteAllContentBySectionId(@Param("sectionId") Long sectionId);

    Boolean existsBySection_Course_Instructor_UserIdAndId(Long instructorId, Long contentId);
}
