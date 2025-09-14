package com.curcus.lms.model.request;

import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Data
public class ContentVideoCreateRequest {
    @NotNull(message = "Section ID cannot be null")
    private Long sectionId;
    @NotNull(message = "File cannot be null")
    private MultipartFile file;

//    @Min(value = 0, message = "Position must be a positive integer")
//    private Long position;
}