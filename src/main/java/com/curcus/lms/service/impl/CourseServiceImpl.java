package com.curcus.lms.service.impl;

import com.curcus.lms.model.request.*;
import com.curcus.lms.model.response.*;
import com.curcus.lms.model.dto.ContentDeleteWrapper;
import com.curcus.lms.model.dto.SessionContentItem;
import com.curcus.lms.model.entity.*;
import com.curcus.lms.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.curcus.lms.service.CategorySevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.curcus.lms.validation.FileValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.mapper.ContentMapper;
import com.curcus.lms.model.mapper.CourseMapper;
import com.curcus.lms.model.mapper.SectionMapper;
import com.curcus.lms.service.CourseService;
import com.curcus.lms.specification.CourseSpecifications;
import com.curcus.lms.service.InstructorService;
import com.curcus.lms.util.FileAsyncUtil;
import com.curcus.lms.util.ValidatorUtil;
import com.curcus.lms.validation.CourseValidator;
import com.curcus.lms.validation.InstructorValidator;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseValidator courseValidator;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private ValidatorUtil validatorUtil;
    @Autowired
    private CategorySevice categoryService;
    @Autowired
    private InstructorValidator instructorValidator;
    @Autowired
    private InstructorService instructorService;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private FileAsyncUtil fileAsyncUtil;

    @Override
    public Page<CourseSearchResponse> findAll(Pageable pageable) {
        try {
            Page<Course> coursesPage = courseRepository.findAll(pageable);
            return coursesPage.map(courseMapper::toCourseSearchResponse);
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public Course findById(Long id) {
        try {
            return courseRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public Instructor findByIdInstructor(Long id) {
        try {
            return instructorRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public Page<CourseSearchResponse> findByCategory(Long categoryId, Pageable pageable) {
        try {
            Category category = new Category();
            category.setCategoryId(categoryId);
            Page<Course> coursesPage = courseRepository.findByCategory(category, pageable);
            return coursesPage.map(courseMapper::toCourseSearchResponse);
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    // @Override
    // public CourseResponse saveCourse(CourseCreateRequest courseCreateRequest) {
    // // TODO Auto-generated method stub
    // Instructor instructor =
    // instructorRepository.findById(courseCreateRequest.getInstructorId())
    // .orElseThrow(() -> new NotFoundException(
    // "Instructor has not existed with id" +
    // courseCreateRequest.getInstructorId()));
    // Category category =
    // categoryRepository.findById(courseCreateRequest.getCategoryId())
    // .orElseThrow(() -> new NotFoundException(
    // "Category has not existed with id " + courseCreateRequest.getCategoryId()));

    // Course course = courseMapper.toEntity(courseCreateRequest);
    // System.out.println(course.toString());
    // Course savedCourse = courseRepository.save(course);
    // return courseMapper.toResponse(savedCourse);
    // }

    @Override
    @Transactional
    public CourseResponse deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Course has not existed with id " + id));
        if (!course.getEnrollment().isEmpty())
            throw new ValidationException("The course cannot be deleted because someone is currently enrolled");
        courseRepository.deleteById(id);

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse saveCourse(CourseCreateRequest courseCreateRequest) {
        // TODO Auto-generated method stub
        Course course = courseMapper.toEntity(courseCreateRequest);
        fileAsyncUtil.validImage(courseCreateRequest.getCourseThumbnail());
        Course savedCourse = courseRepository.save(course);
        fileAsyncUtil.uploadImageAsync(course.getCourseId(), courseCreateRequest.getCourseThumbnail());
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    public ContentCreateResponse saveVideoContent(ContentVideoCreateRequest contentCreateRequest) {
        // TODO Auto-generated method stub
        Content content = contentMapper.toEntity(contentCreateRequest);
        FileValidation.validateVideoType(contentCreateRequest.getFile().getOriginalFilename());
        content = contentRepository.save(content);
        byte[] fileBytes = null;
        try {
            fileBytes = contentCreateRequest.getFile().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileAsyncUtil.uploadFileAsync(content.getId(), fileBytes);
        return contentMapper.toResponse(content);

    }

    @Override
    public ContentCreateResponse saveDocumentContent(ContentDocumentCreateRequest contentCreateRequest) {
        // TODO Auto-generated method stub
        Content content = contentMapper.toEntity(contentCreateRequest);
        content = contentRepository.save(content);
        return contentMapper.toResponse(content);

    }


    @Override
    public ContentCreateResponse updateVideoContent(ContentVideoUpdateRequest contentVideoUpdateRequest) {
        // TODO Auto-generated method stub
        Content content = contentRepository.findById(contentVideoUpdateRequest.getContentId())
                .orElseThrow(() -> new NotFoundException("Content not found for section ID: " + contentVideoUpdateRequest.getContentId()));

        FileValidation.validateVideoType(contentVideoUpdateRequest.getFile().getOriginalFilename());
        byte[] fileBytes = null;
        try {
            fileBytes = contentVideoUpdateRequest.getFile().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileAsyncUtil.updateFileAsync(content.getId(), fileBytes, content.getContent());
        return contentMapper.toResponse(content);

    }

    @Override
    public ContentCreateResponse updateDocumentContent(ContentDocumentUpdateRequest contentDocumentUpdateRequest) {
        // TODO Auto-generated method stub
        Content content = contentRepository.findById(contentDocumentUpdateRequest.getContentId())
                .orElseThrow(() -> new NotFoundException("Content not found for section ID: " + contentDocumentUpdateRequest.getContentId()));
        content.setContent(contentDocumentUpdateRequest.getContent());
        content = contentRepository.save(content);
        return contentMapper.toResponse(content);

    }

    @Override
    public SectionCreateResponse createSection(SectionRequest sectionRequest) {
    Section section = new Section();
    Course course = courseRepository.findById(sectionRequest.getCourseId())
        .orElseThrow(() -> new NotFoundException(
            "Course has not existed with id " + sectionRequest.getCourseId()));

    section.setCourse(course);
    section.setSectionName(sectionRequest.getSectionName());
    // Auto-increment position: find max position for this course
    Long maxPosition = sectionRepository.findTopByCourse_CourseIdOrderByPositionDesc(course.getCourseId())
        .map(Section::getPosition)
        .orElse(0L);
    section.setPosition(maxPosition + 1);
    SectionCreateResponse sectionCreateResponse = sectionMapper.toResponse(sectionRepository.save(section));
    return sectionCreateResponse;
    }

    @Override
    @Transactional
    public SessionCreateResponse createSession(SessionCreateRequest sessionCreateRequest) {
        // Find the course
        Course course = courseRepository.findById(sessionCreateRequest.getCourseId())
            .orElseThrow(() -> new NotFoundException(
                "Course has not existed with id " + sessionCreateRequest.getCourseId()));

        // Create the section (session)
        Section section = new Section();
        section.setCourse(course);
        section.setSectionName(sessionCreateRequest.getSectionName());
        section.setTitle(sessionCreateRequest.getTitle());
        section.setDescription(sessionCreateRequest.getDescription());
        section.setSessionType(sessionCreateRequest.getSessionType());
        
        // Auto-increment position: find max position for this course
        Long maxPosition = sectionRepository.findTopByCourse_CourseIdOrderByPositionDesc(course.getCourseId())
            .map(Section::getPosition)
            .orElse(0L);
        section.setPosition(maxPosition + 1);
        
        // Save the section first to get the ID
        Section savedSection = sectionRepository.save(section);
        
        // Create contents if provided
        List<SessionCreateResponse.ContentResponse> contentResponses = new ArrayList<>();
        if (sessionCreateRequest.getContents() != null && !sessionCreateRequest.getContents().isEmpty()) {
            for (int i = 0; i < sessionCreateRequest.getContents().size(); i++) {
                SessionCreateRequest.SessionContentRequest contentRequest = sessionCreateRequest.getContents().get(i);
                
                Content content = new Content();
                content.setSection(savedSection);
                content.setType(contentRequest.getContentType());
                content.setContent(contentRequest.getContent());
                content.setPosition((long) (i + 1));
                
                Content savedContent = contentRepository.save(content);
                
                SessionCreateResponse.ContentResponse contentResponse = SessionCreateResponse.ContentResponse.builder()
                    .contentId(savedContent.getId())
                    .contentType(savedContent.getType().name())
                    .content(savedContent.getContent())
                    .position(savedContent.getPosition())
                    .build();
                    
                contentResponses.add(contentResponse);
            }
        }
        
        // Build and return response
        return SessionCreateResponse.builder()
            .sectionId(savedSection.getSectionId())
            .sectionName(savedSection.getSectionName())
            .title(savedSection.getTitle())
            .description(savedSection.getDescription())
            .sessionType(savedSection.getSessionType())
            .position(savedSection.getPosition())
            .contents(contentResponses)
            .build();
    }

    @Override
    @Transactional
    public SessionCreateResponse createSessionWithFiles(SessionCreateWithFilesRequest sessionCreateRequest) {
        // Find the course
        Course course = courseRepository.findById(sessionCreateRequest.getCourseId())
            .orElseThrow(() -> new NotFoundException(
                "Course has not existed with id " + sessionCreateRequest.getCourseId()));

        // Create the section (session)
        Section section = new Section();
        section.setCourse(course);
        section.setSectionName(sessionCreateRequest.getSectionName());
        section.setTitle(sessionCreateRequest.getTitle());
        section.setDescription(sessionCreateRequest.getDescription());
        section.setSessionType(sessionCreateRequest.getSessionType());
        
        // Auto-increment position: find max position for this course
        Long maxPosition = sectionRepository.findTopByCourse_CourseIdOrderByPositionDesc(course.getCourseId())
            .map(Section::getPosition)
            .orElse(0L);
        section.setPosition(maxPosition + 1);
        
        // Save the section first to get the ID
        Section savedSection = sectionRepository.save(section);
        
        // Parse content items and organize them
        List<SessionContentItem> allContentItems = parseSessionContent(sessionCreateRequest);
        
        // Sort content items by position
        allContentItems.sort((a, b) -> Long.compare(a.getPosition(), b.getPosition()));
        
        // Create contents
        List<SessionCreateResponse.ContentResponse> contentResponses = new ArrayList<>();
        for (SessionContentItem contentItem : allContentItems) {
            Content content = new Content();
            content.setSection(savedSection);
            content.setType(contentItem.getContentType());
            content.setPosition(contentItem.getPosition());
            
            if (contentItem.isFileContent()) {
                // Handle file upload
                content.setContent(""); // Will be updated by async upload
                Content savedContent = contentRepository.save(content);
                
                try {
                    byte[] fileBytes = contentItem.getFile().getBytes();
                    if (contentItem.getContentType() == ContentType.VIDEO) {
                        FileValidation.validateVideoType(contentItem.getFile().getOriginalFilename());
                    }
                    // Upload file asynchronously
                    fileAsyncUtil.uploadFileAsync(savedContent.getId(), fileBytes);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to process uploaded file", e);
                }
                
                SessionCreateResponse.ContentResponse contentResponse = SessionCreateResponse.ContentResponse.builder()
                    .contentId(savedContent.getId())
                    .contentType(savedContent.getType().name())
                    .content("File uploaded - URL will be available shortly")
                    .position(savedContent.getPosition())
                    .build();
                    
                contentResponses.add(contentResponse);
                
            } else if (contentItem.isTextContent()) {
                // Handle text content
                content.setContent(contentItem.getTextContent());
                Content savedContent = contentRepository.save(content);
                
                SessionCreateResponse.ContentResponse contentResponse = SessionCreateResponse.ContentResponse.builder()
                    .contentId(savedContent.getId())
                    .contentType(savedContent.getType().name())
                    .content(savedContent.getContent())
                    .position(savedContent.getPosition())
                    .build();
                    
                contentResponses.add(contentResponse);
            }
        }
        
        // Build and return response
        return SessionCreateResponse.builder()
            .sectionId(savedSection.getSectionId())
            .sectionName(savedSection.getSectionName())
            .title(savedSection.getTitle())
            .description(savedSection.getDescription())
            .sessionType(savedSection.getSessionType())
            .position(savedSection.getPosition())
            .contents(contentResponses)
            .build();
    }

    private List<SessionContentItem> parseSessionContent(SessionCreateWithFilesRequest request) {
        List<SessionContentItem> contentItems = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            // Parse text contents
            if (request.getTextContents() != null && !request.getTextContents().trim().isEmpty()) {
                List<SessionCreateRequest.SessionContentRequest> textContents = 
                    objectMapper.readValue(request.getTextContents(), new TypeReference<List<SessionCreateRequest.SessionContentRequest>>() {});
                
                for (int i = 0; i < textContents.size(); i++) {
                    SessionCreateRequest.SessionContentRequest textContent = textContents.get(i);
                    contentItems.add(SessionContentItem.builder()
                        .contentType(textContent.getContentType())
                        .textContent(textContent.getContent())
                        .position((long) (i + 1))
                        .build());
                }
            }
            
            // Parse file positions to know where to place files
            List<Long> filePositions = new ArrayList<>();
            if (request.getFilePositions() != null && !request.getFilePositions().trim().isEmpty()) {
                filePositions = objectMapper.readValue(request.getFilePositions(), new TypeReference<List<Long>>() {});
            }
            
            // Add image files
            if (request.getImageFiles() != null) {
                for (int i = 0; i < request.getImageFiles().size(); i++) {
                    Long position = i < filePositions.size() ? filePositions.get(i) : (long) (contentItems.size() + i + 1);
                    contentItems.add(SessionContentItem.builder()
                        .contentType(ContentType.IMAGE)
                        .file(request.getImageFiles().get(i))
                        .position(position)
                        .build());
                }
            }
            
            // Add video files
            if (request.getVideoFiles() != null) {
                int imageFileCount = request.getImageFiles() != null ? request.getImageFiles().size() : 0;
                for (int i = 0; i < request.getVideoFiles().size(); i++) {
                    Long position = (imageFileCount + i) < filePositions.size() ? 
                        filePositions.get(imageFileCount + i) : (long) (contentItems.size() + i + 1);
                    contentItems.add(SessionContentItem.builder()
                        .contentType(ContentType.VIDEO)
                        .file(request.getVideoFiles().get(i))
                        .position(position)
                        .build());
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse session content", e);
        }
        
        return contentItems;
    }

    @Override
    public SectionCreateResponse updateSection(Long sectionId, SectionRequest sectionRequest) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException(
                        "Section not found with id " + sectionId));

        section.setSectionName(sectionRequest.getSectionName());
        SectionCreateResponse sectionUpdateResponse = sectionMapper.toResponse(sectionRepository.save(section));
        return sectionUpdateResponse;
    }

    @Override
    public void checkCourseRequest(CourseRequest courseRequest, BindingResult bindingResult) {
        // Get id of course
        if (findById(courseRequest.getCourseId()) == null) {
            throw new NotFoundException("Course has not existed with id " + courseRequest.getCourseId());
        }
        // Validator to check category and instructor of course
        courseValidator.validate(courseRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new NotFoundException(bindingResult.getFieldError().getDefaultMessage());
        }

    }

    @Override
    public CourseResponse update(CourseRequest courseRequest, BindingResult bindingResult) {
        try {
            // call method check course
            checkCourseRequest(courseRequest, bindingResult);
            // set category entity to course
            Course course = courseMapper.toRequest(courseRequest);
            Category category = categoryService.findById(courseRequest.getCategoryId());
            course.setCategory(category);
            // set instructor entity to course
            Instructor instructor = instructorRepository.findById(courseRequest.getInstructorId()).orElse(null);
            if (instructor == null) {
                throw new NotFoundException("instructor not found with id " + courseRequest.getInstructorId());
            }
            course.setInstructor(instructor);
            // Save update course
            courseRepository.save(course);
            // Mapping course to courseResponse
            CourseResponse courseResponse = courseMapper.toResponse(course);
            return courseResponse;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public Page<CourseSearchResponse> searchCourses(
            Long instructorId,
            Long categoryId,
            String title,
            Long minprice,
            Long maxprice,
            Boolean isFree,
            Pageable pageable) {
        // TODO Auto-generated method stub
        Specification<Course> spec = Specification.where(CourseSpecifications.hasStatus(CourseStatus.APPROVED));
        if (instructorId != null) {
            spec = spec.and(CourseSpecifications.hasInstructorId(instructorId));
        }

        if (categoryId != null) {
            spec = spec.and(CourseSpecifications.hasCategoryId(categoryId));
        }

        if (title != null) {
            spec = spec.and(CourseSpecifications.hasTitleLike(title));
        }

        if (minprice != null) {
            spec = spec.and(CourseSpecifications.hasPriceGreaterThanOrEqualTo(minprice));
        }
        if (maxprice != null) {
            spec = spec.and(CourseSpecifications.hasPriceLowerThanOrEqualTo(maxprice));
        }
        if (isFree != null) {
            spec = spec.and(CourseSpecifications.isFree(isFree));
        }
        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        return coursePage.map(courseMapper::toCourseSearchResponse);
    }

    @Transactional
    @Override
    public Page<CourseDetailResponse2> getCoursebyInstructorId(Long id, Pageable pageable) {
        Page<Course> courses = courseRepository.findByInstructorUserId(id, pageable);
        return courses.map(this::convertToCourseDetailResponse);
    }

    @Override
    public CourseDetailResponse getCourseDetails(Long courseId) {
        Course course = courseRepository.findWithSectionsByCourseId(courseId);
        if (course == null) {
            throw new NotFoundException("Course not found with id " + courseId);
        }
        CourseDetailResponse courseDetailResponse = courseMapper.toDetailResponse(course);
        return courseDetailResponse;
    }

    private CourseDetailResponse2 convertToCourseDetailResponse(Course course) {
        return CourseDetailResponse2.builder()
                .courseId(course.getCourseId())
                .courseThumbnail(course.getCourseThumbnail())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .categoryId(course.getCategory().getCategoryId())
                .studentList(course.getEnrollment().stream()
                        .map(e -> convertToStudentResponse(e.getStudent()))
                        .collect(Collectors.toList()))
                .createDate(course.getCreatedAt().toLocalDate())
                .status("") // Assuming you want an empty string as default
                .build();
    }

    private StudentResponse convertToStudentResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setStudentId(student.getUserId().intValue());
        response.setName(student.getName());
        return response;
    }
    // @Override
    // public ContentCreateResponse updateContent(Long id, ContentUpdateRequest
    // contentUpdateRequest) {
    // Content content = contentRepository.findById(contentUpdateRequest.getId())
    // .orElseThrow(() -> new ApplicationException("Content not found"));
    // content = contentMapper.toEntity(contentUpdateRequest);
    // content = contentRepository.save(content);
    // return contentMapper.toResponse(content);
    // }

    @Override
    public List<ContentCreateResponse> updateContentPositions(Long id,
            List<ContentUpdatePositionRequest> positionUpdates) {
        try {
            Section section = sectionRepository.findById(id)
                    .orElseThrow(() -> new ApplicationException("Section not found with id: " + id));

            List<Content> updatedContents = new ArrayList<>();
            for (ContentUpdatePositionRequest update : positionUpdates) {
                Content content = contentRepository.findById(update.getContentId())
                        .orElseThrow(() -> new ApplicationException("Content not found"));

                content.setPosition(update.getNewPosition());
                updatedContents.add(content);
                contentRepository.save(content);
            }
            updatedContents.sort(Comparator.comparingLong(Content::getPosition));
            boolean needsAdjustment = false;
            for (int i = 0; i < updatedContents.size() - 1; i++) {
                if (updatedContents.get(i).getPosition() == updatedContents.get(i + 1).getPosition()) {
                    throw new ApplicationException("Position is invalid");
                }
            }
            for (int i = 0; i < updatedContents.size(); i++) {
                if (updatedContents.get(i).getPosition() != i + 1) {
                    needsAdjustment = true;
                    break;
                }
            }

            if (needsAdjustment) {
                for (int i = 0; i < updatedContents.size(); i++) {
                    Content content = updatedContents.get(i);
                    content.setPosition((long) (i + 1));
                    contentRepository.save(content);
                }
            }

            List<ContentCreateResponse> responseList = new ArrayList<>();
            for (Content content : updatedContents) {
                ContentCreateResponse response = contentMapper.toResponse(content);
                responseList.add(response);
            }

            return responseList;
            // return updatedContents.stream()
            // .map(contentMapper::toResponse)
            // .collect(Collectors.toList());
        } catch (ApplicationException ex) {
            throw ex;
        }
    }

    @Override
    public CourseStatusResponse updateCourseStatus(Long courseId, String status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        switch (status) {
            case "CREATED":
                course.setStatus(CourseStatus.CREATED);
                break;
            case "PENDING_APPROVAL":
                course.setStatus(CourseStatus.PENDING_APPROVAL);
                break;
            case "APPROVED":
                course.setStatus(CourseStatus.APPROVED);
                break;
            case "REJECTED":
                course.setStatus(CourseStatus.REJECTED);
                break;
            default:
                throw new ValidationException("Invalid request");
        }
        courseRepository.save(course);
        CourseStatusResponse courseStatusResponse = new CourseStatusResponse();
        courseStatusResponse.setStatus(status);
        courseStatusResponse.setCourseId(courseId);
        return courseStatusResponse;
    }

    @Override
    public List<SectionUpdatePositionRes> updateSectionPositions(Long id,
            List<SectionUpdatePositionRequest> positionUpdates) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ApplicationException("Course not found with id: " + id));

            List<Section> updatedSections = new ArrayList<>();

            for (SectionUpdatePositionRequest update : positionUpdates) {

                Section section = sectionRepository.findById(update.getSectionId())
                        .orElseThrow(
                                () -> new ApplicationException("Section not found with Id " + update.getSectionId()));

                section.setPosition(update.getNewPosition());
                updatedSections.add(section);

                sectionRepository.save(section);
            }
            updatedSections.sort(Comparator.comparingLong(s -> s.getPosition()));
            boolean needsAdjustment = false;
            for (int i = 0; i < updatedSections.size() - 1; i++) {
                if (updatedSections.get(i).getPosition() == updatedSections.get(i + 1).getPosition()) {
                    throw new ApplicationException("Position is invalid");
                }
            }
            for (int i = 0; i < updatedSections.size(); i++) {
                if (updatedSections.get(i).getPosition() != i + 1) {
                    needsAdjustment = true;
                    break;
                }
            }

            if (needsAdjustment) {
                for (int i = 0; i < updatedSections.size(); i++) {
                    Section section = updatedSections.get(i);
                    section.setPosition((long) (i + 1));
                    sectionRepository.save(section);
                }
            }

            List<SectionUpdatePositionRes> responseList = new ArrayList<>();
            for (Section section : updatedSections) {
                SectionUpdatePositionRes response = new SectionUpdatePositionRes();
                response.setSectionId(section.getSectionId());
                response.setTitle(section.getSectionName());
                response.setPosition(section.getPosition());
                List<Content> sortedContents = section.getContents().stream()
                        .sorted(Comparator.comparing(Content::getPosition))
                        .collect(Collectors.toList());
                boolean contentNeedsAdjustment = false;
                for (int i = 0; i < sortedContents.size(); i++) {
                    if (sortedContents.get(i).getPosition() != i + 1) {
                        contentNeedsAdjustment = true;
                        break;
                    }
                }

                if (contentNeedsAdjustment) {
                    for (int i = 0; i < sortedContents.size(); i++) {
                        Content content = sortedContents.get(i);
                        content.setPosition((long) (i + 1));
                        contentRepository.save(content);
                    }
                }
                response.setContentIds(sortedContents.stream()
                        .map(Content::getId)
                        .collect(Collectors.toList()));
                responseList.add(response);
            }

            return responseList;
        } catch (ApplicationException ex) {
            throw ex;
        }
    }

    @Override

    public List<CourseDetailResponse3> unapprovedCourse(Pageable pageable) {
        try {
            List<Course> listCourses = courseRepository.getCourseByIsApproved(CourseStatus.PENDING_APPROVAL.name(),
                    pageable);
            if (listCourses.isEmpty() || listCourses == null) {
                throw new NotFoundException("No course is unapproved");
            }
            List<CourseDetailResponse3> courseDetails = listCourses.stream()
                    .map(courseMapper::coursesToCourseDetailResponse2List)
                    .collect(Collectors.toList());
            return courseDetails;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public SectionDetailResponse2 getContentsBySection(Long sectionId){
        Section section = sectionRepository.findWithContentsBySectionId(sectionId);
        if (section == null) {
            throw new NotFoundException("Section not found with id " + sectionId);
        }
        SectionDetailResponse2 sectionDetailResponse = sectionMapper.toDetailResponse2(section);
        return sectionDetailResponse;
    }

    @Override
    @Transactional
    public void deleteContentById(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(() -> new NotFoundException("content is deleted or doesn't exist"));
        Section section = content.getSection();
        Long deletedPosition = content.getPosition();

        contentRepository.deleteContentById(contentId);
        List<Content> remainingContents = contentRepository.findBySectionOrderByPosition(section);

        // Reorder the remaining contents
        long position = 1;
        for (Content remainingContent : remainingContents) {
            // Skip the deleted content's position
            if (remainingContent.getPosition() > deletedPosition) {
                remainingContent.setPosition(position++);
                contentRepository.save(remainingContent); // Update the position
            } else {
                position++;
            }
        }
    }

    @Override
    @Transactional
    public void deleteSectionById(Long sectionId) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("section is deleted or doesn't exist"));
        Course course = section.getCourse();
        Long deletedPosition = section.getPosition();

        contentRepository.deleteAllContentBySectionId(sectionId);
        sectionRepository.deleteSectionById(sectionId);
        List<Section> remainingSections = sectionRepository.findByCourseOrderByPosition(course);

        // Reorder the remaining contents
        long position = 1;
        for (Section remainingSection : remainingSections) {
            // Skip the deleted content's position
            if (remainingSection.getPosition() > deletedPosition) {
                remainingSection.setPosition(position++);
                sectionRepository.save(remainingSection); // Update the position
            } else {
                position++;
            }
        }
    }

    @Override
    @Transactional
    public void deleteListContent(Long sectionId,ContentDeleteWrapper wrapper){
        List<ContentDeleteRequest> updates = wrapper.getUpdates();
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new NotFoundException("section is deleted or doesn't exist"));
        for (ContentDeleteRequest update : updates) {
            Content content = contentRepository.findById(update.getId()).orElseThrow(() -> new NotFoundException("content is deleted or doesn't exist"));
            contentRepository.deleteContentById(update.getId());
            // try {
            //     cloudinaryService.deleteFile(content.getUrl());
            // } catch (IOException e) {
            //     log.error("Error deleting file from Cloudinary for content ID: " + content.getContentId(), e);
            // }
    
            // contentRepository.deleteById(update.getId());
            Long deletedPosition = content.getPosition();
            List<Content> remainingContents = contentRepository.findBySectionOrderByPosition(section);

            // Reorder the remaining contents
            long position = 1;
            for (Content remainingContent : remainingContents) {
                // Skip the deleted content's position
                if (remainingContent.getPosition() > deletedPosition) {
                    remainingContent.setPosition(position++);
                    contentRepository.save(remainingContent); // Update the position
                } else {
                    position++;
                }
            }
        }
    }
}
