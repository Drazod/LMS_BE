package com.curcus.lms.model.response;

import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import com.curcus.lms.constants.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class ContentCreateResponse {
    private Long id;
    private Long sectionId;
    private ContentType type;
    private String content;
    private Long position;
}
