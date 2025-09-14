package com.curcus.lms.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentDocumentUpdateRequest {
    @NotNull(message = "Content ID cannot be null")
    private Long contentId;
    @NotNull(message = "Content cannot be null")
    private String content;
}
