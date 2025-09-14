package com.curcus.lms.service.impl;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.curcus.lms.model.response.InstructorStatisticResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.mapper.CourseMapper;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.InstructorRepository;
import com.curcus.lms.repository.OrderItemsRepository;
import com.curcus.lms.service.InstructorStatisticService;

@Service
public class InstructorStatisticServiceImpl implements InstructorStatisticService {
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Override
    public Long getTotalrevenue(Long instructorId) {
        return instructorRepository.getTotalRevenue(instructorId);
    }

    @Override
    public Long getTotalCourses(Long instructorId) {

        return courseRepository.countCoursesOfInstructor(instructorId);
    }

    @Override
    public CourseResponse getTheMostPurchasedCourse(Long instructorId) {
        Pageable pageable = PageRequest.of(0, 1);
        Optional<Course> courseOptional = courseRepository.getTheMostPurchasedCourses(instructorId, pageable).stream().findFirst();

        if (courseOptional.isPresent()) {
            return courseMapper.toResponse(courseOptional.get());
        } else {
            return new CourseResponse();
        }
        // throw new UnsupportedOperationException("Unimplemented method 'getRevenueStatisticsForYears'");
    }


    @Override
    public Map<Long, Long> getRevenueStatisticsForYears(Long instructorId, int numberyear) {
        LocalDate now = LocalDate.now();
        Map<Long, Long> revenueStatisticsForYears = new HashMap<>();
        for (int i = 0; i < numberyear; i++) {
            LocalDate start = now.minusYears(i).with(TemporalAdjusters.firstDayOfYear());
            LocalDate end = now.minusYears(i).with(TemporalAdjusters.lastDayOfYear());
            Long revenue = orderItemsRepository.getRevenueStatisticsForYears(instructorId, start,
                    end);
            if (revenue == null) {
                revenue = 0L;
            }
            revenueStatisticsForYears.put((long) start.getYear(), revenue);
        }
        return revenueStatisticsForYears;
    }

    @Override
    public Map<Long, Long> getTotalCoursesForYears(Long instructorId, int numberyear) {
        LocalDate now = LocalDate.now();
        Map<Long, Long> coursesForYears = new HashMap<>();
        for (int i = 0; i < numberyear; i++) {
            LocalDate start = now.minusYears(i).with(TemporalAdjusters.firstDayOfYear());
            LocalDate end = now.minusYears(i).with(TemporalAdjusters.lastDayOfYear());
            Long revenue = courseRepository.getTotalCoursesForYears(instructorId, start,
                    end);
            if (revenue == null) {
                revenue = 0L;
            }
            coursesForYears.put((long) start.getYear(), revenue);
        }
        return coursesForYears;
    }

    @Override
    public Long getTotalUsersBuyedCourses(Long instructorId) {
        return orderItemsRepository.getTotalUsersBuyedCourses(instructorId);
    }


    @Override
    public InstructorStatisticResponse getStatistic(Long instructorId, int yearCount) {
        InstructorStatisticResponse statisticResponse = new InstructorStatisticResponse();
        statisticResponse.setTotalRevenue(getTotalrevenue(instructorId));
        statisticResponse.setNumberOfCourses(getTotalCourses(instructorId));
        statisticResponse.setTotalUserBoughtCourse(getTotalUsersBuyedCourses(instructorId));
        statisticResponse.setMostPurchasedCourse(getTheMostPurchasedCourse(instructorId));
        statisticResponse.setCourseCountPerYear(getTotalCoursesForYears(instructorId, yearCount));
        statisticResponse.setRevenuePerYear(getRevenueStatisticsForYears(instructorId, yearCount));
        return statisticResponse;
    }

}
