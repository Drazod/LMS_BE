package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseStatusRequest {
    @NotNull(message = "courseId is mandatory")
    private Long courseId;
    @NotNull(message = "status is mandatory")
    private String status;
}
