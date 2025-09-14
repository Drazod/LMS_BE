package com.curcus.lms.model.request;
import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import com.curcus.lms.constants.ContentType;

import jakarta.validation.constraints.NotNull;
@Data
public class ContentUpdateRequest {
    @NotNull(message = "Section ID cannot be null")
    private Long id;
    @NotNull(message = "File cannot be null")
    private MultipartFile file;
}
