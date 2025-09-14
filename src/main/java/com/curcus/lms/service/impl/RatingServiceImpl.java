package com.curcus.lms.service.impl;

import com.curcus.lms.exception.*;
import com.curcus.lms.model.dto.RatingDetailDTO;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.Rating;
import com.curcus.lms.model.entity.Student;
import com.curcus.lms.model.mapper.OthersMapper;
import com.curcus.lms.model.request.RatingRequest;
import com.curcus.lms.model.response.CourseRatingResponse;
import com.curcus.lms.model.response.RatingResponse;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.RatingRepository;
import com.curcus.lms.repository.StudentRepository;
import com.curcus.lms.repository.UserRepository;
import com.curcus.lms.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OthersMapper othersMapper;
    @Autowired
    private StudentRepository studentRepository;
    @Override
    public RatingResponse updateRating(RatingRequest ratingRequest) {
        try {
            if (!userRepository.existsById(ratingRequest.getStudentId()))
                throw new UserNotFoundException("Student not found");
            if (!courseRepository.existsById(ratingRequest.getCourseId()))
                throw new CourseException("Course not found");
            Rating newRating = ratingRepository
                    .findByStudent_UserIdAndCourse_CourseId(
                            ratingRequest.getStudentId(),
                            ratingRequest.getCourseId()
                    );
            if (newRating != null) {
                newRating.setRating(ratingRequest.getRating());
                newRating.setComment(ratingRequest.getComment());
                newRating.setRatingDate(LocalDateTime.now());

                return othersMapper.toRatingResponse(ratingRepository.save(newRating));
            } else
                throw new ApplicationException("This student has not registered this course");

        } catch (ApplicationException e) {
            throw e;
        }
    }

    @Override
    public RatingResponse getRatingByStudentIdAndCourseId(Long studentId, Long courseId) {
        try {
            if (!userRepository.existsById(studentId))
                throw new UserNotFoundException("Student not found");
            if (!courseRepository.existsById(courseId))
                throw new CourseException("Course not found");
            Rating newRating = ratingRepository
                    .findByStudent_UserIdAndCourse_CourseId(
                            studentId,
                            courseId
                    );
            if (newRating == null) {
                throw new EnrollmentException("This student has not registered this course");
            } else
                return othersMapper.toRatingResponse(newRating);
        } catch (ApplicationException e) {
            throw e;
        }
    }

    @Override
    public List<RatingResponse> getRatingByCourseId(Long courseId) {
        try {
            if (!courseRepository.existsById(courseId))
                throw new CourseException("Course not found");
            return othersMapper.toRatingResponseList(ratingRepository.findAllByCourse_CourseId(courseId));
        }
        catch (ApplicationException e) {
            throw e;
        }
    }

    @Override
    public void deleteRatingByStudentIdAndCourseId(Long studentId, Long courseId) {
        try {
            if (!userRepository.existsById(studentId))
                throw new UserNotFoundException("Student not found");
            if (!courseRepository.existsById(courseId))
                throw new CourseException("Course not found");
            Rating newRating = ratingRepository
                    .findByStudent_UserIdAndCourse_CourseId(
                            studentId,
                            courseId
                    );
            if (newRating == null) {
                throw new EnrollmentException("This student has not registered this course");
            } else
                ratingRepository.deleteByStudent_UserIdAndCourse_CourseId(studentId, courseId);
        } catch (ApplicationException e) {
            throw e;
        }
    }

    @Override
    public CourseRatingResponse getCourseRatingsByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new CourseException("Course not found")
        );
        List<Rating> ratings = ratingRepository.findAllByCourse_CourseId(courseId);
        CourseRatingResponse courseRatingResponse = new CourseRatingResponse();

        courseRatingResponse.setTotalRating(course.getTotalRating());
        courseRatingResponse.setAvgRating(course.getAvgRating());

        RatingDetailDTO ratingDetailDTO = new RatingDetailDTO();
        if (ratings != null && !ratings.isEmpty()) {
            for (Rating rating : ratings) {
                switch (rating.getRating().intValue()) {
                    case 1:
                        ratingDetailDTO.setOneStar(ratingDetailDTO.getOneStar()+1);
                        break;
                    case 2:
                        ratingDetailDTO.setTwoStar(ratingDetailDTO.getTwoStar()+1);
                        break;
                    case 3:
                        ratingDetailDTO.setThreeStar(ratingDetailDTO.getThreeStar()+1);
                        break;
                    case 4:
                        ratingDetailDTO.setFourStar(ratingDetailDTO.getFourStar()+1);
                        break;
                    case 5:
                        ratingDetailDTO.setFiveStar(ratingDetailDTO.getFiveStar()+1);
                        break;
                    default:
                        break;
                }
            }
        }
        courseRatingResponse.setRatingDetailDTO(ratingDetailDTO);

        return courseRatingResponse;
    }

    @Override
    public RatingResponse createRating(RatingRequest ratingRequest) {
        if (ratingRepository.existsByStudent_UserIdAndCourse_CourseId(ratingRequest.getStudentId(), ratingRequest.getCourseId())) {
            throw new UniqueConstraintException("Rating already exists");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        Student student = studentRepository.findById(ratingRequest.getStudentId()).orElseThrow();
        Course course = courseRepository.findById(ratingRequest.getCourseId()).orElseThrow();
        Rating rating = new Rating();
        rating.setStudent(student);
        rating.setCourse(course);
        rating.setComment(ratingRequest.getComment());
        rating.setRating(ratingRequest.getRating());
        rating.setRatingDate(currentDateTime);
        return othersMapper.toRatingResponse(ratingRepository.save(rating));
    }
}
