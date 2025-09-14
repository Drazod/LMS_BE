package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotEmpty(message = "CATEGORY IS MANDATORY")
    @NotNull(message = "CATEGORY IS MANDATORY")
    private String categoryName;
}
