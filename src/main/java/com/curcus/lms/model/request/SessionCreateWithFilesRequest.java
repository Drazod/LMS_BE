package com.curcus.lms.model.request;

import com.curcus.lms.constants.ContentType;
import com.curcus.lms.constants.SessionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Schema(
    description = """
    Request to create a new session with mixed content including file uploads.
    This request uses multipart/form-data format and supports uploading images and videos
    along with text content. Content positioning can be customized using the filePositions field.
    """
)
public class SessionCreateWithFilesRequest {
    @NotNull(message = "Course ID cannot be null")
    @Schema(description = "ID of the course to add the session to", example = "1", required = true)
    private Long courseId;
    
    @NotEmpty(message = "Section name cannot be empty")
    @Schema(description = "Name of the section/session", example = "Listening Comprehension 1", required = true)
    private String sectionName;
    
    @NotEmpty(message = "Title cannot be empty")
    @Schema(description = "Title of the session", example = "Introduction to English Listening", required = true)
    private String title;
    
    @Schema(description = "Detailed description of the session", example = "Basic listening exercises with audio and visual aids")
    private String description;
    
    @NotNull(message = "Session type cannot be null")
    @Schema(description = "Type of session", example = "LISTEN", allowableValues = {"LISTEN", "READING", "SPEAKING"}, required = true)
    private SessionType sessionType;
    
    @Schema(description = "JSON string containing array of text content items", 
            example = "[{\"contentType\":\"TEXT\",\"content\":\"Listen to the following audio and answer the questions below.\"},{\"contentType\":\"TEXT\",\"content\":\"What did the speaker mention about the weather?\"}]")
    private String textContents;
    
    @Schema(description = "Array of image files to upload", type = "array", format = "binary")
    private List<MultipartFile> imageFiles;
    
    @Schema(description = "Array of video files to upload", type = "array", format = "binary")
    private List<MultipartFile> videoFiles;
    
    @Schema(description = "JSON string containing array of positions where files should be placed among content", 
            example = "[2, 4, 3]", 
            implementation = String.class)
    private String filePositions;
}