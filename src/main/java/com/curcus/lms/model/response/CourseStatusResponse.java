package com.curcus.lms.model.response;

import lombok.Data;

@Data
public class CourseStatusResponse {
    private Long courseId;
    private String status;
}
