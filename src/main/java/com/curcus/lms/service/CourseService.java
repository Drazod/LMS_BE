package com.curcus.lms.service;

import java.util.List;

import com.curcus.lms.model.request.*;
import com.curcus.lms.model.response.*;

import org.springframework.validation.BindingResult;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.curcus.lms.model.dto.ContentDeleteWrapper;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.entity.Section;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

public interface CourseService {
        // List<CourseResponse> findByCategory(Long categoryId);

        CourseResponse deleteCourse(Long id);

        ContentCreateResponse updateVideoContent(ContentVideoUpdateRequest contentVideoUpdateRequest);

        ContentCreateResponse updateDocumentContent(ContentDocumentUpdateRequest contentDocumentUpdateRequest);

        SectionCreateResponse createSection(SectionRequest sectionRequest);

        Page<CourseSearchResponse> findByCategory(Long categoryId, Pageable pageable);

        Page<CourseSearchResponse> findAll(Pageable pageable);

        CourseResponse saveCourse(CourseCreateRequest courseCreateRequest);

        // Page<CourseResponse> searchCoursesByName(String name,Pageable pageable);
        // Page<CourseResponse> searchCourses(Long instructorId, Long categoryId, String
        // title, Long minPrice, Long maxPrice, Pageable pageable);
        Page<CourseSearchResponse> searchCourses(Long instructorId, Long categoryId, String title, Long minprice, Long maxprice,
                        Boolean isFree, Pageable pageable);

        ContentCreateResponse saveVideoContent(ContentVideoCreateRequest contentCreateRequest);

        ContentCreateResponse saveDocumentContent(ContentDocumentCreateRequest contentCreateRequest);

        Course findById(Long id);

        Instructor findByIdInstructor(Long id);

        CourseResponse update(CourseRequest courseRequest, BindingResult bindingResult);

        void checkCourseRequest(CourseRequest courseRequest, BindingResult bindingResult);

        SectionCreateResponse updateSection(Long sectionId, SectionRequest sectionRequest);

        CourseDetailResponse getCourseDetails(Long courseId);

        CourseStatusResponse updateCourseStatus(Long courseId, String status);

        // CourseResponse update(CourseRequest courseRequest, BindingResult
        // bindingResult);
        Page<CourseDetailResponse2> getCoursebyInstructorId(Long id, Pageable pageable);

        // ContentCreateResponse updateContent(Long id, @Valid ContentUpdateRequest
        // contentUpdateRequest);

        List<ContentCreateResponse> updateContentPositions(Long id,
                        @Valid List<ContentUpdatePositionRequest> positionUpdates);

        List<SectionUpdatePositionRes> updateSectionPositions(Long id,
                        @Valid List<SectionUpdatePositionRequest> positionUpdates);

        List<CourseDetailResponse3> unapprovedCourse(Pageable pageable);

        SectionDetailResponse2 getContentsBySection(Long id);



        void deleteContentById(Long contentId);
        void deleteSectionById(Long contentId);
        void deleteListContent(Long sectionId, @Valid ContentDeleteWrapper wrapper);
}
