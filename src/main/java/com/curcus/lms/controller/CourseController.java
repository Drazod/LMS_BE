package com.curcus.lms.controller;

import com.curcus.lms.model.request.*;
import com.curcus.lms.model.response.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.curcus.lms.constants.CourseSearchOptions;
import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.SearchOptionsException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.mapper.CourseMapper;
import com.curcus.lms.model.entity.Section;
import com.curcus.lms.service.CourseService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PutMapping;
import com.curcus.lms.model.dto.*;

@RestController
@RequestMapping("/api/courses")
@Validated
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseMapper courseMapper;

    // @GetMapping(value = {"", "/list"})
    // public ResponseEntity<ApiResponse<List<CourseSearchResponse>>> getAllCourses(
    // @RequestParam(value = "category", required = false) Long category,
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "10") int size) {
    // try {
    // Pageable pageable = PageRequest.of(page, size);
    // Page<CourseSearchResponse> coursePage;
    //
    // if (category == null) {
    // coursePage = courseService.findAll(pageable);
    // } else {
    // coursePage = courseService.findByCategory(category, pageable);
    // }
    //
    // if (coursePage.isEmpty()) {
    // throw new NotFoundException("Course not found.");
    // }
    //
    // MetadataResponse metadata = new MetadataResponse(
    // coursePage.getTotalElements(),
    // coursePage.getTotalPages(),
    // coursePage.getNumber(),
    // coursePage.getSize(),
    // (coursePage.hasNext() ? "/api/courses/list?page=" + (coursePage.getNumber() +
    // 1) : null),
    // (coursePage.hasPrevious() ? "/api/courses/list?page=" +
    // (coursePage.getNumber() - 1) : null),
    // "/api/courses/list?page=" + (coursePage.getTotalPages() - 1),
    // "/api/courses/list?page=0"
    // );
    //
    // ApiResponse<List<CourseSearchResponse>> apiResponse = new ApiResponse<>();
    // Map<String, Object> responseMetadata = new HashMap<>();
    // responseMetadata.put("pagination", metadata);
    // apiResponse.ok(coursePage.getContent(), responseMetadata);
    // return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    // } catch (NotFoundException ex) {
    // throw ex;
    // } catch (Exception ex) {
    // throw new ApplicationException();
    // }
    // }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #courseRequest.instructorId)")
    @PutMapping(value = "/updateCourse")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@Valid @RequestBody CourseRequest courseRequest,
            BindingResult bindingResult) {
        try {
            // CourseResponse courseResponse = courseService.update(courseRequest,
            // bindingResult);
            ApiResponse apiResponse = new ApiResponse<>();
            // apiResponse.ok(courseResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
            // other exception throw application() (not exception of user)
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') and authentication.principal.getId() == #courseCreateRequest.instructorId)")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @ModelAttribute @Valid @RequestBody CourseCreateRequest courseCreateRequest) {
        CourseResponse courseResponse = courseService.saveCourse(courseCreateRequest);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(courseResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @courseRepository.existsByInstructor_UserIdAndCourseId(authentication.principal.getId(), #id))")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> deleteCourse(@Valid @PathVariable("id") Long id) {
        System.err.println("deleteCourse");
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(courseService.deleteCourse(id));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @courseRepository.existsByInstructor_UserIdAndCourseId(authentication.principal.getId(), #sectionRequest.courseId))")
    @PostMapping(value = "/addSection")
    public ResponseEntity<ApiResponse<SectionCreateResponse>> createSection(
            @RequestBody SectionRequest sectionRequest) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(courseService.createSection(sectionRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @sectionRepository.existsByCourse_Instructor_UserIdAndSectionId(authentication.principal.getId(), #contentCreateRequest.sectionId))")
    @PostMapping(value = "/addContent/document")
    public ResponseEntity<ApiResponse<ContentCreateResponse>> createDocumentContent(
            @RequestBody @Valid ContentDocumentCreateRequest contentCreateRequest) {
        ContentCreateResponse contentCreateResponse = courseService
                .saveDocumentContent(contentCreateRequest);

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(contentCreateResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
             "and @sectionRepository.existsByCourse_Instructor_UserIdAndSectionId(authentication.principal.getId(), #contentCreateRequest.sectionId))")
    @PostMapping(value = "/addContent/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ContentCreateResponse>> createVideoContent(
            @ModelAttribute @Valid @RequestBody ContentVideoCreateRequest contentCreateRequest) {
        ContentCreateResponse contentCreateResponse = courseService
                .saveVideoContent(contentCreateRequest);

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(contentCreateResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);

    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @contentRepository.existsBySection_Course_Instructor_UserIdAndId(authentication.principal.getId(), #contentCreateRequest.contentId))")
    @PostMapping(value = "/updateContent/document")
    public ResponseEntity<ApiResponse<ContentCreateResponse>> updateDocumentContent(
            @RequestBody @Valid ContentDocumentUpdateRequest contentCreateRequest) {
        ContentCreateResponse contentCreateResponse = courseService
                .updateDocumentContent(contentCreateRequest);

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(contentCreateResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @contentRepository.existsBySection_Course_Instructor_UserIdAndId(authentication.principal.getId(), #contentCreateRequest.contentId))")
    @PostMapping(value = "/updateContent/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ContentCreateResponse>> updateVideoContent(
            @ModelAttribute @Valid @RequestBody ContentVideoUpdateRequest contentCreateRequest) {
        ContentCreateResponse contentCreateResponse = courseService
                .updateVideoContent(contentCreateRequest);

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(contentCreateResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);

    }

    private String buildBaseUrl(Long instructorId, Long categoryId, String title, Long minPrice,Long maxPrice, Boolean isFree,
            String sort, String direction) {
        StringBuilder baseUrl = new StringBuilder("/api/courses/search?");
        if (instructorId != null)
            baseUrl.append("instructorId=").append(instructorId).append("&");
        if (categoryId != null)
            baseUrl.append("categoryId=").append(categoryId).append("&");
        if (title != null)
            baseUrl.append("title=").append(title).append("&");
        if (minPrice != null)
            baseUrl.append("minPrice=").append(minPrice).append("&");
            if (maxPrice != null)
            baseUrl.append("maxPrice=").append(maxPrice).append("&");
        if (isFree != null)
            baseUrl.append("isFree=").append(isFree).append("&");
        if (sort != null)
            baseUrl.append("sort=").append(sort).append("&");
        if (direction != null)
            baseUrl.append("direction=").append(direction).append("&");
        return baseUrl.toString();
    }

    private MetadataResponse createPaginationMetadata(Page<CourseSearchResponse> coursePage, String baseUrlStr,
            int size) {
        return new MetadataResponse(
                coursePage.getTotalElements(),
                coursePage.getTotalPages(),
                coursePage.getNumber(),
                coursePage.getSize(),
                (coursePage.hasNext() ? baseUrlStr + "page=" + (coursePage.getNumber() + 1) + "&size=" + size : null),
                (coursePage.hasPrevious() ? baseUrlStr + "page=" + (coursePage.getNumber() - 1) + "&size=" + size
                        : null),
                baseUrlStr + "page=" + (coursePage.getTotalPages() - 1) + "&size=" + size,
                baseUrlStr + "page=0&size=" + size);
    }

    @GetMapping({ "search", "", "/list" })
    public ResponseEntity<ApiResponse<List<CourseSearchResponse>>> searchCourses(
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            // @RequestParam(defaultValue = "0") Long minStar,
            // @RequestParam(defaultValue = "5") Long maxStar,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction// parameter for sorting direction
    ) {
        if (sort != null && !CourseSearchOptions.SORT_OPTIONS.contains(sort)) {
            throw new SearchOptionsException("Invalid sort parameter.");
        }

        if (direction != null && !CourseSearchOptions.DIRECTION_OPTIONS.contains(direction.toLowerCase())) {
            throw new SearchOptionsException("Invalid direction parameter.");
        }

        // Validate direction parameter
        Sort.Direction sortDirection = Sort.Direction.ASC; // default to ascending
        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        // Build pageable with sorting
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = PageRequest.of(page, size, sortDirection, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        // Call service method to search courses
        Page<CourseSearchResponse> coursePage = courseService.searchCourses(instructorId, categoryId, title, minPrice,maxPrice,
                isFree, pageable);

        // Handle empty result
        if (coursePage.isEmpty()) {
            throw new NotFoundException("Course not found.");
        }

        // Create base URL with query parameters
        String baseUrlStr = buildBaseUrl(instructorId, categoryId, title, minPrice,maxPrice, isFree, sort, direction);

        // Create pagination metadata
        MetadataResponse metadata = createPaginationMetadata(coursePage, baseUrlStr, size);

        // Create API response
        ApiResponse<List<CourseSearchResponse>> apiResponse = new ApiResponse<>();
        Map<String, Object> responseMetadata = new HashMap<>();

        responseMetadata.put("pagination", metadata);
        responseMetadata.put("searchOptions", CourseSearchOptions.HINTS_MAP);

        apiResponse.ok(coursePage.getContent(), responseMetadata);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/details/{courseId}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseDetails(@PathVariable Long courseId) {

        CourseDetailResponse course = courseService.getCourseDetails(courseId);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(course);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    // @PutMapping(value="sections/{id}/updateContent")
    // public ResponseEntity<ApiResponse<ContentCreateResponse>> updateContent(
    // @PathVariable Long id,
    // @RequestBody @Valid ContentUpdateRequest contentUpdateRequest) {
    // ContentCreateResponse contentUpdateResponse = courseService.updateContent(id,
    // contentUpdateRequest);
    // ApiResponse apiResponse = new ApiResponse<>();
    // apiResponse.ok(contentUpdateResponse);
    // return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    // }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @sectionRepository.existsByCourse_Instructor_UserIdAndSectionId(authentication.principal.getId(), #sectionId))")
    @PutMapping("/sections/{sectionId}/contents/positions")
    public ResponseEntity<ApiResponse<List<ContentCreateResponse>>> updateContentPositions(
            @PathVariable Long sectionId,
            @RequestBody @Valid ContentPositionUpdateWrapper wrapper) {
        List<ContentCreateResponse> updatedContents = courseService.updateContentPositions(sectionId,
                wrapper.getUpdates());
        ApiResponse<List<ContentCreateResponse>> apiResponse = new ApiResponse<>();
        apiResponse.ok(updatedContents);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @courseRepository.existsByInstructor_UserIdAndCourseId(authentication.principal.getId(), #courseStatusRequest.courseId)"
            +
            // instructor chỉ được đổi status sang CREATED hoặc PENDING_APPROVAL chứ ko được
            // đổi sang APPROVED hay REJECTED
            "and (#courseStatusRequest.status == 'CREATED' or #courseStatusRequest.status == 'PENDING_APPROVAL'))")
    @PutMapping("/update-course-status")
    public ResponseEntity<ApiResponse<CourseStatusResponse>> updateCourseStatus(
            @Valid @RequestBody CourseStatusRequest courseStatusRequest,
            BindingResult bindingResult) {
        ApiResponse<CourseStatusResponse> apiResponse = new ApiResponse<>();
        try {
            if (bindingResult.hasErrors()) {
                apiResponse.error(ResponseCode.getError(1));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }

            apiResponse.ok(courseService.updateCourseStatus(courseStatusRequest.getCourseId(),
                    courseStatusRequest.getStatus()));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException e) {
            apiResponse.error(ResponseCode.getError(10));
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            apiResponse.error(ResponseCode.getError(1));
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @courseRepository.existsByInstructor_UserIdAndCourseId(authentication.principal.getId(), #courseId))")
    @PutMapping("/{courseId}/sections/positions")
    public ResponseEntity<ApiResponse<List<SectionUpdatePositionRes>>> updateSectionPositions(
            @PathVariable Long courseId,
            @RequestBody @Valid SectionPositionUpdateWrapper wrapper) {
        List<SectionUpdatePositionRes> updatedSections = courseService.updateSectionPositions(courseId,
                wrapper.getUpdates());
        ApiResponse<List<SectionUpdatePositionRes>> apiResponse = new ApiResponse<>();
        apiResponse.ok(updatedSections);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/courses/getUnapprovedCourses")
    public ResponseEntity<ApiResponse<List<CourseDetailResponse3>>> unapprovedCourse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        ApiResponse<List<CourseDetailResponse3>> apiResponse = new ApiResponse<>();
        try {

            apiResponse.ok(courseService.unapprovedCourse(pageable));
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception e) {
            apiResponse.error(ResponseCode.getError(23));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_STUDENT') and " +
            "@enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(" +
            "   authentication.principal.getId()," +
            "   @sectionRepository.findById(#sectionID).orElseThrow().getCourse().getCourseId()" +
            "))")
    @GetMapping("section/{sectionID}/contents")
    public ResponseEntity<ApiResponse<SectionDetailResponse2>> getContentsBySection(@PathVariable Long sectionID) {
        SectionDetailResponse2 section = courseService.getContentsBySection(sectionID);
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.ok(section);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @sectionRepository.existsByCourse_Instructor_UserIdAndSectionId(authentication.principal.getId(), #sectionId))")
    @PutMapping("/section/update-name")
    public ResponseEntity<ApiResponse<SectionCreateResponse>> updateSectionName(
            @RequestParam Long sectionId,
            @Valid @RequestBody SectionRequest updateRequest
    )
    {
        SectionCreateResponse section = courseService.updateSection(sectionId, updateRequest);
        ApiResponse<SectionCreateResponse> apiResponse = new ApiResponse<>();
        apiResponse.ok(section);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @contentRepository.existsBySection_Course_Instructor_UserIdAndId(authentication.principal.getId(), #contentId))")
    @DeleteMapping("deleteContent/{contentId}")
    public ResponseEntity<ApiResponse<Void>> deleteContent(@PathVariable Long contentId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
            courseService.deleteContentById(contentId);
            apiResponse.ok(null);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_INSTRUCTOR') " +
            "and @sectionRepository.existsByCourse_Instructor_UserIdAndSectionId(authentication.principal.getId(), #sectionId))")
    @DeleteMapping("deleteSection/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long sectionId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        courseService.deleteSectionById(sectionId);
        apiResponse.ok(null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }

    @DeleteMapping("delete/{sectionId}/listContent")
    public ResponseEntity<ApiResponse<Void>> deleteListContent(
        @PathVariable Long sectionId, @RequestBody @Valid ContentDeleteWrapper wrapper) {
        courseService.deleteListContent(sectionId,wrapper);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.ok(null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
}
