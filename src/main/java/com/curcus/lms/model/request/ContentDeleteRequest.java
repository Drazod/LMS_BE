package com.curcus.lms.model.request;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
@Data
public class ContentDeleteRequest {
    @NotNull(message = "Section ID cannot be null")
    private Long id;
}
