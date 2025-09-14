package com.curcus.lms.service;

import java.util.Map;

import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.InstructorStatisticResponse;

public interface InstructorStatisticService {

    Long getTotalrevenue(Long instructorId);

    Long getTotalCourses(Long instructorId);

    Long getTotalUsersBuyedCourses(Long instructorId);

    CourseResponse getTheMostPurchasedCourse(Long instructorId);

    Map<Long, Long> getRevenueStatisticsForYears(Long instructorId, int numberyear);

    Map<Long, Long> getTotalCoursesForYears(Long instructorId, int numberyear);

    InstructorStatisticResponse getStatistic(Long id, int year);


}
