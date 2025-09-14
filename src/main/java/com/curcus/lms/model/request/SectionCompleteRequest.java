package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionCompleteRequest {
    @NotNull
    private Long sectionId;
    @NotNull
    private Long studentId;
}
