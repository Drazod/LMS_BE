package com.curcus.lms.model.request;

import com.curcus.lms.constants.ContentType;
import com.curcus.lms.constants.SessionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SessionCreateRequest {
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;
    
    @NotEmpty(message = "Section name cannot be empty")
    private String sectionName;
    
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    
    private String description;
    
    @NotNull(message = "Session type cannot be null")
    private SessionType sessionType;
    
    @Valid
    private List<SessionContentRequest> contents;
    
    @Getter
    @Setter
    public static class SessionContentRequest {
        @NotNull(message = "Content type cannot be null")
        private ContentType contentType;
        
        @NotEmpty(message = "Content cannot be empty")
        private String content;
    }
}