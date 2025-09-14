package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionUpdateRequest {
    @NotNull(message = "Section ID cannot be null")
    private Long sectionId;
    @NotNull(message = "Section name cannot be null")
    private String sectionName;
}
