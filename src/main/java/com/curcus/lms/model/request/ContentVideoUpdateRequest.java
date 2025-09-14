package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ContentVideoUpdateRequest {
    @NotNull(message = "Content ID cannot be null")
    private Long contentId;
    @NotNull(message = "File cannot be null")
    private MultipartFile file;
}
