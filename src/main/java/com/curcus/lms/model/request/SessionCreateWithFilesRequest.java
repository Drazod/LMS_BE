package com.curcus.lms.model.request;

import com.curcus.lms.constants.ContentType;
import com.curcus.lms.constants.SessionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class SessionCreateWithFilesRequest {
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;
    
    @NotEmpty(message = "Section name cannot be empty")
    private String sectionName;
    
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    
    private String description;
    
    @NotNull(message = "Session type cannot be null")
    private SessionType sessionType;
    
    // Text contents (JSON string of array)
    private String textContents;
    
    // File uploads
    private List<MultipartFile> imageFiles;
    private List<MultipartFile> videoFiles;
    
    // File positions (JSON string of array indicating position for each file)
    private String filePositions;
}