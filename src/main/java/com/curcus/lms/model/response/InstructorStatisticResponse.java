package com.curcus.lms.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class InstructorStatisticResponse {
    private Long totalRevenue;
    private Long numberOfCourses;
    private Long totalUserBoughtCourse;
    private CourseResponse mostPurchasedCourse;
    private Map<Long, Long> courseCountPerYear ;
    private Map<Long, Long> revenuePerYear;
}
