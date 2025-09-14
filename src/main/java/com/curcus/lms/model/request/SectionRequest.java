package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionRequest {
    @NotNull(message = "Course ID cannot be null")
    private Long CourseId;
    @NotNull(message = "Section name cannot be null")
    private String sectionName;
    @NotNull(message = "Section position cannot be null")
    private Long position;
}
