package com.curcus.lms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.model.entity.Admin;
import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.entity.Content;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.CourseStatus;
import com.curcus.lms.model.entity.Enrollment;
import com.curcus.lms.model.entity.Instructor;
import com.curcus.lms.model.entity.Section;
import com.curcus.lms.model.mapper.ContentMapper;
import com.curcus.lms.model.mapper.CourseMapper;
import com.curcus.lms.model.mapper.SectionMapper;
import com.curcus.lms.model.request.ContentDocumentCreateRequest;
import com.curcus.lms.model.request.CourseCreateRequest;
import com.curcus.lms.model.request.CourseRequest;
import com.curcus.lms.model.request.SectionRequest;
import com.curcus.lms.model.response.ContentCreateResponse;
import com.curcus.lms.model.response.CourseDetailResponse;
import com.curcus.lms.model.response.CourseDetailResponse2;
import com.curcus.lms.model.response.CourseDetailResponse3;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.CourseSearchResponse;
import com.curcus.lms.model.response.CourseStatusResponse;
import com.curcus.lms.model.response.SectionCreateResponse;
import com.curcus.lms.model.response.StudentResponse;
import com.curcus.lms.repository.AdminRepository;
import com.curcus.lms.repository.ContentRepository;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.InstructorRepository;
import com.curcus.lms.repository.SectionRepository;
import com.curcus.lms.service.impl.CourseServiceImpl;
import com.curcus.lms.util.FileAsyncUtil;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class CourseServiceTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private FileAsyncUtil fileAsyncUtil;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private SectionMapper sectionMapper;

    @Mock
    private SectionRequest sectionRequest;

    @Mock
    private Content content;

    @Mock
    private ContentMapper contentMapper;

    @Mock
    private CategorySevice categoryService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private InstructorRepository instructorRepository;
    Pageable pageable = PageRequest.of(0, 10);
    private CourseRequest courseRequest;
    private Course course;
    private Category category;
    private Instructor instructor;
    private CourseResponse courseResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseRequest = new CourseRequest();
        courseRequest.setCategoryId(1L);
        courseRequest.setInstructorId(1L);

        course = new Course();
        category = new Category();
        instructor = new Instructor();
        courseResponse = new CourseResponse();
    }

    @Test
    void testFindAll() {
        // Prepare test data
        Course course = Course.builder()
                .courseId(1L)
                .title("Test Course")
                .description("Test Description")
                .price(200L)
                .courseThumbnail("thumbnail.png")
                .createdAt(LocalDateTime.now())
                .avgRating(4.5)
                .totalRating(100L)
                .status(CourseStatus.CREATED)
                .instructor(instructor)
                .category(category)
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        List<Course> courses = Collections.singletonList(course);
        Page<Course> coursesPage = new PageImpl<>(courses, pageable, courses.size());

        when(courseRepository.findAll(pageable)).thenReturn(coursesPage);
        when(courseMapper.toCourseSearchResponse(course)).thenReturn(new CourseSearchResponse());

        Page<CourseSearchResponse> responsePage = courseService.findAll(pageable);

        // Verify interactions
        verify(courseRepository).findAll(pageable);
        verify(courseMapper).toCourseSearchResponse(course);

        // Assert the response

        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Test Course", responsePage.getContent().get(0).getTitle());
        assertEquals("thumbnail.png", responsePage.getContent().get(0).getCourseThumbnail());
        assertEquals(4.5, responsePage.getContent().get(0).getAvgRating());
        assertEquals(100L, responsePage.getContent().get(0).getTotalReviews());
        assertEquals("Category Name", responsePage.getContent().get(0).getCategoryName());
        assertEquals(200L, responsePage.getContent().get(0).getPrePrice());
        assertEquals(150L, responsePage.getContent().get(0).getAftPrice());
        assertNotNull(responsePage);

    }

    @Test
    void testCreateSection() {
        // Prepare test data
        SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setCourseId(1L);
        sectionRequest.setSectionName("Introduction");
        sectionRequest.setPosition(1L);

        Course course = new Course();
        course.setCourseId(1L);

        Section section = new Section();
        section.setCourse(course);
        section.setSectionName("Introduction");
        section.setPosition(1L);

        SectionCreateResponse sectionCreateResponse = new SectionCreateResponse();
        sectionCreateResponse.setCourseId(1L);
        sectionCreateResponse.setSectionName("Introduction");
        sectionCreateResponse.setPosition(1L);

        when(courseRepository.findById(sectionRequest.getCourseId())).thenReturn(Optional.of(course));
        when(sectionRepository.save(any(Section.class))).thenReturn(section);
        when(sectionMapper.toResponse(any(Section.class))).thenReturn(sectionCreateResponse);

        SectionCreateResponse response = courseService.createSection(sectionRequest);

        assertEquals(sectionCreateResponse, response);
        verify(courseRepository).findById(sectionRequest.getCourseId());
        verify(sectionRepository).save(any(Section.class));
        verify(sectionMapper).toResponse(any(Section.class));
        assertNotNull(response);
    }

    @Test
    void testSearchCourses() {
        Long instructorId = 1L;
        Long categoryId = 2L;
        String title = "Test Course";
        Long minprice = 100L;
        Long maxprice = 100L;
        Boolean isFree = false;
        Pageable pageable = PageRequest.of(0, 10);

        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Test Course");

        Page<Course> coursePage = new PageImpl<>(Collections.singletonList(course));

        when(courseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(coursePage);
        when(courseMapper.toCourseSearchResponse(any(Course.class))).thenReturn(new CourseSearchResponse());

        Page<CourseSearchResponse> result = courseService.searchCourses(instructorId, categoryId, title, minprice,
                maxprice, isFree, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(courseRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(courseMapper, times(1)).toCourseSearchResponse(any(Course.class));
    }

    @Test
    void testSaveDocumentContent() {
        ContentDocumentCreateRequest contentCreateRequest = new ContentDocumentCreateRequest();
        contentCreateRequest.setSectionId(1L);
        contentCreateRequest.setContent("Test content");

        Content content = new Content();
        content.setId(1L);
        content.setContent("Test content");

        ContentCreateResponse contentCreateResponse = new ContentCreateResponse();
        contentCreateResponse.setId(1L);
        contentCreateResponse.setSectionId(1L);
        contentCreateResponse.setContent("Test content");

        when(contentMapper.toEntity(any(ContentDocumentCreateRequest.class))).thenReturn(content);
        when(contentRepository.save(any(Content.class))).thenReturn(content);
        when(contentMapper.toResponse(any(Content.class))).thenReturn(contentCreateResponse);

        ContentCreateResponse result = courseService.saveDocumentContent(contentCreateRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSectionId());
        assertEquals("Test content", result.getContent());
        verify(contentMapper, times(1)).toEntity(any(ContentDocumentCreateRequest.class));
        verify(contentRepository, times(1)).save(any(Content.class));
        verify(contentMapper, times(1)).toResponse(any(Content.class));
    }

    @Test
    void testFindByIdInstructor() {
        Long id = 1L;
        Instructor instructor = new Instructor();
        when(instructorRepository.findById(id)).thenReturn(Optional.of(instructor));

        Instructor foundInstructor = courseService.findByIdInstructor(id);

        assertNotNull(foundInstructor);
        assertEquals(instructor, foundInstructor);
    }

    @Test
    void testGetCourseDetails() {
        Instructor instructor = new Instructor();
        instructor.setUserId(1L);
        Category category = new Category();
        category.setCategoryId(1L);

        Course course = Course.builder()
                .courseId(1L)
                .courseThumbnail("thumbnail.jpg")
                .title("Course Title")
                .description("Course Description")
                .price(100L)
                .createdAt(LocalDateTime.now())
                .avgRating(4.5)
                .instructor(instructor)
                .category(category)
                .build();

        CourseDetailResponse courseDetailResponse = new CourseDetailResponse();
        courseDetailResponse.setCourseThumbnail("thumbnail.jpg");
        courseDetailResponse.setTitle("Course Title");
        courseDetailResponse.setDescription("Course Description");
        courseDetailResponse.setPrice(100L);
        courseDetailResponse.setCreatedAt(LocalDateTime.now());
        courseDetailResponse.setAvgRating(4.5);

        Long courseId = 1L;

        // Course not found

        when(courseRepository.findWithSectionsByCourseId(courseId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            courseService.getCourseDetails(courseId);
        });

        verify(courseRepository, times(1)).findWithSectionsByCourseId(courseId);
        verify(courseMapper, never()).toDetailResponse(any());

        // Course is found
        reset(courseRepository, courseMapper);

        when(courseRepository.findWithSectionsByCourseId(courseId)).thenReturn(course);
        when(courseMapper.toDetailResponse(course)).thenReturn(courseDetailResponse);

        CourseDetailResponse result = courseService.getCourseDetails(courseId);

        assertNotNull(result);
        assertEquals("Course Title", result.getTitle());
        verify(courseRepository, times(1)).findWithSectionsByCourseId(courseId);
        verify(courseMapper, times(1)).toDetailResponse(course);

    }

    // @Test
    // void testGetCoursebyInstructorId() {
    // Instructor instructor = new Instructor();
    // instructor.setUserId(1L);
    // Category category = new Category();
    // category.setCategoryId(1L);

    // Course course = Course.builder()
    // .courseId(1L)
    // .courseThumbnail("thumbnail.jpg")
    // .title("Course Title")
    // .description("Course Description")
    // .price(100L)
    // .createdAt(LocalDateTime.now())
    // .avgRating(4.5)
    // .instructor(instructor)
    // .category(category)
    // .build();

    // StudentResponse studentResponse = new StudentResponse();
    // // Initialize studentResponse fields

    // CourseDetailResponse2 courseDetailResponse2 = CourseDetailResponse2.builder()
    // .courseId(1L)
    // .courseThumbnail("thumbnail.jpg")
    // .title("Course Title")
    // .description("Course Description")
    // .price(100L)
    // .categoryId(1L)
    // .studentList(Arrays.asList(studentResponse))
    // .createDate(LocalDate.now())
    // .status("Active")
    // .build();

    // Long instructorId = 1L;
    // Pageable pageable = PageRequest.of(0, 10);
    // List<Course> coursesList = Arrays.asList(course);
    // Page<Course> coursesPage = new PageImpl<>(coursesList, pageable,
    // coursesList.size());

    // when(courseRepository.findByInstructorUserId(instructorId,
    // pageable)).thenReturn(coursesPage);
    // when(courseService.convertToCourseDetailResponse(any(Course.class))).thenReturn(courseDetailResponse2);

    // Page<CourseDetailResponse2> result =
    // courseService.getCoursebyInstructorId(instructorId, pageable);

    // assertNotNull(result);
    // assertEquals(1, result.getTotalElements());
    // assertEquals("Course Title", result.getContent().get(0).getTitle());
    // verify(courseRepository, times(1)).findByInstructorUserId(instructorId,
    // pageable);
    // verify(courseService,
    // times(1)).convertToCourseDetailResponse(any(Course.class));
    // }
    @Test
    @Transactional
    public void testDeleteCourse() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setTitle("Test Course");
        course.setEnrollment(Set.of());
        // Scenario 1: Successful Deletion
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseMapper.toResponse(course)).thenReturn(new CourseResponse());

        CourseResponse response = courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
        verify(courseMapper).toResponse(course);
        assertNotNull(response);

        // Reset the mocks to test the next scenario
        reset(courseRepository, courseMapper);

        // Scenario 2: Course Not Found
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception notFoundException = assertThrows(NotFoundException.class,
                () -> courseService.deleteCourse(1L));
        assertEquals("Course has not existed with id 1", notFoundException.getMessage());

        // Reset the mocks to test the next scenario
        reset(courseRepository, courseMapper);

        // Scenario 3: Enrollment Not Empty
        Enrollment enrollment = new Enrollment();
        course.setEnrollment(Set.of(enrollment));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Exception validationException = assertThrows(ValidationException.class,
                () -> courseService.deleteCourse(1L));
        assertEquals("The course cannot be deleted because someone is currently enrolled",
                validationException.getMessage());
    }

    @Test
    void testSaveCourse() {
        // Prepare test data
        MockMultipartFile file = new MockMultipartFile("file", "filename.png", MediaType.IMAGE_PNG_VALUE,
                "some-image".getBytes());
        CourseCreateRequest request = new CourseCreateRequest("Test Title", "Test Description", 100L, 1L, 2L);
        request.setCourseThumbnail(file);

        Course course = new Course();
        // course.setCourseId(2L);

        when(courseMapper.toEntity(any(CourseCreateRequest.class))).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toResponse(any(Course.class))).thenReturn(new CourseResponse());

        // Execute the method under test
        CourseResponse response = courseService.saveCourse(request);

        // Verify interactions
        verify(fileAsyncUtil).validImage(file);
        verify(courseRepository).save(any(Course.class));
        verify(fileAsyncUtil).uploadImageAsync(course.getCourseId(), file);
        verify(courseMapper).toResponse(course);

        // Assert the response (you can add more assertions as needed)
        assertNotNull(response);
    }

    @Test
    public void testFindByCategory_Success() {
        // Arrange
        Category category = new Category();
        category.setCategoryId(1L);

        Course course = new Course();
        Page<Course> coursesPage = new PageImpl<>(Collections.singletonList(course));

        CourseSearchResponse response = new CourseSearchResponse();

        when(courseRepository.findByCategory(any(Category.class), eq(pageable))).thenReturn(coursesPage);
        when(courseMapper.toCourseSearchResponse(any(Course.class))).thenReturn(response);

        // Act
        Page<CourseSearchResponse> result = courseService.findByCategory(1L, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(response, result.getContent().get(0));

        verify(courseRepository, times(1)).findByCategory(any(Category.class), eq(pageable));
        verify(courseMapper, times(1)).toCourseSearchResponse(any(Course.class));
    }

    @Test
    public void testFindByCategory_Exception() {
        // Arrange
        when(courseRepository.findByCategory(any(Category.class), eq(pageable))).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(ApplicationException.class, () -> courseService.findByCategory(1L, pageable));

        verify(courseRepository, times(1)).findByCategory(any(Category.class), eq(pageable));
        verify(courseMapper, never()).toCourseSearchResponse(any(Course.class));
    }

    @Test
    public void testUpdateSection() {
        Section section = new Section();
        section.setSectionId(1L);
        section.setSectionName("Original Name");

        SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setSectionName("Updated Name");

        SectionCreateResponse sectionCreateResponse = new SectionCreateResponse();
        // Arrange
        when(sectionRepository.findById(anyLong()))
                .thenReturn(Optional.of(section))
                .thenReturn(Optional.empty());
        when(sectionRepository.save(any(Section.class))).thenReturn(section);
        when(sectionMapper.toResponse(any(Section.class))).thenReturn(sectionCreateResponse);

        // Act & Assert: Success case
        SectionCreateResponse response = courseService.updateSection(1L, sectionRequest);

        assertNotNull(response);
        assertEquals(sectionCreateResponse, response);

        verify(sectionRepository, times(1)).findById(1L);
        verify(sectionRepository, times(1)).save(section);
        verify(sectionMapper, times(1)).toResponse(section);

        // Act & Assert: Not Found case
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> courseService.updateSection(1L, sectionRequest),
                "Expected updateSection to throw, but it didn't");

        assertTrue(thrown.getMessage().contains("Section not found with id 1"));

        verify(sectionRepository, times(2)).findById(1L);
        verify(sectionRepository, times(1)).save(section); // save should still be called only once
        verify(sectionMapper, times(1)).toResponse(section); // toResponse should still be called only once
    }

    @Test
    public void testUpdateCourseStatus() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setStatus(CourseStatus.CREATED);
        // Scenario 1: Course not found
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> courseService.updateCourseStatus(1L, "APPROVED"));

        assertEquals("Course not found", notFoundException.getMessage());
        verify(courseRepository, times(1)).findById(1L);

        // Reset the mock to avoid interaction count issues in subsequent tests
        reset(courseRepository);

        // Scenario 2: Invalid status
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> courseService.updateCourseStatus(1L, "INVALID_STATUS"));

        assertEquals("Invalid request", validationException.getMessage());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, never()).save(course);

        // Reset the mock to avoid interaction count issues in subsequent tests
        reset(courseRepository);

        // Scenario 3: Successful update to APPROVED
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String status = "APPROVED";
        CourseStatusResponse response = courseService.updateCourseStatus(1L, status);

        assertNotNull(response);
        assertEquals(1L, response.getCourseId());
        assertEquals(status, response.getStatus());

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testUnapprovedCourse_Success() {
        Course course = new Course();
        course.setCourseId(1L);
        CourseDetailResponse3 courseDetailResponse3 = new CourseDetailResponse3();
        // Arrange
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.getCourseByIsApproved(anyString(), eq(pageable)))
                .thenReturn(courses);
        when(courseMapper.coursesToCourseDetailResponse2List(any(Course.class)))
                .thenReturn(courseDetailResponse3);

        // Act
        List<CourseDetailResponse3> result = courseService.unapprovedCourse(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseDetailResponse3, result.get(0));

        verify(courseRepository, times(1)).getCourseByIsApproved(anyString(), eq(pageable));
        verify(courseMapper, times(1)).coursesToCourseDetailResponse2List(course);
    }

    @Test
    public void testUnapprovedCourse_NotFound() {
        // Arrange
        when(courseRepository.getCourseByIsApproved(anyString(), eq(pageable)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> courseService.unapprovedCourse(pageable),
                "Expected unapprovedCourse to throw, but it didn't");

        assertTrue(thrown.getMessage().contains("No course is unapproved"));

        verify(courseRepository, times(1)).getCourseByIsApproved(anyString(), eq(pageable));
        verify(courseMapper, never()).coursesToCourseDetailResponse2List(any(Course.class));
    }

    @Test
    public void testUnapprovedCourse_ApplicationException() {
        // Arrange
        when(courseRepository.getCourseByIsApproved(anyString(), eq(pageable)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ApplicationException thrown = assertThrows(
                ApplicationException.class,
                () -> courseService.unapprovedCourse(pageable),
                "Expected unapprovedCourse to throw, but it didn't");

        assertTrue(thrown.getMessage().contains("Database error"));

        verify(courseRepository, times(1)).getCourseByIsApproved(anyString(), eq(pageable));
        verify(courseMapper, never()).coursesToCourseDetailResponse2List(any(Course.class));
    }

}
